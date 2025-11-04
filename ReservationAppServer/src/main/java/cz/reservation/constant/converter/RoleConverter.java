package cz.reservation.constant.converter;

import cz.reservation.constant.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null){
            return null;
        }
        return role.getSpec();
    }

    @Override
    public Role convertToEntityAttribute(String spec) {
        if(spec == null){
            return null;
        }
        return Stream.of(Role.values())
                .filter(r -> r.getSpec().equals(spec))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
