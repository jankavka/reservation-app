package cz.reservation.service;

import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.dto.mapper.InvoiceSummaryMapper;
import cz.reservation.entity.InvoiceSummaryEntity;
import cz.reservation.entity.repository.InvoiceSummaryRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.InvoiceSummaryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceSummaryServiceImpl implements InvoiceSummaryService {

    private final InvoiceSummaryRepository invoiceSummaryRepository;

    private final InvoiceSummaryMapper invoiceSummaryMapper;

    private final UserRepository userRepository;

    @Autowired
    public InvoiceSummaryServiceImpl(
            InvoiceSummaryMapper invoiceSummaryMapper,
            InvoiceSummaryRepository invoiceSummaryRepository,
            UserRepository userRepository) {
        this.invoiceSummaryMapper = invoiceSummaryMapper;
        this.invoiceSummaryRepository = invoiceSummaryRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<InvoiceSummaryDto> createSummary(InvoiceSummaryDto invoiceSummaryDto) {
        if (invoiceSummaryDto == null) {
            throw new IllegalArgumentException("Invoice must not be null");

        } else {
            InvoiceSummaryEntity entityToSave = invoiceSummaryMapper.toEntity(invoiceSummaryDto);
            entityToSave.setGeneratedAt(new Date());
            setForeignKeys(entityToSave, invoiceSummaryDto);
            InvoiceSummaryEntity savedEntity = invoiceSummaryRepository.save(entityToSave);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(invoiceSummaryMapper.toDto(savedEntity));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<InvoiceSummaryDto> getSummary(Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invoiceSummaryMapper.toDto(invoiceSummaryRepository
                        .findById(id)
                        .orElseThrow(EntityNotFoundException::new)));
    }

    @Override
    @Transactional
    public ResponseEntity<InvoiceSummaryDto> editSummary(InvoiceSummaryDto invoiceSummaryDto, Long id) {
        if (invoiceSummaryDto != null) {
            InvoiceSummaryEntity entityToSave = invoiceSummaryMapper.toEntity(invoiceSummaryDto);
            setForeignKeys(entityToSave, invoiceSummaryDto);
            entityToSave.setId(id);
            InvoiceSummaryEntity savedEntity = invoiceSummaryRepository.save(entityToSave);
            return ResponseEntity.status(HttpStatus.OK).body(invoiceSummaryMapper.toDto(savedEntity));
        } else {
            throw new EntityNotFoundException("Invoice summary not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<InvoiceSummaryDto>> getAllSummaries() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invoiceSummaryRepository
                        .findAll()
                        .stream()
                        .map(invoiceSummaryMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<InvoiceSummaryDto>> getAllSummariesByUser(Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invoiceSummaryRepository
                        .findByUserId(userId)
                        .stream()
                        .map(invoiceSummaryMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteSummary(Long id) {
        if (invoiceSummaryRepository.existsById(id)) {
            invoiceSummaryRepository.deleteById(id);

            Map<String, String> responseMessage = Map.of(
                    "message", "Invoice summary with id " + id + " was deleted");

            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        } else {
            throw new EntityNotFoundException("Invoice summary not found");
        }
    }

    private void setForeignKeys(InvoiceSummaryEntity target, InvoiceSummaryDto source) {
        target.setUser(userRepository.getReferenceById(source.user().id()));
    }
}
