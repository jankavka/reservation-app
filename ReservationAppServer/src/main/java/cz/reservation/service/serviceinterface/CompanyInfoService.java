package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CompanyInfoDto;

public interface CompanyInfoService {

    CompanyInfoDto setCompanyInfo(CompanyInfoDto companyInfoDto);

    CompanyInfoDto getCompanyInfo();
}
