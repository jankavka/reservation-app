package cz.reservation.service;

import cz.reservation.dto.VenueDto;
import cz.reservation.dto.mapper.VenueMapper;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.service.annotation.ReadOnlyTransaction;
import cz.reservation.service.exception.PhotoSavingException;
import cz.reservation.service.files.MyFilesUtils;
import cz.reservation.service.serviceinterface.VenueService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    private final VenueMapper venueMapper;

    private final MyFilesUtils filesUtils;

    private static final String SERVICE_NAME = "venue";

    @Value("${venues-photos.path}")
    private String venuesPhotosPath;

    @Value("${venues-photos.resource}")
    private String venuesPhotoResource;

    @Override
    @Transactional
    public VenueDto createVenue(VenueDto venueDto, MultipartFile file) {
        String photoUrl = handleMultipartFile(file);

        var entityToSave = venueMapper.toEntity(venueDto);
        entityToSave.setPhotoUrl(photoUrl);
        var savedEntity = venueRepository.save(entityToSave);
        return venueMapper.toDto(savedEntity);
    }

    @Override
    @ReadOnlyTransaction
    public VenueDto getVenue(Long id) {
        return venueMapper.toDto(venueRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @Transactional
    public void editVenue(VenueDto venueDto, Long id, MultipartFile file) {
        var entityToUpdate = venueRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        if (file != null && !file.isEmpty()) {
            var currentPhotoUrl = entityToUpdate.getPhotoUrl();
            filesUtils.deleteFile(currentPhotoUrl);
            var photoUrl = handleMultipartFile(file);
            entityToUpdate.setPhotoUrl(photoUrl);
        }
        venueMapper.updateEntity(entityToUpdate, venueDto);

    }

    @Override
    @ReadOnlyTransaction
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
        var venue = venueRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        var photoUrl = venue.getPhotoUrl();
        filesUtils.deleteFile(photoUrl);
        venueRepository.deleteById(id);
    }


    /**
     * This helper method handles empty check and content type check
     * for multipart file, prepares file to save and calls savePhotoFile()
     *
     * @param file Multipart file received form client
     * @return url of saved photo url as String
     */
    private String handleMultipartFile(MultipartFile file) {
        String photoUrl = "";
        if (file != null && !file.isEmpty()) {
            var contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("Current content type: {}", contentType);
                throw new PhotoSavingException("Bad file format");
            }

            var fileName = file.getName();
            var fileSuffix = filesUtils.getSuffix(file);
            var fileUrl = venuesPhotosPath + File.separator + fileName + "." + fileSuffix;
            photoUrl = venuesPhotoResource + File.separator + fileName + "." + fileSuffix;
            File outputFile = new File(fileUrl);
            filesUtils.savePhotoFile(file, outputFile);

        }
        return photoUrl;
    }
}
