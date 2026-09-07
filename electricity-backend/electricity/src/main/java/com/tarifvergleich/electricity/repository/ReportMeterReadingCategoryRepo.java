package com.tarifvergleich.electricity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.ReportMeterReadingCategory;

@Repository
public interface ReportMeterReadingCategoryRepo extends JpaRepository<ReportMeterReadingCategory, Integer> {

	Optional<ReportMeterReadingCategory> findByCategoryNameLikeAndAdminAdminId(String categoryName, Integer adminId);
	
	Optional<ReportMeterReadingCategory> findByIdAndAdminAdminId(Integer id, Integer adminId);

	List<ReportMeterReadingCategory> findAllByAdminAdminIdOrderByCategoryNameAsc(Integer adminId);
	
	List<ReportMeterReadingCategory> findAllByAdminAdminIdOrderByIdAsc(Integer adminId);

}
