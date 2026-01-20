package cz.reservation.service;

import cz.reservation.constant.PricingType;
import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.dto.mapper.InvoiceSummaryMapper;
import cz.reservation.entity.InvoiceSummaryEntity;
import cz.reservation.entity.filter.InvoiceSummaryFilter;
import cz.reservation.entity.repository.InvoiceSummaryRepository;
import cz.reservation.entity.repository.specification.InvoiceSummarySpecification;
import cz.reservation.service.exception.InvoiceStorageException;
import cz.reservation.service.invoice.InvoiceEngine;
import cz.reservation.service.pricing.resolver.PricingStrategyResolver;
import cz.reservation.service.serviceinterface.InvoiceSummaryService;
import cz.reservation.service.serviceinterface.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;
import static cz.reservation.service.message.MessageHandling.notNullMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceSummaryServiceImpl implements InvoiceSummaryService {

    private final InvoiceSummaryRepository invoiceSummaryRepository;
    private final InvoiceSummaryMapper invoiceSummaryMapper;
    private final PlayerService playerService;
    private final PricingStrategyResolver pricingStrategyResolver;
    private final InvoiceEngine invoiceEngine;

    private static final String SERVICE_NAME = "invoice summary";

    @Override
    @Transactional
    public InvoiceSummaryDto createSummary(InvoiceSummaryDto invoiceSummaryDto) {
        var entityToSave = invoiceSummaryMapper.toEntity(invoiceSummaryDto);

        setPriceToEntity(entityToSave, invoiceSummaryDto);
        replaceSummaryIfAlreadyExists(invoiceSummaryDto, entityToSave);
        entityToSave.setGeneratedAt(LocalDateTime.now());
        setForeignKeys(entityToSave, invoiceSummaryDto);
        makeInvoicePdf(entityToSave);

        InvoiceSummaryEntity savedEntity = invoiceSummaryRepository.save(entityToSave);
        return invoiceSummaryMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceSummaryDto getSummary(Long id) {
        return invoiceSummaryMapper.toDto(invoiceSummaryRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @Transactional
    public void editSummary(InvoiceSummaryDto invoiceSummaryDto, Long id) {
        var entityToUpdate = invoiceSummaryRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        invoiceSummaryMapper.updateEntity(entityToUpdate, invoiceSummaryDto);
        setForeignKeys(entityToUpdate, invoiceSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceSummaryDto> getAllSummaries(InvoiceSummaryFilter invoiceSummaryFilter) {
        var spec = new InvoiceSummarySpecification(invoiceSummaryFilter);
        return invoiceSummaryRepository
                .findAll(spec)
                .stream()
                .map(invoiceSummaryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceSummaryDto> getAllSummariesByUser(Long userId) {
        return invoiceSummaryRepository
                .findByPlayerId(userId)
                .stream()
                .map(invoiceSummaryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteSummary(Long id) {
        if (!invoiceSummaryRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        invoiceSummaryRepository.deleteById(id);
    }

    private void setForeignKeys(InvoiceSummaryEntity target, InvoiceSummaryDto source) {
        target.setPlayer(playerService.getPlayerEntity(source.player().id()));
    }

    private void setPriceToEntity(InvoiceSummaryEntity target, InvoiceSummaryDto invoiceSummaryDto) {
        var relatedPlayer = playerService.getPlayerEntity(invoiceSummaryDto.player().id());
        var packagee = relatedPlayer.getPackagee();
        var pricingType = relatedPlayer.getPricingType();

        if (pricingType == null) {
            throw new IllegalArgumentException(notNullMessage("Pricing type"));
        }
        if (pricingType.equals(PricingType.PACKAGE) && packagee == null) {
            throw new IllegalArgumentException("Current player doesn't have active package");
        }

        var pricingEngine = pricingStrategyResolver.resolve(pricingType);
        var price = pricingEngine.computePrice(invoiceSummaryDto);
        target.setTotalCentsAmount(price);
    }

    private void replaceSummaryIfAlreadyExists(InvoiceSummaryDto source, InvoiceSummaryEntity target) {
        var existingSummary = invoiceSummaryRepository.getSummaryOfCurrentMonth(source.month().getValue());
        existingSummary.ifPresent(summary -> target.setId(summary.getId()));
    }

    private void makeInvoicePdf(InvoiceSummaryEntity target) {
        if (target.getTotalCentsAmount() == 0) {
            target.setPath(null);
        } else {
            try {
                target.setPath(invoiceEngine.createInvoice(target));
            } catch (IOException e) {
                throw new InvoiceStorageException("An problem occurred during creating pdf invoice");
            }
        }
    }
}
