package cz.reservation.service;

import cz.reservation.dto.VenueDto;
import cz.reservation.dto.mapper.VenueMapper;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.service.serviceinterface.VenueService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    private final VenueMapper venueMapper;

    private static final String SERVICE_NAME = "venue";

    @Override
    @Transactional
    public VenueDto createVenue(VenueDto venueDto) {
        var entityToSave = venueMapper.toEntity(venueDto);
        var savedEntity = venueRepository.save(entityToSave);
        return venueMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueDto getVenue(Long id) {
        return venueMapper.toDto(venueRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @Transactional
    public void editVenue(VenueDto venueDto, Long id) {
        var entityToUpdate = venueRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        venueMapper.updateEntity(entityToUpdate, venueDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueDto> getAllVenues() {
        return venueRepository
                .findAll()
                .stream()
                .map(venueMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        venueRepository.deleteById(id);
    }
}
