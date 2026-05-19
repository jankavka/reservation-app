package cz.reservation.service;

import cz.reservation.dto.CourtDto;
import cz.reservation.dto.mapper.CourtMapper;
import cz.reservation.entity.CourtEntity;
import cz.reservation.entity.filter.CourtFilter;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.entity.repository.specification.CourtSpecification;
import cz.reservation.service.annotation.ReadOnlyTransaction;
import cz.reservation.service.exception.PhotoSavingException;
import cz.reservation.service.files.MyFilesUtils;
import cz.reservation.service.serviceinterface.CourtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;
    private final CourtMapper courtMapper;
    private final VenueRepository venueRepository;
    private final MyFilesUtils filesUtils;

    private static final String SERVICE_NAME = "court";

    @Value("${court-photos.path}")
    private String courtPhotosPath;

    @Value("${court-photos.resource}")
    private String courtResource;

    @Override
    @Transactional
    public CourtDto createCourt(CourtDto courtDto, MultipartFile file) {
        String photoUrl = handleMultipartFile(file);
        var entityToSave = courtMapper.toEntity(courtDto);
        setForeignKeys(entityToSave, courtDto);
        entityToSave.setPhotoUrl(photoUrl);
        var savedEntity = courtRepository.save(entityToSave);
        return courtMapper.toDto(savedEntity);
    }

    @Override
    @ReadOnlyTransaction
    public CourtDto getCourt(Long id) {
        return courtMapper.toDto(courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @ReadOnlyTransaction
    public List<CourtDto> getAllCourts(CourtFilter courtFilter) {
        var spec = new CourtSpecification(courtFilter);
        return courtRepository.findAll(spec).stream()
                .map(courtMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteCourt(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        courtRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void editCourt(CourtDto courtDto, Long id, MultipartFile file) {
        var entityToUpdate = courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        if (file != null && !file.isEmpty()) {
            var currentPhotoUrl = entityToUpdate.getPhotoUrl();
            filesUtils.deleteFile(currentPhotoUrl);
            var photoUrl = handleMultipartFile(file);
            entityToUpdate.setPhotoUrl(photoUrl);

        }
        courtMapper.updateEntity(entityToUpdate, courtDto);
        setForeignKeys(entityToUpdate, courtDto);
    }

    private void setForeignKeys(CourtEntity target, CourtDto source) {
        var venueId = source.venue().id();
        target.setVenue(venueRepository
                .findById(venueId)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                        "venue", venueId))));
    }

    private String handleMultipartFile(MultipartFile file) {
        String photoUrl = "";
        if (file != null && !file.isEmpty()) {
            var contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("Current content type: {}", contentType);
                throw new PhotoSavingException("This content type is not supported. File must be image");
            }

            var fileName = file.getName();
            var fileSuffix = filesUtils.getSuffix(file);
            var fileUrl = courtPhotosPath + File.separator + fileName + "." + fileSuffix;
            photoUrl = courtResource + File.separator + fileName + "." + fileSuffix;
            File outputFile = new File(fileUrl);
            filesUtils.savePhotoFile(file, outputFile);

        }
        return photoUrl;
    }
}
