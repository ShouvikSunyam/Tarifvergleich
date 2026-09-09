package com.tarifvergleich.electricity.controller.customer;

import java.util.Map;

import com.tarifvergleich.electricity.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarifvergleich.electricity.service.customer.CustomerBookingService;
import com.tarifvergleich.electricity.service.customer.CustomerDetailService;
import com.tarifvergleich.electricity.service.customer.CustomerEnergySupplierService;
import com.tarifvergleich.electricity.service.customer.CustomerMeterService;
import com.tarifvergleich.electricity.service.customer.CustomerUpdateService;
import com.tarifvergleich.electricity.service.customer.CustomerCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

	private final CustomerBookingService customerBookingService;
	private final CustomerDetailService customerDetailService;
	private final CustomerUpdateService customerUpdateService;
	private final ObjectMapper objectMapper;
	private final CustomerMeterService customerMeterService;
	private final CustomerEnergySupplierService customerEnergySupplierService;
	private final CustomerCategoryService customerCategoryService;

	@PostMapping("/fetch-customer-detail")
	public ResponseEntity<?> fetchCustomer(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerDetailService.getCustomerDetails(customerDto.getId()));
	}

	@PostMapping("/add-delivery")
	public ResponseEntity<?> addDelivery(@RequestBody CustomerDeliveryRequestWrapper deliveryWrapper) {
		return ResponseEntity.ok(customerBookingService.saveDelivery(deliveryWrapper.getCustomerId(),
				deliveryWrapper.getDeliveryId(), deliveryWrapper.getDeliveryAddress(),
				deliveryWrapper.getBillingAddress(), deliveryWrapper.getProvider()));
	}

	@PostMapping("/add-connection")
	public ResponseEntity<?> addConnection(@RequestBody CustomerConnectWrapper payload) {
		return ResponseEntity.ok(customerBookingService.saveConnection(payload.getCustomerId(), payload.getDeliveryId(),
				payload.getConnectionData()));
	}

	@PostMapping("/add-payment")
	public ResponseEntity<?> addPayment(@RequestBody CustomerPaymentRequestDto paymentDto) {
		return ResponseEntity.ok(customerBookingService.savePayment(paymentDto));
	}

	@PostMapping("/fetch-form")
	public ResponseEntity<?> fetchForm(@RequestBody Map<String, Object> payload) {

		Integer customerId = payload.get("customerId") != null ? (Integer) payload.get("customerId") : 0;
		Integer deliveryId = payload.get("deliveryId") != null ? (Integer) payload.get("deliveryId") : 0;
		Integer step = payload.get("step") != null ? (Integer) payload.get("step") : 0;

		return ResponseEntity.ok(customerBookingService.fetchByStep(customerId, deliveryId, step));
	}

	@PostMapping("/submit-declaration")
	public ResponseEntity<?> submitDeclaration(@RequestBody Map<String, Object> payload) {
		Integer customerId = payload.get("customerId") != null ? (Integer) payload.get("customerId") : 0;
		Integer deliveryId = payload.get("deliveryId") != null ? (Integer) payload.get("deliveryId") : 0;

		return ResponseEntity.ok(customerBookingService.submit(customerId, deliveryId));
	}

	@PostMapping("/add-schedule")
	public ResponseEntity<?> addCustomerSchedule(@RequestBody CustomerContactScheduleRequestDto schedule) {
		return ResponseEntity.ok(customerBookingService.submitCustomerSchedule(schedule));
	}

	@PostMapping(value = "/add-attorny")
	public ResponseEntity<?> addCustomerAttorny(@RequestPart("data") String jsonData,
			@RequestPart(value = "file") MultipartFile file) {

		CustomerAttornyDto attornyDto;
		try {
			attornyDto = objectMapper.readValue(jsonData, CustomerAttornyDto.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (JsonProcessingException e) {
			throw new RuntimeException();
		}
		return ResponseEntity.ok(customerDetailService.submitCustomerAttorny(attornyDto, file));
	}

	@PostMapping("/fetch-placed-deliveries")
	public ResponseEntity<?> fetchCustomerWithPlacedDelivery(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerDetailService.fetchAllCustomerDeliveries(customerDto.getId(), 1));
	}

	@PostMapping("/fetch-cutomer-service")
	public ResponseEntity<?> fetchCustomerServices(@RequestBody CustomerServicesDto serviceDto) {
		return ResponseEntity
				.ok(customerDetailService.fetchCustomerServices(serviceDto.getAdminId(), serviceDto.getServiceType()));
	}

	@PostMapping("/add-service-request")
	public ResponseEntity<?> addServiceRequestAndMessage(@RequestBody CustomerServiceRequestDto serviceRequestDto) {
		return ResponseEntity.ok(customerDetailService.addRequestAndMessage(serviceRequestDto));
	}

	@PostMapping("/fetch-request-messages")
	public ResponseEntity<?> fetchServiceMessages(@RequestBody CustomerServiceRequestDto serviceRequestDto) {
		return ResponseEntity.ok(customerDetailService.getAllMessages(serviceRequestDto.getServiceRequestId()));
	}

	@PostMapping("/fetch-service-count")
	public ResponseEntity<?> fetchRequestCount(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerDetailService.getCountOfRequestInDifferentTabs(customerDto.getId()));
	}

	@PostMapping("/fetch-all-requests")
	public ResponseEntity<?> fetchAllCustomerRequests(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerDetailService.fetchAllCustomerServiceRequest(customerDto.getId()));
	}

	@PostMapping("/check-attorny")
	public ResponseEntity<?> checkAttorny(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerDetailService.checkAttornyStatus(customerDto.getId()));
	}

	@PostMapping("/revoke-attorny")
	public ResponseEntity<?> revokeAttorny(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerDetailService.revokeAttorny(customerDto.getId()));
	}

	@PostMapping("/check-booking")
	public ResponseEntity<?> checkBooking(@RequestBody CustomerAddressDto addressDto) {
		return ResponseEntity.ok(customerDetailService.checkForBookings(addressDto));
	}

	@PostMapping("/send-attachment-mail")
	public ResponseEntity<?> sendAttachmentMail(@RequestBody CustomerDto customerDto) {
		return ResponseEntity
				.ok(customerBookingService.sendUnsignedDocumentByEmail(customerDto.getAdminId(), customerDto.getId()));
	}

	@PostMapping("/fetch-customer-delivery-group")
	public ResponseEntity<?> fetchCustomerDeliveryInGroup(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(
				customerDetailService.fetchAllCustomerDeliveriesByGroup(customerDto.getAdminId(), customerDto.getId()));
	}

	@PostMapping("/toggle-customer-notification")
	public ResponseEntity<?> toggleCustomerNotification(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerDetailService.toggleNotificationOfCustomer(customerDto.getAdminId(),
				customerDto.getId(), customerDto.getIsNotificationEnabled()));
	}

	@PostMapping("/toggle-delivery-notification")
	public ResponseEntity<?> toggleDeliveryNotification(@RequestBody CustomerDeliveryDto deliveryDto) {
		return ResponseEntity.ok(customerDetailService.toggleEachDeliveryNotifcation(deliveryDto.getCustomerId(),
				deliveryDto.getDeliveryId()));
	}

	@PostMapping("/send-attorny-mail")
	public ResponseEntity<?> sendAttornyMail(@RequestBody CustomerDto customerDto) {
		return ResponseEntity
				.ok(customerDetailService.sendMailToCustomer(customerDto.getId(), customerDto.getAdminId()));
	}

	@PostMapping("/fetch-profile-info")
	public ResponseEntity<?> fetchCustomerProfileInfo(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerDetailService.fetchCustomerProfile(customerDto));
	}

	@PostMapping("/update-customer-detail")
	public ResponseEntity<?> updateCustomerDetail(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(customerUpdateService.updateCustomerDetail(customerDto));
	}

	@PostMapping("/update-meter-designation")
	public ResponseEntity<?> updateMeterDesignation(@RequestBody Map<String, Object> payload) {

		Long connectionId = Long.valueOf(payload.get("connectionId").toString());

		String meterDesignation = payload.get("meterDesignation").toString();

		customerMeterService.updateMeterDesignation(connectionId, meterDesignation);

		return ResponseEntity.ok(customerMeterService.updateMeterDesignation(connectionId, meterDesignation));
	}

	@PostMapping("/submit-invoice-request")
	public ResponseEntity<?> submitInvoiceRequest(@RequestBody CustomerInvoiceRequestDto dto) {

		return ResponseEntity.ok(customerMeterService.submitInvoiceRequest(dto));
	}

	@PostMapping(value = "/report-meter-reading")
	public ResponseEntity<?> reportMeterReading(@RequestPart("data") String jsonData,
			@RequestPart(value = "files", required = false) MultipartFile[] files) {

		try {

			ReportMeterReadingDto dto = objectMapper.readValue(jsonData, ReportMeterReadingDto.class);

			return ResponseEntity.ok(customerMeterService.reportMeterReading(dto, files));

		} catch (Exception e) {

			e.printStackTrace();

			return ResponseEntity.ok(Map.of("res", false, "message", e.getMessage()));
		}
	}

	@PostMapping("/add-supplier-message")
	public ResponseEntity<?> addSupplierMessage(@RequestBody EnergySupplierMessageDto energySupplierMessageDto) {
		return ResponseEntity.ok(customerEnergySupplierService.saveSupplierMessage(energySupplierMessageDto));
	}

	@PostMapping("/change-discount-request")
	public ResponseEntity<?> changeDiscountRequest(@RequestBody CustomerChangeDiscountRequestDto dto) {

		return ResponseEntity.ok(customerDetailService.saveChangeDiscountRequest(dto));
	}

	@PostMapping("/fetch-invoice-categories")
	public ResponseEntity<?> fetchInvoiceCategories(@RequestBody EnergySupplierInvoiceCategoryDto dto) {
		return ResponseEntity.ok(customerCategoryService.fetchInvoiceCategoriesForCustomer(dto));
	}

	@PostMapping("/fetch-cancellation-service-category")
	public ResponseEntity<?> fetchCancellationServiceCategories(
			@RequestBody com.tarifvergleich.electricity.dto.CancellationServiceCategoryDto dto) {
		return ResponseEntity.ok(customerCategoryService.fetchCancellationServiceCategoriesForCustomer(dto));
	}

	@PostMapping("/edit-contract-details")
	public ResponseEntity<?> editCustomerContract(
			@RequestPart(value = "lastNameProof", required = false) MultipartFile lastNameProof,
			@RequestPart(value = "companyProof", required = false) MultipartFile companyProof,
			@RequestPart(value = "titleProof", required = false) MultipartFile titleProof,
			@RequestPart(value = "firstSalProof", required = false) MultipartFile firstSalProof,
			@RequestPart(value = "dobProof", required = false) MultipartFile dobProof,
			@RequestPart(value = "metaData", required = true) CustomerContractEditRequestDto editContractDto) {
		return ResponseEntity.ok(customerUpdateService.updateCustomerContract(lastNameProof, companyProof, titleProof,
				firstSalProof, dobProof, editContractDto));
	}
}
