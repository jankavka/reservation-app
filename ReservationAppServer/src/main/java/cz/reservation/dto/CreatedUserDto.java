package cz.reservation.dto;

import cz.reservation.entity.UserEntity;
import org.springframework.context.ApplicationEvent;

public class CreatedUserDto extends ApplicationEvent {

    public final transient UserEntity createdEntity;

    public CreatedUserDto(Object source, UserEntity createdEntity) {
        super(source);
        this.createdEntity = createdEntity;
    }
}
