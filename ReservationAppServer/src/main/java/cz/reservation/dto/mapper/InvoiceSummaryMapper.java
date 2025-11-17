package cz.reservation.dto.mapper;

import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.entity.InvoiceSummaryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface InvoiceSummaryMapper {

    InvoiceSummaryDto toDto(InvoiceSummaryEntity invoiceSummaryEntity);

    InvoiceSummaryEntity toEntity(InvoiceSummaryDto invoiceSummaryDto);
}
