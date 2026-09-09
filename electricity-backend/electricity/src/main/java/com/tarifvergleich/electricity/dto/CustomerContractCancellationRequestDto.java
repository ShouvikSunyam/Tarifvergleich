package com.tarifvergleich.electricity.dto;

import java.math.BigInteger;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tarifvergleich.electricity.dto.CustomerDto.CustomerShortDetail;
import com.tarifvergleich.electricity.dto.CustomerRequestCounsellingDto.CustomerRequestCousellingResponseForAdmin;
import com.tarifvergleich.electricity.model.CustomerContractCancellationRequest;
import com.tarifvergleich.electricity.model.CustomerDelivery;
import com.tarifvergleich.electricity.model.CustomerRequestCounselling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContractCancellationRequestDto {

	private Integer CustomerContractCancellationRequestId;

	private String reason;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate desiredDate;

	private String terminationType;

	private String additionalInfo;

	private Integer status;

	private BigInteger createdOn;

	private BigInteger resolvedOn;

	private BigInteger rejectedOn;

	private Integer selectedCategoryId;

	private Integer deliveryId;

	private Integer adminId;

	private Integer customerId;

	private String mobileNumber;

	private String weekDay;

	private String timeSlot;

	private String description;

	private Integer page;

	private Integer size;

	@Builder
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerContractCancellationCategoryResponse {

		private Integer contractCancellationCategoryId;
		private String categoryName;

	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerContractCancellationRequestAdminResDto {

		private Integer CustomerContractCancellationRequestId;

		private CustomerContractCancellationCategoryResponse selectedCategory;

		private CustomerRequestCousellingResponseForAdmin counselling;

		private CustomerShortDetail customer;

		private String energyBranch;

		private Integer deliveryId;

		private String reason;

		private BigInteger desiredDate;

		private String terminationType;

		private String additionalInfo;

		private Integer status;

		private BigInteger createdOn;

		private BigInteger resolvedOn;

		private BigInteger rejectedOn;

	}

	public static CustomerContractCancellationRequestAdminResDto mapAdminRes(
			CustomerContractCancellationRequest request) {
		if (request == null) {
			return null;
		}

		CustomerDelivery delivery = request.getCustomerDelivery();
		var category = request.getSelectedCategory();

		CustomerContractCancellationCategoryResponse categoryResponse = null;
		if (category != null) {
			categoryResponse = CustomerContractCancellationCategoryResponse.builder()
					.contractCancellationCategoryId(category.getId()).categoryName(category.getCategoryName()).build();
		}

		CustomerRequestCounselling latestCounselling = null;
		if (request.getCounselling() != null && !request.getCounselling().isEmpty())
			latestCounselling = request.getCounselling().get(request.getCounselling().size() - 1);

		return CustomerContractCancellationRequestAdminResDto.builder()
				.CustomerContractCancellationRequestId(request.getId()).selectedCategory(categoryResponse)
				.customer(
						request.getCustomer() != null ? CustomerDto.customerShortResponse(request.getCustomer()) : null)
				.energyBranch(delivery != null ? delivery.getDeliveryType() : null)
				.counselling(
						CustomerRequestCounsellingDto.mapCustomerRequestCounsellingResponseForAdminWithDelivery(latestCounselling))
				.deliveryId(delivery != null ? delivery.getId() : null).reason(request.getReason())
				.desiredDate(request.getDesiredDate()).terminationType(request.getTerminationType())
				.additionalInfo(request.getAdditionalInfo()).status(request.getStatus())
				.createdOn(request.getCreatedOn()).resolvedOn(request.getResolvedOn())
				.rejectedOn(request.getRejectedOn()).build();
	}

}
