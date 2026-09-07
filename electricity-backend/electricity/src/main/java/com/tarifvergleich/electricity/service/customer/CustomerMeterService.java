package com.tarifvergleich.electricity.service.customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tarifvergleich.electricity.dto.CustomerInvoiceRequestDto;
import com.tarifvergleich.electricity.dto.ReportMeterReadingCategoryDto;
import com.tarifvergleich.electricity.dto.ReportMeterReadingDto;
import com.tarifvergleich.electricity.exception.InternalServerException;
import com.tarifvergleich.electricity.model.Customer;
import com.tarifvergleich.electricity.model.CustomerInvoiceRequest;
import com.tarifvergleich.electricity.model.EnergySupplierInvoiceCategory;
import com.tarifvergleich.electricity.model.ReportMeterReading;
import com.tarifvergleich.electricity.model.ReportMeterReadingCategory;
import com.tarifvergleich.electricity.repository.CustomerConnectionRepository;
import com.tarifvergleich.electricity.repository.CustomerInvoiceRequestRepository;
import com.tarifvergleich.electricity.repository.CustomerRepository;
import com.tarifvergleich.electricity.repository.EnergySupplierInvoiceCategoryRepository;
import com.tarifvergleich.electricity.repository.ReportMeterReadingRepository;
import com.tarifvergleich.electricity.util.FileServiceCustomer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerMeterService {

	private final CustomerInvoiceRequestRepository invoiceRepo;
	private final CustomerConnectionRepository customerConnectionRepository;
	private final ReportMeterReadingRepository reportMeterReadingRepo;
	private final FileServiceCustomer fileServiceCustomer;
	private final CustomerRepository customerRepo;
	private final EnergySupplierInvoiceCategoryRepository energySupplierInvoiceCategoryRepo;
	private final com.tarifvergleich.electricity.repository.ReportMeterReadingCategoryRepo reportMeterReadingCategoryRepo;

	public Map<String, Object> updateMeterDesignation(Long connectionId, String meterDesignation) {

		customerConnectionRepository.updateMeterDesignation(connectionId, meterDesignation);

		return Map.of("res", true, "message", "Meter designation updated successfully");
	}

	public Map<String, Object> submitInvoiceRequest(CustomerInvoiceRequestDto dto) {

		if (dto.getInvoiceCategory() == null || dto.getInvoiceCategory() <= 0)
			throw new InternalServerException("Category not found", HttpStatus.OK);

		EnergySupplierInvoiceCategory invoiceCategory = energySupplierInvoiceCategoryRepo
				.findById(dto.getInvoiceCategory())
				.orElseThrow(() -> new InternalServerException("Category not found", HttpStatus.OK));

		CustomerInvoiceRequest request = CustomerInvoiceRequest.builder().customerId(dto.getCustomerId())
				.connectionId(dto.getConnectionId()).energySupplierInvoiceCategory(invoiceCategory).orderId(dto.getOrderId())
				.deliveryId(dto.getDeliveryId()).message(dto.getMessage()).status(1).createdAt(LocalDateTime.now())
				.build();

		invoiceRepo.save(request);

		return Map.of("res", true, "message", "Invoice request submitted successfully");
	}

	@Transactional
	public Map<String, Object> reportMeterReading(ReportMeterReadingDto dto, MultipartFile[] files) {

		if (dto.getDeliveryId() == null || dto.getDeliveryId() <= 0)
			throw new InternalServerException("Delivery id missing", HttpStatus.OK);

		if (dto.getOrderId() == null || dto.getOrderId() <= 0)
			throw new InternalServerException("Order id missing", HttpStatus.OK);

		if (dto.getConnectionId() == null || dto.getConnectionId() <= 0)
			throw new InternalServerException("Connection id missing", HttpStatus.OK);

		if (dto.getCategory() == null || dto.getCategory().trim().isEmpty())
			throw new InternalServerException("Category missing", HttpStatus.OK);

		if (dto.getReadingDate() == null || dto.getReadingDate().trim().isEmpty())
			throw new InternalServerException("Reading date missing", HttpStatus.OK);

		if (dto.getMeterReading() == null || dto.getMeterReading().trim().isEmpty())
			throw new InternalServerException("Meter reading missing", HttpStatus.OK);

		if (dto.getCustomerId() == null || dto.getCustomerId() <= 0)
			throw new InternalServerException("Customer id missing", HttpStatus.OK);

		if (files == null || files.length == 0)
			throw new InternalServerException("Meter image missing", HttpStatus.OK);

		Customer customer = customerRepo.findById(dto.getCustomerId()).orElseThrow(
				() -> new InternalServerException("Customer not found with this credential", HttpStatus.OK));

		for (MultipartFile file : files) {

			if (file.isEmpty())
				continue;

			ReportMeterReadingCategory meterCat = reportMeterReadingCategoryRepo
					.findByCategoryNameLikeAndAdminAdminId(dto.getCategory().trim().toUpperCase(), dto.getAdminId())
					.orElseThrow(() -> new InternalServerException("Category not found", HttpStatus.OK));

			String filePath = fileServiceCustomer.saveFile(file, "meter-reading");

			ReportMeterReading report = new ReportMeterReading();

			report.setDeliveryId(dto.getDeliveryId());

			report.setOrderId(dto.getOrderId());

			report.setConnectionId(dto.getConnectionId());

			report.setCategory(meterCat);

			report.setReadingDate(dto.getReadingDate());

			report.setMeterReading(dto.getMeterReading());

			report.setImagePath(filePath);

			report.setCustomer(customer);

			report.setStatus(1);

			report.setCreatedAt(LocalDateTime.now());

			reportMeterReadingRepo.save(report);
		}

		return Map.of("res", true, "message", "Meter reading submitted successfully");
	}

	public Map<String, Object> fetchCustomerReportMeterReading(ReportMeterReadingDto categoryDto) {
		if (categoryDto == null || categoryDto.getAdminId() == null || categoryDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		List<ReportMeterReadingCategory> categories = reportMeterReadingCategoryRepo
				.findAllByAdminAdminIdOrderByIdAsc(categoryDto.getAdminId());

		List<ReportMeterReadingCategoryDto.ReportMeterReadingCategoryAdminResponseDto> response = categories.stream()
				.map(ReportMeterReadingCategoryDto::mapForAdmin).toList();
		return Map.of("res", true, "data", response);
	}
}