package com.tarifvergleich.electricity.service.customer;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarifvergleich.electricity.dto.CustomerContractCancellationRequestDto;
import com.tarifvergleich.electricity.dto.CustomerContractEditRequestDto;
import com.tarifvergleich.electricity.dto.CustomerDto;
import com.tarifvergleich.electricity.exception.InternalServerException;
import com.tarifvergleich.electricity.model.Customer;
import com.tarifvergleich.electricity.model.CustomerAddress;
import com.tarifvergleich.electricity.model.CustomerContractCancellationCategory;
import com.tarifvergleich.electricity.model.CustomerContractCancellationRequest;
import com.tarifvergleich.electricity.model.CustomerContractEditOptions;
import com.tarifvergleich.electricity.model.CustomerContractEditRequest;
import com.tarifvergleich.electricity.model.CustomerDelivery;
import com.tarifvergleich.electricity.model.CustomerRequestCounselling;
import com.tarifvergleich.electricity.repository.CustomerAddressRepository;
import com.tarifvergleich.electricity.repository.CustomerContractCancellationCategoryRepository;
import com.tarifvergleich.electricity.repository.CustomerContractCancellationRequestRepository;
import com.tarifvergleich.electricity.repository.CustomerContractEditOptionsRepository;
import com.tarifvergleich.electricity.repository.CustomerContractEditRequestRepository;
import com.tarifvergleich.electricity.repository.CustomerDeliveryRepository;
import com.tarifvergleich.electricity.repository.CustomerRepository;
import com.tarifvergleich.electricity.util.FileServiceCustomer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerUpdateService {

	private final CustomerRepository customerRepo;
	private final CustomerAddressRepository customerAddressRepo;
	private final CustomerDeliveryRepository customerDeliveryRepo;
	private final CustomerContractEditRequestRepository customerContractEditRequestRepo;
	private final CustomerContractEditOptionsRepository customerContractOptionsRepo;
	private final FileServiceCustomer fileServiceCustomer;
	private final ObjectMapper objectMapper;
	private final CustomerContractCancellationRequestRepository cancellationRequestRepo;
	private final CustomerContractCancellationCategoryRepository cancellationCategoryRepository;

	@Transactional
	public Map<String, Object> updateCustomerDetail(CustomerDto customerDto) {

		if (customerDto.getAdminId() == null || customerDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (customerDto.getId() == null || customerDto.getId() <= 0)
			throw new InternalServerException("Customer id missing", HttpStatus.OK);

		if (customerDto.getFirstName() == null || customerDto.getLastName() == null
				|| customerDto.getFirstName().isEmpty() || customerDto.getLastName().isEmpty())
			throw new InternalServerException("Customer name missing", HttpStatus.OK);

		if (customerDto.getSalutation() == null || customerDto.getSalutation().isEmpty())
			throw new InternalServerException("Salutation missing", HttpStatus.OK);

		Customer customer = customerRepo.findByCustomerIdAndAdminAdminId(customerDto.getId(), customerDto.getAdminId())
				.orElseThrow(
						() -> new InternalServerException("Customer not found with this credential", HttpStatus.OK));

		if (customer.getUserType().equalsIgnoreCase("BUSINESS") && customerDto.getCompanyName().isEmpty())
			throw new InternalServerException("Company name missing", HttpStatus.OK);

		if (customer.getUserType().equalsIgnoreCase("BUSINESS") && !customerDto.getCompanyName().isEmpty())
			customer.setCompanyName(customerDto.getCompanyName());

		customer.setFirstName(customerDto.getFirstName());
		customer.setLastName(customerDto.getLastName());
		customer.setSalutation(customerDto.getSalutation());

		customer.setTitle(customerDto.getTitle());

		if (customerDto.getMobileNumber() != null && !customerDto.getMobileNumber().isEmpty())
			customer.setMobileNumber(customerDto.getMobileNumber());

		if (customerDto.getTelephone() != null && !customerDto.getTelephone().isEmpty())
			customer.setTelephone(customerDto.getTelephone());

		if (customerDto.getZip() != null && customerDto.getCity() != null && customerDto.getStreet() != null
				&& !customerDto.getZip().isEmpty() && !customerDto.getCity().isEmpty()
				&& !customerDto.getStreet().isEmpty() && customerDto.getHouseNumber() != null
				&& !customerDto.getHouseNumber().isEmpty()) {
			CustomerAddress address = customerAddressRepo.findAddress(customer.getCustomerId(), customerDto.getZip(),
					customerDto.getCity(), customerDto.getStreet(), customerDto.getHouseNumber()).orElse(null);

			if (address != null) {
				customerAddressRepo.updateRegistrationStatus(false, customerDto.getId());
				address.setIsRegisterAddress(true);
				customerAddressRepo.save(address);
			} else {
				CustomerAddress newAddress = CustomerAddress.builder().zip(customerDto.getZip())
						.city(customerDto.getCity()).street(customerDto.getStreet())
						.houseNumber(customerDto.getHouseNumber()).isRegisterAddress(true).customerId(customer)
						.isRegisterAddress(true).build();
				customerAddressRepo.updateRegistrationStatus(false, customerDto.getId());
				customerAddressRepo.save(newAddress);
			}
		}

		if (customerDto.getZip() != null && !customerDto.getZip().isEmpty())
			customer.setZip(customerDto.getZip());
		if (customerDto.getCity() != null && !customerDto.getCity().isEmpty())
			customer.setCity(customer.getCity());
		if (customerDto.getStreet() != null && !customerDto.getStreet().isEmpty())
			customer.setStreet(customerDto.getStreet());
		if (customerDto.getHouseNumber() != null && !customerDto.getHouseNumber().isEmpty())
			customer.setHouseNumber(customerDto.getHouseNumber());

		customerRepo.save(customer);

		return Map.of("res", true, "message", "customer details updated successfully");
	}

	@Transactional
	public Map<String, Object> updateCustomerContract(MultipartFile lastNameProof, MultipartFile companyProof,
			MultipartFile titleProof, MultipartFile firstSalProof, MultipartFile dobProof,
			CustomerContractEditRequestDto editContractDto) {

		if (editContractDto == null)
			throw new InternalServerException("Meta data missing", HttpStatus.OK);

		if (editContractDto.getSelectedOption() == null || editContractDto.getSelectedOption().size() < 1)
			throw new InternalServerException("Contract edit selected option missing", HttpStatus.OK);

		CustomerDelivery delivery = customerDeliveryRepo.findById(editContractDto.getDeliveryId())
				.orElseThrow(() -> new InternalServerException("Customer order not found", HttpStatus.OK));

		List<CustomerContractEditOptions> selectedOptions = customerContractOptionsRepo
				.findAllByContractEditOptionIdIn(editContractDto.getSelectedOption());

		if (selectedOptions == null || selectedOptions.size() < 1)
			throw new InternalServerException("Contract edit options not found", HttpStatus.OK);

		List<CustomerContractEditRequest> requests = selectedOptions.stream().map(options -> {

			CustomerContractEditRequest request = CustomerContractEditRequest.builder().admin(delivery.getAdmin())
					.customer(delivery.getCustomerId()).delivery(delivery).selectedOption(options).build();

			Map<String, Object> payloadRequest = new HashMap<String, Object>();

			if (options.getContractEditOptionName().toLowerCase().equals("Last Name".toLowerCase())) {

				if (editContractDto.getLastName() == null || editContractDto.getLastName().isEmpty())
					throw new InternalServerException("Last name missing", HttpStatus.OK);

				String filePath = fileServiceCustomer.saveFile(lastNameProof, "last_name_proof");
				payloadRequest.put("lastNameProofUrl", filePath);
				payloadRequest.put("lastName", editContractDto.getLastName());
			} else if (options.getContractEditOptionName().toLowerCase().equals("Company Name".toLowerCase())) {

				if (editContractDto.getCompanyName() == null || editContractDto.getCompanyName().isEmpty())
					throw new InternalServerException("Company name missing", HttpStatus.OK);

				String filePath = fileServiceCustomer.saveFile(companyProof, "company_proof");
				payloadRequest.put("companyProof", filePath);
				payloadRequest.put("companyName", editContractDto.getCompanyName());

			} else if (options.getContractEditOptionName().toLowerCase().equals("Title".toLowerCase())) {

				if (editContractDto.getTitle() == null || editContractDto.getTitle().isEmpty())
					throw new InternalServerException("Title missing", HttpStatus.OK);

				String filePath = fileServiceCustomer.saveFile(titleProof, "title_proof");
				payloadRequest.put("titleProof", filePath);
				payloadRequest.put("title", editContractDto.getTitle());

			} else if (options.getContractEditOptionName().toLowerCase().equals("First Name".toLowerCase())) {

				if (editContractDto.getFirstName() == null || editContractDto.getFirstName().isEmpty())
					throw new InternalServerException("First name missing", HttpStatus.OK);

				String filePath = fileServiceCustomer.saveFile(firstSalProof, "first_name_proof");
				payloadRequest.put("firstNameProof", filePath);
				payloadRequest.put("firstName", editContractDto.getFirstName());

			} else if (options.getContractEditOptionName().toLowerCase().equals("Salutation".toLowerCase())) {

				if (editContractDto.getSalutation() == null || editContractDto.getSalutation().isEmpty())
					throw new InternalServerException("Salutation missing", HttpStatus.OK);

				String filePath = fileServiceCustomer.saveFile(firstSalProof, "salutation_proof");
				payloadRequest.put("salutationProof", filePath);
				payloadRequest.put("salutation", editContractDto.getSalutation());

			} else if (options.getContractEditOptionName().toLowerCase().equals("Date of Birth".toLowerCase())) {

				if (editContractDto.getDob() == null || Period
						.between(editContractDto.getDob(), LocalDate.now(ZoneId.of("Europe/Berlin"))).getYears() < 18)
					throw new InternalServerException("DOB missing or age less than 18", HttpStatus.OK);

				String filePath = fileServiceCustomer.saveFile(dobProof, "dob_proof");
				payloadRequest.put("dobProof", filePath);
				payloadRequest.put("dob", editContractDto.getDob());

			} else if (options.getContractEditOptionName().toLowerCase().equals("Billing Address".toLowerCase())) {

			} else if (options.getContractEditOptionName().toLowerCase().equals("Delivery Address".toLowerCase())) {

			} else if (options.getContractEditOptionName().toLowerCase().equals("Change Email Address".toLowerCase())) {

			} else if (options.getContractEditOptionName().toLowerCase().equals("Change Bank Details".toLowerCase())) {

			} else if (options.getContractEditOptionName().toLowerCase().equals("Phone Number".toLowerCase())) {

			} else if (options.getContractEditOptionName().toLowerCase().equals("Other".toLowerCase())) {
				payloadRequest.put("others", editContractDto.getOthers());
			}

			else {
				throw new InternalServerException("Selected option not found", HttpStatus.OK);
			}

			if (payloadRequest.size() < 1)
				throw new InternalServerException("No request found", HttpStatus.OK);

			JsonNode payloadJson = objectMapper.valueToTree(payloadRequest);

			request.setRequestpayload(payloadJson);

			return request;
		}).toList();

		customerContractEditRequestRepo.saveAll(requests);

		return Map.of("res", true, "message", "Request Submitted Successfully");
	}

	@Transactional
	public Map<String, Object> requestContractCancellation(CustomerContractCancellationRequestDto requestDto) {

		if (requestDto == null || requestDto.getDeliveryId() == null || requestDto.getDeliveryId() < 1
				|| requestDto.getTerminationType() == null || requestDto.getTerminationType().isEmpty()
				|| requestDto.getSelectedCategoryId() == null || requestDto.getSelectedCategoryId() < 1)
			throw new InternalServerException("Insufficient data", HttpStatus.OK);

		if (requestDto.getDesiredDate().isBefore(LocalDate.now(ZoneId.of("Europe/Berlin")))) {
			throw new InternalServerException("Past time not allowed", HttpStatus.OK);
		}

		if (!requestDto.getTerminationType().toLowerCase().equals("ordinary termination")
				&& !requestDto.getTerminationType().toLowerCase().equals("extraordinary termination"))
			throw new InternalServerException("Invalid Termination Type", HttpStatus.OK);

		CustomerDelivery delivery = customerDeliveryRepo.findById(requestDto.getDeliveryId())
				.orElseThrow(() -> new InternalServerException("Customer order not found", HttpStatus.OK));

		CustomerContractCancellationCategory category = cancellationCategoryRepository
				.findById(requestDto.getSelectedCategoryId())
				.orElseThrow(() -> new InternalServerException("Invalid Category", HttpStatus.OK));

		CustomerRequestCounselling counsellingSchedule = CustomerRequestCounselling.builder()
				.mobileNumber(requestDto.getMobileNumber()).weekDay(requestDto.getWeekDay().toUpperCase())
				.customerOrder(delivery.getCustomerOrder())
				.timeSlot(requestDto.getTimeSlot()).description(requestDto.getDescription()).build();

		CustomerContractCancellationRequest request = CustomerContractCancellationRequest.builder()
				.reason(requestDto.getReason())
				.desiredDate(BigInteger
						.valueOf(requestDto.getDesiredDate().atStartOfDay(ZoneId.of("Europe/Berlin")).toEpochSecond()))
				.terminationType(requestDto.getTerminationType()).additionalInfo(requestDto.getAdditionalInfo())
				.selectedCategory(category).customerDelivery(delivery).customer(delivery.getCustomerId())
				.admin(delivery.getAdmin()).build();

		request.addCounsellingRequest(counsellingSchedule);

		cancellationRequestRepo.save(request);

		return Map.of("res", true, "message", "Request saved successfully");
	}
}
