package cz.reservation.service.serviceinterface;

import cz.reservation.dto.VenueDto;

import java.util.List;

public interface VenueService {

    VenueDto createVenue(VenueDto venueDto);

    VenueDto getVenue(Long id);

    void editVenue(VenueDto venueDto, Long id);

    List<VenueDto> getAllVenues();

    void deleteVenue(Long id);
}
