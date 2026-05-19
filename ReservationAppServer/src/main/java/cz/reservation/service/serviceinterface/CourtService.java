package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CourtDto;
import cz.reservation.entity.filter.CourtFilter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourtService {

    CourtDto createCourt(CourtDto courtDto, MultipartFile file);

    CourtDto getCourt(Long id);

    List<CourtDto> getAllCourts(CourtFilter courtFilter);

    void deleteCourt(Long id);

    void editCourt(CourtDto courtDto, Long id, MultipartFile file);
}
