package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CourtDto;

import java.util.List;

public interface CourtService {

    CourtDto createCourt(CourtDto courtDto);

    CourtDto getCourt(Long id);

    List<CourtDto> getAllCourts();

    void deleteCourt(Long id);

    void editCourt(CourtDto courtDto, Long id);
}
