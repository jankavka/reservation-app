package cz.reservation.service.serviceinterface;

import cz.reservation.dto.VenueDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface VenueService {

    ResponseEntity<VenueDto> createVenue(VenueDto venueDto);

    ResponseEntity<VenueDto> getVenue(Long id);

    ResponseEntity<Map<String,String>> editVenue(VenueDto venueDto, Long id);

    ResponseEntity<List<VenueDto>> getAllVenues();

    ResponseEntity<Map<String, String>> deleteVenue(Long id);
}
