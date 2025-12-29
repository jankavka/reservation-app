package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CompanyInfoDto;

import java.io.IOException;

public interface CompanyInfoService {

    CompanyInfoDto setCompanyInfo(CompanyInfoDto companyInfoDto);

    CompanyInfoDto getCompanyInfo();
}
