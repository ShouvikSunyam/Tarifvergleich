package com.tarifvergleich.electricity.dto;

import java.math.BigInteger;

import com.tarifvergleich.electricity.model.CustomerChangeDiscountRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerChangeDiscountRequestDto {

	private Integer id;

	private Integer customerId;

	private Integer deliveryId;

	private Integer orderId;

	private String newAdvanceAmount;

	private String reason;

	private Integer status;

	private BigInteger createdOn;
	
	private Integer adminId;
	
	private Integer page;
	
	private Integer size;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerChangeDiscountAdminResponseDto {

		private Integer customerChangeDiscountId;

		private CustomerDeliveryResponseDto customerDetails;

		private String newAdvanceAmount;

		private String reason;

		private Integer status;

		private BigInteger createdOn;
	}

	public static CustomerChangeDiscountAdminResponseDto mapAdminResponse(
			CustomerChangeDiscountRequest customerChangeDiscountRequest) {

		if (customerChangeDiscountRequest == null)
			return null;

		return CustomerChangeDiscountAdminResponseDto.builder()
				.customerChangeDiscountId(customerChangeDiscountRequest.getId())
				.newAdvanceAmount(customerChangeDiscountRequest.getNewAdvanceAmount())
				.reason(customerChangeDiscountRequest.getReason()).status(customerChangeDiscountRequest.getStatus())
				.createdOn(customerChangeDiscountRequest.getCreatedOn()).customerDetails(CustomerDeliveryResponseDto
						.mapResponse(customerChangeDiscountRequest.getCustomerDelivery()))
				.build();
	}
}