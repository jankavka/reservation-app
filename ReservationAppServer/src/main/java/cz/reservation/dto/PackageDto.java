package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PackageDto(
        Long id,

        List<PlayerDto> players,

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull(message = "Total number of slots must not be null")
        Integer slotTotal,

        Integer slotUsed,

        @NotNull(message = "Date when starts validity of package must not be null")
        LocalDate validFrom,

        @NotNull(message = "Date when ends validity of package must not be null" )
        LocalDate validTo,

        LocalDate generatedAt,

        @NotNull(message = "Pricing rule must not be empty")
        PricingRuleDto pricingRule

) {
}
