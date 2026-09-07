package com.tarifvergleich.electricity.dto;

import java.math.BigInteger;

import com.tarifvergleich.electricity.dto.CustomerDeliveryResponseDto.CustomerDeliveryProfileDetail;
import com.tarifvergleich.electricity.model.EnergySupplierMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergySupplierMessageDto {

	private Integer supplierMesageId;
	private Integer supplierMessageCategoryId;
	private String message;
	private BigInteger addedOn;
	private Integer status;

	private Integer customerOrderId;
	private Long orderId;
	private Integer customerDeliveryId;
	private Integer customerId;
	private Integer adminId;
	private Integer page;
	private Integer size;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SupplierMessageCustomerResponse {
		private Integer supplierMesageId;
		private String message;
		private Integer status;
		private Integer categoryId;
		private String categoryName;
		private String statusLabel;
		private BigInteger addedOn;
		private CustomerDeliveryProfileDetail customerOrderDetail;

	}

	public static SupplierMessageCustomerResponse mapForDeliveryResponseCustomer(EnergySupplierMessage message) {
		if (message == null)
			return null;

		return SupplierMessageCustomerResponse.builder().supplierMesageId(message.getId()).message(message.getMessage())
				.status(message.getStatus()).statusLabel(mapStatusToLabel(message.getStatus()))
				.categoryId(message.getCategory().getId())
				.categoryName(message.getCategory().getCategoryName())
				.customerOrderDetail(CustomerDeliveryResponseDto.getDeliveryResponseForProfile(message.getCustomerDelivery()))
				.addedOn(message.getAddedOn()).build();
	}

	private static String mapStatusToLabel(Integer status) {
		if (status == null)
			return "Unknown";
		return switch (status) {
		case 0 -> "Offene Anfrage";
		case 1 -> "In Bearbeitung";
		case 2 -> "Weitergeleitet";
		case 3 -> "Vom Admin abgelehnt";
		default -> "Unbekannter Status (" + status + ")";
		};
	}
}
