package com.tarifvergleich.electricity.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tarifvergleich.electricity.dto.ReportMeterReadingDto;
import com.tarifvergleich.electricity.dto.ReportMeterReadingDto.ReportMeterReadingResponseForAdminDto;
import com.tarifvergleich.electricity.model.CustomerOrder;
import com.tarifvergleich.electricity.model.ReportMeterReading;
import com.tarifvergleich.electricity.repository.CustomerOrderRepository;
import com.tarifvergleich.electricity.repository.ReportMeterReadingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReportMeterReadingService {

	private final ReportMeterReadingRepository reportMeterReadingRepository;
	private final CustomerOrderRepository customerOrderRepository;

	public List<ReportMeterReadingResponseForAdminDto> getAllMeterReadings() {

		return reportMeterReadingRepository.findAll().stream().map(entity -> convertToDto(entity)).toList();
	}
	
	public List<ReportMeterReadingResponseForAdminDto> getAllMeterReadings(String search) {

	    List<ReportMeterReading> readings;

	    if (search != null && !search.trim().isEmpty()) {
	        readings = reportMeterReadingRepository.searchMeterReadings(search.trim());
	    } else {
	        readings = reportMeterReadingRepository.findAll();
	    }

	    return readings.stream()
	            .map(this::convertToDto)
	            .toList();
	}

	private ReportMeterReadingResponseForAdminDto convertToDto(ReportMeterReading entity) {

		CustomerOrder order = customerOrderRepository.findByOrderId(Long.valueOf(entity.getOrderId())).orElse(null);

		return ReportMeterReadingDto.mapForAdminResponse(entity, order);
	}
}