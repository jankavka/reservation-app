package cz.reservation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.reservation.dto.CompanyInfoDto;
import cz.reservation.service.exception.CustomJsonException;
import cz.reservation.service.serviceinterface.CompanyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class CompanyInfoServiceImpl implements CompanyInfoService {

    private final File file;
    private final ObjectMapper objectMapper;

    public CompanyInfoServiceImpl(@Value("${company-info.path}") String companyInfoPath, ObjectMapper objectMapper) {
        this.file = new File(companyInfoPath);
        this.objectMapper = objectMapper;
    }

    @Override
    public synchronized CompanyInfoDto setCompanyInfo(CompanyInfoDto companyInfoDto) {

        try {
            objectMapper.writeValue(file, companyInfoDto);

            return companyInfoDto;
        } catch (IOException e) {
            throw new CustomJsonException("An error occurred during updating company info. " + e.getMessage());
        }

    }

    @Override
    public synchronized CompanyInfoDto getCompanyInfo() {

        try {
            return objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new CustomJsonException("An error occurred during reading company info. " + e.getMessage());

        }


    }
}
