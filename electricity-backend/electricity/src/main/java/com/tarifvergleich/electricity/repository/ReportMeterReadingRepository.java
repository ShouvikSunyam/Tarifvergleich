package com.tarifvergleich.electricity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.ReportMeterReading;

@Repository
public interface ReportMeterReadingRepository extends JpaRepository<ReportMeterReading, Integer> {

	List<ReportMeterReading> findByDeliveryIdIdIn(List<Integer> deliveryIds);

	@Query("SELECT r FROM ReportMeterReading r WHERE (:search IS NULL OR :search = '' OR LOWER(r.customer.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.customer.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.customer.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.category.categoryName) LIKE LOWER(CONCAT('%', :search, '%')) ) ORDER BY r.id DESC ")
	List<ReportMeterReading> searchMeterReadings(@Param("search") String search);
	
}