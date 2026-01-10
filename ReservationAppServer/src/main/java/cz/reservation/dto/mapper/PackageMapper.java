package cz.reservation.dto.mapper;

import cz.reservation.dto.PackageDto;
import cz.reservation.entity.PackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PackageMapper {

    @Mapping(target = "players", ignore = true)
    @Mapping(target = "pricingRuleId", source = "pricingRule.id")
    PackageDto toDto(PackageEntity packageEntity);

    @Mapping(target = "pricingRule", ignore = true)
    PackageEntity toEntity(PackageDto packageDto);

    void updateEntity(@MappingTarget PackageEntity target, PackageDto source);
}
