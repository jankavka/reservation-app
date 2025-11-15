package cz.reservation.constant.converter;

import cz.reservation.constant.BookingStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class BookingStatusConverter implements AttributeConverter<BookingStatus,String> {
    @Override
    public String convertToDatabaseColumn(BookingStatus bookingStatus) {
        if(bookingStatus == null){
            return null;
        }
        return bookingStatus.getCode();
    }

    @Override
    public BookingStatus convertToEntityAttribute(String code) {
        if(code == null){
            return null;
        }
        return Stream.of(BookingStatus.values())
                .filter(status -> status.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
