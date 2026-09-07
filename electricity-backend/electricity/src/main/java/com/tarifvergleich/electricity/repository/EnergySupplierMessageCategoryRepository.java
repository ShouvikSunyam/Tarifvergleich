package com.tarifvergleich.electricity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.EnergySupplierMessageCategory;

@Repository
public interface EnergySupplierMessageCategoryRepository extends JpaRepository<EnergySupplierMessageCategory, Integer> {

	Optional<EnergySupplierMessageCategory> findByIdAndAdminAdminId(Integer id, Integer adminId);

	Optional<EnergySupplierMessageCategory> findByCategoryNameLikeAndAdminAdminId(String categoryName, Integer adminId);

	List<EnergySupplierMessageCategory> findAllByAdminAdminIdOrderByCategoryNameAsc(Integer adminId);
	
	List<EnergySupplierMessageCategory> findAllByAdminAdminIdOrderByIdAsc(Integer adminId);
}
