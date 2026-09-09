package com.tarifvergleich.electricity.dto;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.tarifvergleich.electricity.dto.CustomerDto.CustomerShortDetail;
import com.tarifvergleich.electricity.model.CustomerContractEditOptions;
import com.tarifvergleich.electricity.model.CustomerContractEditRequest;
import com.tarifvergleich.electricity.model.CustomerDelivery;
import com.tarifvergleich.electricity.util.FileServiceCustomer;
import com.tarifvergleich.electricity.util.Helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContractEditRequestDto {

	private Integer id;

	private List<Integer> selectedOption;

	private Integer deliveryId;

	private Integer customerId;

	private Integer adminId;

	private String lastName;

	private String companyName;

	private String title;

	private String firstName;

	private String salutation;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dob;

	private String others;

	private Integer page;

	private Integer size;

	@Builder
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerContractEditSelectedOptionResponse {

		private Integer contractEditSelectedOptionId;
		private String optionName;

	}

	@Builder
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerContractEditRequestAdminResponseDto {

		private Integer deliveryId;

		private CustomerShortDetail customer;

		private CustomerContractEditSelectedOptionResponse selectedOption;

		private String lastName;

		private String companyName;

		private String filePath;

		private String title;

		private String firstName;

		private String salutation;

		private BigInteger dob;

		private String others;
		
		private BigInteger createdOn;
		
		private Integer status;
		
		private String energyBranch;

	}

	public static CustomerContractEditRequestAdminResponseDto mapAdminResponse(CustomerContractEditRequest request,
			FileServiceCustomer fileService) {
		
		Helper helper = new Helper();
		
		if (request == null)
			return null;

		JsonNode payload = request.getRequestpayload();

		CustomerContractEditOptions option = request.getSelectedOption();
		CustomerDelivery delivery = request.getDelivery();

		CustomerContractEditSelectedOptionResponse selectedOption = CustomerContractEditSelectedOptionResponse.builder()
				.contractEditSelectedOptionId(option.getContractEditOptionId())
				.optionName(option.getContractEditOptionName()).build();
		
		String filePath = resolvedFilePath(selectedOption.getOptionName(), payload, fileService);

		return CustomerContractEditRequestAdminResponseDto.builder().deliveryId(delivery.getId())
				.customer(CustomerDto.customerShortResponse(request.getCustomer()))
				.selectedOption(selectedOption)
				.lastName((payload != null && payload.has("lastName")) ? payload.get("lastName").asText() : null)
	            .companyName((payload != null && payload.has("companyName")) ? payload.get("companyName").asText() : null)
	            .title((payload != null && payload.has("title")) ? payload.get("title").asText() : null)
	            .firstName((payload != null && payload.has("firstName")) ? payload.get("firstName").asText() : null)
	            .salutation((payload != null && payload.has("salutation")) ? payload.get("salutation").asText() : null)
	            .dob((payload != null && payload.has("dob")) ? helper.toGermamUnixTimestamp(LocalDate.parse(payload.get("dob").asText())) : null)
	            .others((payload != null && payload.has("others")) ? payload.get("others").asText() : null).others(null).filePath(filePath)
	            .status(request.getRequestStatus()).createdOn(request.getCreatedOn()).energyBranch(delivery.getDeliveryType())
	            .build();
	}

	public static String resolvedFilePath(String requestOption, JsonNode payload,
			FileServiceCustomer fileServiceCustomer) {

		if (requestOption == null || payload == null) {
			return null;
		}

		return switch (requestOption.toLowerCase()) {
		case "last name" -> payload.has("lastNameProofUrl")
				? fileServiceCustomer.getAbsolutePath(payload.get("lastNameProofUrl").asText())
				: null;
		case "company name" ->
			payload.has("companyProof") ? fileServiceCustomer.getAbsolutePath(payload.get("companyProof").asText())
					: null;
		case "title" ->
			payload.has("titleProof") ? fileServiceCustomer.getAbsolutePath(payload.get("titleProof").asText()) : null;
		case "first name" ->
			payload.has("firstNameProof") ? fileServiceCustomer.getAbsolutePath(payload.get("firstNameProof").asText())
					: null;
		case "salutation" -> payload.has("salutationProof")
				? fileServiceCustomer.getAbsolutePath(payload.get("salutationProof").asText())
				: null;
		case "date of birth" ->
			payload.has("dobProof") ? fileServiceCustomer.getAbsolutePath(payload.get("dobProof").asText()) : null;
		default -> null; // Address, Email, Bank, Phone, Other don't require proof uploads
		};
	}

}
