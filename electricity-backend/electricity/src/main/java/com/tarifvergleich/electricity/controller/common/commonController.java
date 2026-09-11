package com.tarifvergleich.electricity.controller.common;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarifvergleich.electricity.dto.request.CustomerQueryContactRequestDTO;
import com.tarifvergleich.electricity.service.RecaptchaService;
import com.tarifvergleich.electricity.service.common.CommonService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class commonController {

	private final CommonService commonService;

	@Autowired
	private RecaptchaService recaptchaService;

	@PostMapping("/fetch-contact-category")
	public ResponseEntity<?> fetchContactCategory() {
		return ResponseEntity.ok(commonService.getAllCategories());
	}

	@PostMapping("/save-customer-contact")
	public ResponseEntity<?> saveCustomerContact(@RequestBody CustomerQueryContactRequestDTO dto) {

		if (!recaptchaService.verify(dto.getRecaptchaToken())) {
			return ResponseEntity.status(400).body(Map.of("error", "reCAPTCHA-Verifizierung fehlgeschlagen."));
		}

		Map<String, Object> response = commonService.saveQuery(dto);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/fetch-customer-queries")
	public ResponseEntity<?> fetchCustomerQueries() {
		return ResponseEntity.ok(commonService.getAllCustomers());
	}

	@PostMapping("/link-customer-query")
	public ResponseEntity<?> linkCustomerQuery(@RequestBody Map<String, Object> payload) {
		Integer queryId = payload.get("customerQueryContactId") != null
				? ((Number) payload.get("customerQueryContactId")).intValue()
				: null;

		List<?> rawCustomerIds = (List<?>) payload.get("customerIds");
		List<Integer> customerIds = null;
		if (rawCustomerIds != null) {
			customerIds = rawCustomerIds.stream().map(id -> ((Number) id).intValue())
					.collect(java.util.stream.Collectors.toList());
		}
		return ResponseEntity.ok(commonService.linkCustomersToQuery(queryId, customerIds));
	}

	@PostMapping("/toggle-contact-query-status")
	public ResponseEntity<?> toggleContactQueryStatus(@RequestBody Map<String, Object> payload) {

		Integer queryId = payload.get("queryId") != null ? ((Number) payload.get("queryId")).intValue() : null;

		Boolean isResolved = payload.get("isResolved") != null ? (Boolean) payload.get("isResolved") : false;

		return ResponseEntity.ok(commonService.toggleContactQueryStatus(queryId, isResolved));
	}
}