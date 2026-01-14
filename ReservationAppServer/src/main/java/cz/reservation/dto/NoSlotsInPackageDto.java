package cz.reservation.dto;

import cz.reservation.entity.PackageEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NoSlotsInPackageDto extends ApplicationEvent {

    private final transient PackageEntity packageEntity;

    public NoSlotsInPackageDto(Object source, PackageEntity packageEntity) {
        super(source);
        this.packageEntity = packageEntity;
    }
}
