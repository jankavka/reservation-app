package cz.reservation.dto.mapper;

import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.entity.InvoiceSummaryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface InvoiceSummaryMapper {

    InvoiceSummaryDto toDto(InvoiceSummaryEntity invoiceSummaryEntity);

    InvoiceSummaryEntity toEntity(InvoiceSummaryDto invoiceSummaryDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "player", ignore = true)
    void updateEntity(@MappingTarget InvoiceSummaryEntity target, InvoiceSummaryDto source);
}
