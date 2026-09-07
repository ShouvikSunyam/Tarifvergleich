package com.tarifvergleich.electricity.service.customer;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tarifvergleich.electricity.dto.CancellationServiceCategoryDto;
import com.tarifvergleich.electricity.dto.EnergySupplierInvoiceCategoryDto;
import com.tarifvergleich.electricity.exception.InternalServerException;
import com.tarifvergleich.electricity.model.CancellationServiceCategory;
import com.tarifvergleich.electricity.model.EnergySupplierInvoiceCategory;
import com.tarifvergleich.electricity.repository.CancellationServiceCategoryRepo;
import com.tarifvergleich.electricity.repository.EnergySupplierInvoiceCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerCategoryService {

    private final EnergySupplierInvoiceCategoryRepository energyInvoiceMessageCategoryRepo;
    private final CancellationServiceCategoryRepo cancellationServiceCategoryRepo;

    public Map<String, Object> fetchInvoiceCategoriesForCustomer(EnergySupplierInvoiceCategoryDto dto) {
        if (dto == null || dto.getAdminId() == null || dto.getAdminId() <= 0) {
            throw new InternalServerException("Admin id missing", HttpStatus.OK);
        }
        List<EnergySupplierInvoiceCategory> categories = energyInvoiceMessageCategoryRepo
                .findAllByAdminAdminIdOrderByIdAsc(dto.getAdminId());
        List<EnergySupplierInvoiceCategoryDto.InvoiceSupplierCategoryAdminResponseDto> response = categories.stream()
                .map(EnergySupplierInvoiceCategoryDto::mapForAdminRes).toList();
        return Map.of("res", true, "data", response);
    }

    public Map<String, Object> fetchCancellationServiceCategoriesForCustomer(CancellationServiceCategoryDto dto) {
        if (dto == null || dto.getAdminId() == null || dto.getAdminId() <= 0) {
            throw new InternalServerException("Admin id missing", HttpStatus.OK);
        }
        List<CancellationServiceCategory> categories = cancellationServiceCategoryRepo
                .findAllByAdminAdminIdOrderByCategoryNameAsc(dto.getAdminId());
        List<CancellationServiceCategoryDto.CancellationServiceCategoryResponseDto> response = categories.stream()
                .map(CancellationServiceCategoryDto::mapForAdmin).toList();
        return Map.of("res", true, "data", response);
    }

}