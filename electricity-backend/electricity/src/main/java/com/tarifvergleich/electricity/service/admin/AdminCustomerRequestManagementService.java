package com.tarifvergleich.electricity.service.admin;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarifvergleich.electricity.dto.CustomerChangeDiscountRequestDto;
import com.tarifvergleich.electricity.dto.CustomerChangeDiscountRequestDto.CustomerChangeDiscountAdminResponseDto;
import com.tarifvergleich.electricity.exception.InternalServerException;
import com.tarifvergleich.electricity.model.CustomerChangeDiscountRequest;
import com.tarifvergleich.electricity.repository.AdminSignatureRepository;
import com.tarifvergleich.electricity.repository.CustomerBookingDocumentRepository;
import com.tarifvergleich.electricity.repository.CustomerChangeDiscountRequestRepository;
import com.tarifvergleich.electricity.repository.CustomerDeliveryRepository;
import com.tarifvergleich.electricity.repository.CustomerOrderRepository;
import com.tarifvergleich.electricity.repository.EnergySupplierMessageRepository;
import com.tarifvergleich.electricity.service.ElectricityComparisonService;
import com.tarifvergleich.electricity.service.EnergyService;
import com.tarifvergleich.electricity.service.customer.CustomerBookingService;
import com.tarifvergleich.electricity.util.EmailBodyRender;
import com.tarifvergleich.electricity.util.FileServiceCustomer;
import com.tarifvergleich.electricity.util.FileServiceSuperAdmin;
import com.tarifvergleich.electricity.util.Helper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminCustomerRequestManagementService {

	private final CustomerChangeDiscountRequestRepository customerChangeDiscountRequestRepo;
	private final CustomerDeliveryRepository customerDeliveryRepo;
	private final EnergySupplierMessageRepository energySupplierMessageRepo;
	private final Helper helper;
	private final ElectricityComparisonService electricityComparisonService;
	private final ObjectMapper objectMapper;
	private final CustomerBookingService customerBookingService;
	private final EnergyService energyService;
	private final FileServiceCustomer fileServiceCustomer;
	private final FileServiceSuperAdmin fileServiceSuperAdmin;
	private final CustomerOrderRepository customerOrderRepo;
	private final CustomerBookingDocumentRepository customerBookingDocumentRepo;
	private final AdminSignatureRepository adminSignatureRepo;
	private final AsyncServiceAdmin asyncServiceAdmin;
	private final ApplicationEventPublisher eventPublisher;
	private final EmailBodyRender emailBodyRender;

	public Map<String, Object> fetchCustomerChangeDiscountRequests(
			CustomerChangeDiscountRequestDto changeDiscountRequestDto) {

		if (changeDiscountRequestDto.getAdminId() == null || changeDiscountRequestDto.getAdminId() <= 0)
			throw new InternalServerException("Admin credential not found", HttpStatus.OK);

		if (changeDiscountRequestDto.getPage() != null && changeDiscountRequestDto.getPage() > 0) {

			if (changeDiscountRequestDto.getSize() == null || changeDiscountRequestDto.getSize() <= 0)
				changeDiscountRequestDto.setSize(10);

			Pageable pageable = PageRequest.of(changeDiscountRequestDto.getPage() - 1,
					changeDiscountRequestDto.getSize());

			Page<CustomerChangeDiscountRequest> requests = customerChangeDiscountRequestRepo
					.findAllByAdminAdminIdOrderByCreatedOnDesc(changeDiscountRequestDto.getAdminId(), pageable);

			Page<CustomerChangeDiscountAdminResponseDto> requestsRep = requests
					.map(CustomerChangeDiscountRequestDto::mapAdminResponse);

			return Map.of("res", true, "data", requestsRep.getContent(), "page",
					requestsRep.getPageable().getPageNumber() + 1, "totalPages", requests.getTotalPages());

		}

		List<CustomerChangeDiscountRequest> requests = customerChangeDiscountRequestRepo
				.findAllByAdminAdminIdOrderByCreatedOnDesc(changeDiscountRequestDto.getAdminId());

		List<CustomerChangeDiscountAdminResponseDto> requestRes = requests.stream()
				.map(CustomerChangeDiscountRequestDto::mapAdminResponse).toList();

		return Map.of("res", true, "data", requestRes);
	}

}
