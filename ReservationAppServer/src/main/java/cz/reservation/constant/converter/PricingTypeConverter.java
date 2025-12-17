package cz.reservation.constant.converter;

import cz.reservation.constant.PricingType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class PricingTypeConverter implements AttributeConverter<PricingType, String> {
    @Override
    public String convertToDatabaseColumn(PricingType pricingType) {
        if (pricingType == null) {
            return null;
        }
        return pricingType.getCode();
    }

    @Override
    public PricingType convertToEntityAttribute(String code) {
        if(code == null){
            return null;
        }
        return Arrays
                .stream(PricingType.values())
                .filter(pricingType -> pricingType.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
