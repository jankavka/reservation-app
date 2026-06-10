package cz.reservation.dto;

import cz.reservation.entity.UserEntity;
import org.springframework.context.ApplicationEvent;

public class CreatedUserByAdminDto extends ApplicationEvent {

    public final transient UserEntity userEntity;

    public CreatedUserByAdminDto(Object source, UserEntity userEntity) {
        super(source);
        this.userEntity = userEntity;
    }
}
