package com.tarifvergleich.electricity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tarifvergleich.electricity.model.EnergySupplierInvoiceCategory;

public interface EnergySupplierInvoiceCategoryRepository extends JpaRepository<EnergySupplierInvoiceCategory, Integer> {
    Optional<EnergySupplierInvoiceCategory> findByIdAndAdminAdminId(Integer id, Integer adminId);

    Optional<EnergySupplierInvoiceCategory> findByCategoryNameLikeAndAdminAdminId(String categoryName, Integer adminId);

    List<EnergySupplierInvoiceCategory> findAllByAdminAdminIdOrderByCategoryNameAsc(Integer adminId);
    
    List<EnergySupplierInvoiceCategory> findAllByAdminAdminIdOrderByIdAsc(Integer adminId);

}
