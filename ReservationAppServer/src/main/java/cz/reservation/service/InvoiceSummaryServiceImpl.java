package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.dto.mapper.InvoiceSummaryMapper;
import cz.reservation.dto.mapper.PricingRuleMapper;
import cz.reservation.entity.InvoiceSummaryEntity;
import cz.reservation.entity.PricingRuleEntity;
import cz.reservation.entity.repository.InvoiceSummaryRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.InvoiceSummaryService;
import cz.reservation.service.serviceinterface.PlayerService;
import cz.reservation.service.serviceinterface.PricingRuleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class InvoiceSummaryServiceImpl implements InvoiceSummaryService {

    private final InvoiceSummaryRepository invoiceSummaryRepository;

    private final InvoiceSummaryMapper invoiceSummaryMapper;

    private final UserRepository userRepository;

    private final PlayerService playerService;

    private static final String SERVICE_NAME = "invoice summary";

    private final PricingRuleService pricingRuleService;

    @Override
    @Transactional
    public ResponseEntity<InvoiceSummaryDto> createSummary(InvoiceSummaryDto invoiceSummaryDto) {

        var entityToSave = invoiceSummaryMapper.toEntity(invoiceSummaryDto);
        //var relatedUser = userRepository.getReferenceById(invoiceSummaryDto.user().id());

        entityToSave.setGeneratedAt(LocalDateTime.now());
        setForeignKeys(entityToSave, invoiceSummaryDto);
        InvoiceSummaryEntity savedEntity = invoiceSummaryRepository.save(entityToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invoiceSummaryMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<InvoiceSummaryDto> getSummary(Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invoiceSummaryMapper.toDto(invoiceSummaryRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional
    public ResponseEntity<InvoiceSummaryDto> editSummary(InvoiceSummaryDto invoiceSummaryDto, Long id) {

        var entityToSave = invoiceSummaryMapper.toEntity(invoiceSummaryDto);
        setForeignKeys(entityToSave, invoiceSummaryDto);
        entityToSave.setId(id);
        var savedEntity = invoiceSummaryRepository.save(entityToSave);
        return ResponseEntity.status(HttpStatus.OK).body(invoiceSummaryMapper.toDto(savedEntity));

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
        if (!invoiceSummaryRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            invoiceSummaryRepository.deleteById(id);

            var responseMessage = Map.of(
                    "message", successMessage(SERVICE_NAME, id, EventStatus.DELETED));

            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        }
    }

    private void setForeignKeys(InvoiceSummaryEntity target, InvoiceSummaryDto source) {
        target.setUser(userRepository.getReferenceById(source.user().id()));
    }
}
