package com.tarifvergleich.electricity.service.common;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tarifvergleich.electricity.dto.CustomerDto;
import com.tarifvergleich.electricity.dto.CustomerDto.CustomerShortDetail;
import com.tarifvergleich.electricity.dto.request.CustomerQueryContactRequestDTO;
import com.tarifvergleich.electricity.dto.response.CustomerQueryContactCategoryResponseDTO;
import com.tarifvergleich.electricity.dto.response.CustomerQueryContactResponseDTO;
import com.tarifvergleich.electricity.model.AdminUser;
import com.tarifvergleich.electricity.model.Customer;
import com.tarifvergleich.electricity.model.CustomerQueryContact;
import com.tarifvergleich.electricity.model.CustomerQueryContactCategory;
import com.tarifvergleich.electricity.repository.AdminUserRepository;
import com.tarifvergleich.electricity.repository.CustomerQueryContactCategoryRepository;
import com.tarifvergleich.electricity.repository.CustomerQueryContactRepository;
import com.tarifvergleich.electricity.repository.CustomerRepository;
import com.tarifvergleich.electricity.util.Helper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonService {

	private final CustomerQueryContactRepository queryContactRepository;
	private final CustomerQueryContactCategoryRepository categoryRepository;
	private final CustomerRepository customerRepository;
	private final AdminUserRepository adminUserRepository;

	public List<CustomerQueryContactCategoryResponseDTO> getAllCategories() {
		return categoryRepository.findAll().stream().map(this::mapToCategoryDTO).collect(Collectors.toList());
	}

	public Map<String, Object> getAllCustomers() {
		List<CustomerQueryContactResponseDTO> collect = queryContactRepository.findAllByOrderByCreatedOnDesc().stream()
				.map(this::mapToCustomerQueryContactDto).collect(Collectors.toList());
		Map<String, Object> map = new HashMap<>();
		map.put("res", true);
		map.put("data", collect);
		return map;
	}

	public Map<String, Object> saveQuery(CustomerQueryContactRequestDTO dto) {
		CustomerQueryContactCategory category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId()));

		Customer customer = null;

		System.out.println("CustomerId:" + dto.getCustomerId());

		if (dto.getCustomerId() != null && dto.getCustomerId() > 0)
			customer = customerRepository.findById(dto.getCustomerId()).orElse(null);

		AdminUser adminUser = adminUserRepository.findById(dto.getAdminId())
				.orElseThrow(() -> new EntityNotFoundException("Admin user not found with id: " + dto.getAdminId()));

		CustomerQueryContact contact = CustomerQueryContact.builder().salutation(dto.getSalutation())
				.title(dto.getTitle()).firstName(dto.getFirstName()).lastName(dto.getLastName())
				.phoneNumber(dto.getContactNumber()).email(dto.getEmail()).query(dto.getInquiry()).isResolved(false)
				.createdOn(Helper.getCurrentTimeBerlin()).customer(customer).admin(adminUser).queryCategory(category)
				.build();

		CustomerQueryContact saved = queryContactRepository.save(contact);
		Map<String, Object> response = new HashMap<>();
		response.put("res", true);
		response.put("data", mapToCustomerQueryContactDto(saved));
		return response;
	}

	// Category mapper
	private CustomerQueryContactCategoryResponseDTO mapToCategoryDTO(CustomerQueryContactCategory category) {
		return CustomerQueryContactCategoryResponseDTO.builder().id(category.getId())
				.categoryName(category.getCategoryName()).addedOn(category.getAddedOn())
				.updatedOn(category.getUpdatedOn()).build();
	}

	private CustomerQueryContactResponseDTO mapToCustomerQueryContactDto(CustomerQueryContact contact) {
		List<CustomerShortDetail> customersList = null;
		if (contact.getCustomerIds() != null && !contact.getCustomerIds().isEmpty()) {
			List<Integer> ids = Arrays.stream(contact.getCustomerIds().split(",")).map(String::trim)
					.filter(s -> !s.isEmpty()).map(Integer::valueOf).collect(Collectors.toList());
			if (!ids.isEmpty()) {
				customersList = customerRepository.findAllById(ids).stream().map(CustomerDto::customerShortResponse)
						.collect(Collectors.toList());
			}
		}

		return CustomerQueryContactResponseDTO.builder().customerQueryContactId(contact.getId())
				.salutation(contact.getSalutation()).title(contact.getTitle()).firstName(contact.getFirstName())
				.lastName(contact.getLastName()).email(contact.getEmail()).contactNumber(contact.getPhoneNumber())
				.inquiry(contact.getQuery()).createdOn(contact.getCreatedOn()).isResolved(contact.getIsResolved())
				.resolvedOn(contact.getResolvedOn())
				.categoryName(contact.getQueryCategory() != null ? contact.getQueryCategory().getCategoryName() : null)
				.CategoryId(contact.getQueryCategory() != null ? contact.getQueryCategory().getId() : null)
				.customer(
						contact.getCustomer() != null ? CustomerDto.customerShortResponse(contact.getCustomer()) : null)
				.customers(customersList).build();
	}

	public Map<String, Object> linkCustomersToQuery(Integer queryId, List<Integer> customerIds) {
		CustomerQueryContact query = queryContactRepository.findById(queryId)
				.orElseThrow(() -> new EntityNotFoundException("Query not found with id: " + queryId));

		if (customerIds != null && !customerIds.isEmpty()) {
			Customer primaryCustomer = customerRepository.findById(customerIds.getFirst()).orElseThrow(
					() -> new EntityNotFoundException("Customer not found with id: " + customerIds.getFirst()));
			query.setCustomer(primaryCustomer);

			String idsStr = customerIds.stream().map(String::valueOf).collect(Collectors.joining(","));
			query.setCustomerIds(idsStr);
		} else {
			query.setCustomer(null);
			query.setCustomerIds(null);
		}

		CustomerQueryContact saved = queryContactRepository.save(query);
		Map<String, Object> map = new HashMap<>();
		map.put("res", true);
		map.put("data", mapToCustomerQueryContactDto(saved));
		return map;
	}

	@Transactional
	public Map<String, Object> toggleContactQueryStatus(Integer queryId, Boolean isResolved) {

		BigInteger resolvedOn = isResolved ? Helper.getCurrentTimeBerlin() : null;

		Integer updated = queryContactRepository.updateQueryStatus(queryId, isResolved, resolvedOn);

		Map<String, Object> map = new HashMap<>();

		map.put("res", updated > 0);

		return map;
	}
}