package cz.reservation.service.serviceinterface;

import cz.reservation.dto.VenueDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VenueService {

    VenueDto createVenue(VenueDto venueDto, MultipartFile file);

    VenueDto getVenue(Long id);

    void editVenue(VenueDto venueDto, Long id, MultipartFile file);

    List<VenueDto> getAllVenues();

    void deleteVenue(Long id);
}
