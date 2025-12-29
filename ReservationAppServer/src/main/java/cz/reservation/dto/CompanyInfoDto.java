package cz.reservation.dto;

public record CompanyInfoDto(
        String companyName,
        String address,
        String businessId,
        String taxNumber,
        String email,
        String telNumber,
        String bankAccount,
        String bankAccountInternationalFormat

) {
}
