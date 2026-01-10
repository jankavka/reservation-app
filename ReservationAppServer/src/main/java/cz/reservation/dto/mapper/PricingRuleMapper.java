package cz.reservation.dto.mapper;

import cz.reservation.dto.PricingRuleDto;
import cz.reservation.entity.PricingRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PricingRuleMapper {

    @Mapping(target = "packagee", ignore = true)
    PricingRuleDto toDto(PricingRuleEntity pricingRuleEntity);

    PricingRuleEntity toEntity(PricingRuleDto pricingRulesDto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget PricingRuleEntity target, PricingRuleDto source);
}
