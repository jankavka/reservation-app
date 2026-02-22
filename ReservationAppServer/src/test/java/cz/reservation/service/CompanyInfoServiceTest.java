package cz.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.reservation.dto.CompanyInfoDto;
import cz.reservation.service.exception.CustomJsonException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyInfoServiceTest {

    @TempDir
    Path temp;

    File file;

    private CompanyInfoServiceImpl service(ObjectMapper mapper) {
        Path filePath = temp.resolve("test.json");
        return new CompanyInfoServiceImpl(filePath.toString(), mapper);

    }

    @BeforeEach
    void setup() {
        file = temp.resolve("test.json").toFile();
    }

    @Test
    void shouldUpdateCompanyInfo() {

        CompanyInfoDto infoDto = new CompanyInfoDto("Name", "address",
                "12345", "12345", "email", "12345",
                "12345", "1234567");
        ObjectMapper mapper = new ObjectMapper();
        var myService = service(mapper);

        var result = myService.setCompanyInfo(infoDto);

        assertEquals("address", result.address());
        assertEquals("Name", result.companyName());

    }

    @Test
    void shouldThrowExceptionDuringSavingCompanyInfo() throws IOException {
        CompanyInfoDto infoDto = new CompanyInfoDto(
                "Name", "address", "12345", "12345",
                "email", "12345", "12345", "1234567");

        ObjectMapper mapper = mock(ObjectMapper.class);
        var myService = service(mapper);

        doThrow(CustomJsonException.class).when(mapper).writeValue(file, infoDto);

        var exception = assertThrows(CustomJsonException.class, () -> myService.setCompanyInfo(infoDto));

        assertInstanceOf(CustomJsonException.class, exception);

        verify(mapper).writeValue(file, infoDto);
    }

    @Test
    void shouldGetCompanyInfo() throws IOException {
        CompanyInfoDto infoDto = new CompanyInfoDto(
                "Name", "address", "12345", "12345", "email",
                "12345", "12345", "1234567");
        ObjectMapper mapper = new ObjectMapper();
        CompanyInfoServiceImpl service = service(mapper);
        mapper.writeValue(file, infoDto);

        var result = service.getCompanyInfo();

        assertEquals(infoDto.address(), result.address());
        assertEquals(infoDto.companyName(), result.companyName());

    }

    @Test
    void shouldThrowExceptionWhenNoFile() {

        var mapper = new ObjectMapper();
        var service = service(mapper);

        var exception = assertThrows(CustomJsonException.class, service::getCompanyInfo);
        var exceptionMessage = exception.getMessage().split("[.]")[0];

        assertInstanceOf(CustomJsonException.class, exception);
        assertEquals("An error occurred during reading company info", exceptionMessage);


    }
}
