package cz.reservation.controller;

import cz.reservation.dto.CompanyInfoDto;
import cz.reservation.service.serviceinterface.CompanyInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/company-info")
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;


    @PostMapping("/update")
    public CompanyInfoDto updateInfo(@RequestBody CompanyInfoDto companyInfoDto) {
        return companyInfoService.setCompanyInfo(companyInfoDto);
    }

    @GetMapping
    public CompanyInfoDto getInfo() {
        return companyInfoService.getCompanyInfo();
    }


}
