package com.gov.rwanda.erp.payroll.service;

import com.gov.rwanda.erp.payroll.dto.DeductionRequest;
import com.gov.rwanda.erp.payroll.dto.DeductionResponse;
import com.gov.rwanda.erp.payroll.entity.Deduction;
import com.gov.rwanda.erp.payroll.exception.DuplicateResourceException;
import com.gov.rwanda.erp.payroll.exception.ResourceNotFoundException;
import com.gov.rwanda.erp.payroll.repository.DeductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionService {

    private final DeductionRepository deductionRepository;

    @Transactional
    public DeductionResponse create(DeductionRequest request) {
        deductionRepository.findByNameIgnoreCase(request.getName()).ifPresent(d -> {
            throw new DuplicateResourceException("Deduction already exists: " + request.getName());
        });

        Deduction deduction = Deduction.builder()
                .name(request.getName())
                .percentage(request.getPercentage())
                .category(request.getCategory())
                .active(true)
                .build();

        return toResponse(deductionRepository.save(deduction));
    }

    @Transactional(readOnly = true)
    public List<DeductionResponse> findAll() {
        return deductionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DeductionResponse> findActive() {
        return deductionRepository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DeductionResponse findById(Long id) {
        return toResponse(getDeduction(id));
    }

    @Transactional
    public DeductionResponse update(Long id, DeductionRequest request) {
        Deduction deduction = getDeduction(id);

        deductionRepository.findByNameIgnoreCase(request.getName())
                .filter(d -> !d.getId().equals(id))
                .ifPresent(d -> {
                    throw new DuplicateResourceException("Deduction name already in use: " + request.getName());
                });

        deduction.setName(request.getName());
        deduction.setPercentage(request.getPercentage());
        deduction.setCategory(request.getCategory());

        return toResponse(deductionRepository.save(deduction));
    }

    @Transactional
    public void deactivate(Long id) {
        Deduction deduction = getDeduction(id);
        deduction.setActive(false);
        deductionRepository.save(deduction);
    }

    private Deduction getDeduction(Long id) {
        return deductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction not found with id: " + id));
    }

    private DeductionResponse toResponse(Deduction deduction) {
        return DeductionResponse.builder()
                .id(deduction.getId())
                .name(deduction.getName())
                .percentage(deduction.getPercentage())
                .category(deduction.getCategory())
                .active(deduction.isActive())
                .build();
    }
}
