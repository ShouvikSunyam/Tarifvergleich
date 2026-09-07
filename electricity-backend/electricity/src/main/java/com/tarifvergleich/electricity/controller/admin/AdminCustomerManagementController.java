package com.tarifvergleich.electricity.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarifvergleich.electricity.dto.CustomerAttornyDto;
import com.tarifvergleich.electricity.dto.CustomerChangeDiscountRequestDto;
import com.tarifvergleich.electricity.dto.CustomerConnectionRequestDto;
import com.tarifvergleich.electricity.dto.CustomerDeliveryDto;
import com.tarifvergleich.electricity.dto.CustomerDeliveryRequestWrapper.AdminEditCustomerDeliveryRelated;
import com.tarifvergleich.electricity.dto.CustomerDetailsContactHistoryDto;
import com.tarifvergleich.electricity.dto.CustomerDto;
import com.tarifvergleich.electricity.dto.CustomerNoteDto;
import com.tarifvergleich.electricity.dto.CustomerOrderDto;
import com.tarifvergleich.electricity.dto.CustomerSendEmailRequestDto;
import com.tarifvergleich.electricity.dto.CustomerServiceRequestDto;
import com.tarifvergleich.electricity.dto.CustomerServicesDto;
import com.tarifvergleich.electricity.dto.EnergySupplierMessageDto;
import com.tarifvergleich.electricity.service.admin.AdminCustomerDeliveryManagementService;
import com.tarifvergleich.electricity.service.admin.AdminCustomerManagementService;
import com.tarifvergleich.electricity.service.admin.AdminCustomerRequestManagementService;
import com.tarifvergleich.electricity.service.admin.AdminServicePointManagementService;
import com.tarifvergleich.electricity.service.customer.CustomerDetailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Customer Management", description = "Endpoints for managing customer and customer related operations")
public class AdminCustomerManagementController {

	private final AdminCustomerManagementService adminCustomerManagementService;
	private final AdminServicePointManagementService servicePointManagementService;
	private final CustomerDetailService customerDetailService;
	private final AdminCustomerDeliveryManagementService adminCustomerDeliveryManagementService;
	private final AdminCustomerRequestManagementService adminCustomerRequestManagementService;
	private final ObjectMapper objectMapper;

	@Operation(summary = "Fetch customer", description = "Returns a list of customer with there details")
	@PostMapping("/fetch-customer-details")
	public ResponseEntity<?> getCustomer(@RequestBody CustomerDto payload) {
		return ResponseEntity.ok(adminCustomerManagementService.getCustomers(payload));
	}

	@PostMapping("/fetch-deliveries")
	public ResponseEntity<?> getDeliveries(@RequestBody CustomerDeliveryDto payload) {
		return ResponseEntity.ok(adminCustomerManagementService.getAllDeliveries(payload));
	}

	@PostMapping("/fetch-customer-comparisons")
	public ResponseEntity<?> getCustomerComparisons(@RequestBody Map<String, Object> payload) {
		Integer adminId = (Integer) payload.get("adminId");
		Integer page = (Integer) payload.get("page");
		Integer size = (Integer) payload.get("size");
		String search = payload.getOrDefault("search", "").toString();
		return ResponseEntity.ok(adminCustomerManagementService.getAllComparison(adminId, page, size, search));
	}

	@PostMapping("/add-customer-service")
	public ResponseEntity<?> addCustomerService(@RequestBody CustomerServicesDto servicesDto) {
		return ResponseEntity.ok(servicePointManagementService.addCustomerServices(servicesDto));
	}

	@PostMapping("/remove-customer-service")
	public ResponseEntity<?> removeCustomerService(@RequestBody CustomerServicesDto servicesDto) {
		return ResponseEntity.ok(servicePointManagementService.removeCustomerService(servicesDto));
	}

	@PostMapping("/add-service-request-response")
	public ResponseEntity<?> addResponseToCustomerServiceRequest(
			@RequestBody CustomerServiceRequestDto serviceRequestDto) {
		return ResponseEntity.ok(adminCustomerManagementService.addResponseToCustomerServiceRequest(serviceRequestDto));
	}

	@PostMapping("/fetch-request-messages")
	public ResponseEntity<?> fetchServiceMessages(@RequestBody CustomerServiceRequestDto serviceRequestDto) {
		return ResponseEntity.ok(customerDetailService.getAllMessages(serviceRequestDto.getServiceRequestId()));
	}

	@PostMapping("/fetch-service-requests")
	public ResponseEntity<?> fetchServiceRequests(@RequestBody CustomerServiceRequestDto serviceRequestDto) {
		System.err.println("I reached here");
		return ResponseEntity.ok(adminCustomerManagementService.fetchCustomerServiceRequests(serviceRequestDto));
	}

	@PostMapping("/fetch-services")
	public ResponseEntity<?> fetchServices(@RequestBody CustomerServicesDto servicesDto) {
		return ResponseEntity.ok(servicePointManagementService.fetchServices(servicesDto));
	}

	@PostMapping("/close-service-request")
	public ResponseEntity<?> closeCustomerServiceRequest(@RequestBody CustomerServiceRequestDto serviceRequestDto) {
		return ResponseEntity.ok(adminCustomerManagementService
				.closeCustomerServiceRequest(serviceRequestDto.getAdminId(), serviceRequestDto.getServiceRequestId()));
	}

	@PostMapping("/count-open-service-requests")
	public ResponseEntity<?> countOpenServiceRequests(@RequestBody CustomerServiceRequestDto serviceRequestDto) {
		return ResponseEntity.ok(adminCustomerManagementService.countOpenServiceRequests(serviceRequestDto));
	}

	@PostMapping("/update-attorny-status")
	public ResponseEntity<?> updateAttornyStatus(@RequestBody CustomerAttornyDto attornyDto) {
		return ResponseEntity.ok(adminCustomerManagementService.updateAttornyStatus(attornyDto));
	}

	@PostMapping("/update-customer-booking")
	public ResponseEntity<?> updateCustomerBookingDetails(
			@RequestBody AdminEditCustomerDeliveryRelated bookingDetailsDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.editDeliveryDetailsByAdmin(bookingDetailsDto));
	}

	@PostMapping("/place-order")
	public ResponseEntity<?> placeCustomerOrder(@RequestBody CustomerOrderDto customerOrderDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.placeNewOrderToEgon(customerOrderDto));
	}

	@PostMapping("/toggle-customer-notification")
	public ResponseEntity<?> toggleCustomerNotification(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(adminCustomerManagementService.toggleNotificationOfCustomer(customerDto.getAdminId(),
				customerDto.getId()));
	}

	@PostMapping("/add-customer")
	public ResponseEntity<?> addNewCustomer(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(adminCustomerManagementService.createNewCustomer(customerDto));
	}

	@PostMapping("/add-note")
	public ResponseEntity<?> addNote(@RequestBody CustomerNoteDto noteDto) {
		return ResponseEntity.ok(adminCustomerManagementService.addCustomerNoteByAdmin(noteDto));
	}

	@PostMapping("/add-contact-history")
	public ResponseEntity<?> addContactHistory(@RequestBody CustomerDetailsContactHistoryDto historyDto) {
		return ResponseEntity.ok(adminCustomerManagementService.addCustomerContactHistoryByAdmin(historyDto));
	}

	@PostMapping("/add-new-delivery")
	public ResponseEntity<?> addNewDeliveryByAdmin(
			@RequestBody AdminEditCustomerDeliveryRelated newDeliveryBookingDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.addNewDeliveryByAdmin(newDeliveryBookingDto));
	}

	@PostMapping("/fetch-customer-names")
	public ResponseEntity<?> fetchCustomerSelectiveFields(@RequestBody CustomerDto customerDto) {
		return ResponseEntity
				.ok(adminCustomerManagementService.fetchCustomerByNameEmailAndId(customerDto.getAdminId()));
	}

	@PostMapping("/open-order")
	public ResponseEntity<?> openOrder(@RequestBody CustomerDeliveryDto deliveryDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.openOrder(deliveryDto));
	}

	@PostMapping("/update-meter-number")
	public ResponseEntity<?> updateMeterNumber(@RequestBody CustomerConnectionRequestDto connectionDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.updateMeterNumber(connectionDto.getAdminId(),
				connectionDto.getDeliveryId(), connectionDto.getMeterNumber()));
	}

	@PostMapping("/add-lexoffice-number")
	public ResponseEntity<?> addLexofficeNumber(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(adminCustomerManagementService.addLexofficeNumberForCustomer(customerDto));
	}

	@PostMapping("/fetch-signed-contract")
	public ResponseEntity<?> fetchSignedContract(@RequestBody CustomerOrderDto customerOrderDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.getSignedPdfFromEgon(customerOrderDto));
	}

	@PostMapping("/resend-signing-order")
	public ResponseEntity<?> resendSigningContractMail(@RequestBody CustomerOrderDto customerOrderDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.resendSigningContractMail(customerOrderDto));
	}

	@PostMapping("/check-order-status")
	public ResponseEntity<?> checkOrderStatus(@RequestBody CustomerOrderDto customerOrderDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.checkOrderStatus(customerOrderDto));
	}

	@PostMapping("/send-signed-contract-customer")
	public ResponseEntity<?> sendSignedContractToCustomer(@RequestBody CustomerOrderDto orderDto) {
		return ResponseEntity.ok(adminCustomerDeliveryManagementService.sendSignedPdfToCustomer(orderDto));
	}

	@PostMapping("/fetch-all-customer-doc")
	public ResponseEntity<?> fetchAllCustomerDoc(@RequestBody CustomerDto customerDto) {
		return ResponseEntity.ok(adminCustomerManagementService.fetchAllPdfOfCustomer(customerDto));
	}

	@PostMapping("/send-customer-email")
	public ResponseEntity<?> sendCustomerEmail(@RequestParam("data") String jsonData,
			@RequestPart(value = "uploadDocuments", required = false) List<MultipartFile> uploadDocuments)
			throws Exception {
		CustomerSendEmailRequestDto request = objectMapper.readValue(jsonData, CustomerSendEmailRequestDto.class);
		return ResponseEntity.ok(adminCustomerManagementService.sendCustomerEmail(request, uploadDocuments));
	}

	@PostMapping("/fetch-all-supplier-message")
	public ResponseEntity<?> fetchAllSupploerMessage(@RequestBody EnergySupplierMessageDto supplierMessageDto) {
		return ResponseEntity
				.ok(adminCustomerDeliveryManagementService.fetchEnergyProviderMessagesByCustomer(supplierMessageDto));
	}

	@PostMapping("/fetch-all-customer-discount-request")
	public ResponseEntity<?> fetchAllCustomerDiscountRequest(
			@RequestBody CustomerChangeDiscountRequestDto changeDiscountRequestDto) {
		return ResponseEntity.ok(
				adminCustomerRequestManagementService.fetchCustomerChangeDiscountRequests(changeDiscountRequestDto));
	}
}
