package com.tarifvergleich.electricity.dto;

import java.math.BigInteger;
import java.time.LocalDateTime;

import com.tarifvergleich.electricity.dto.CustomerDeliveryResponseDto.CustomerDeliveryProfileDetail;
import com.tarifvergleich.electricity.model.Customer;
import com.tarifvergleich.electricity.model.CustomerInvoiceRequest;
import com.tarifvergleich.electricity.model.CustomerOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInvoiceRequestDto {

	private Integer id;

	private String salutation;

	private String customerName;

	private String customerEmail;

	private Long bookingId;

	private Integer bookingStatus;

	private BigInteger bookingCreatedOn;

	private Integer customerId;

	private Integer connectionId;

	private Integer orderId;

	private Integer deliveryId;

	private Integer adminId;

	private Integer invoiceCategory;

	private String message;

	private Integer status;

	private LocalDateTime createdAt;

	@Data
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CustomerInvoiceRequestResponseForAdminDto {
		private Integer customerInvoiceRequestId;
		private Integer customerId;
		private String salutation;
		private String customerName;
		private String customerEmail;
		private Long bookingId;
		private Integer bookingStatus;
		private BigInteger bookingCreatedOn;
		private Boolean isExpired;
		private Boolean adminPlacedOrder;
		private String signedFileUrl;
		private Long bookingOrderId;
		private String message;
		private CustomerDeliveryProfileDetail deliveryId;
		private Integer orderId;
		private Integer adminId;
		private Integer connectionId;
		private String invoiceCategory;
		private Integer id;
		private Integer status;
		private LocalDateTime createdAt;
	}

	public static CustomerInvoiceRequestResponseForAdminDto mapForAdminResponse(CustomerInvoiceRequest entity,
			CustomerOrder order) {
		if (entity == null)
			return null;

		CustomerInvoiceRequestResponseForAdminDto dto = new CustomerInvoiceRequestResponseForAdminDto();

		dto.setId(entity.getId());
		Customer customer = order.getCustomer();

		if (customer != null) {
			dto.setCustomerId(customer.getCustomerId());
			dto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
			dto.setCustomerEmail(customer.getEmail());
			dto.setSalutation(customer.getSalutation());
		}

		if (order != null) {

			dto.setBookingId(order.getOrderId());

			dto.setBookingStatus(order.getOrderStatus());

			dto.setBookingCreatedOn(order.getCreatedOn());

			dto.setIsExpired(order.getIsExpired());

			dto.setAdminPlacedOrder(order.getAdminPlacedOrder());

			dto.setBookingOrderId(order.getOrderId());

			if (order.getCustomerBookingDocument() != null) {
				dto.setSignedFileUrl(order.getCustomerBookingDocument().getSignedFileUrl());
			}
		}

		if (order != null) {
			dto.setDeliveryId(CustomerDeliveryResponseDto.getDeliveryResponseForProfile(order.getDelivery()));
		}
		
		dto.setCustomerInvoiceRequestId(entity.getId());
		dto.setOrderId(entity.getOrderId());
		dto.setConnectionId(entity.getConnectionId());
		dto.setInvoiceCategory(entity.getEnergySupplierInvoiceCategory().getCategoryName());
		dto.setMessage(entity.getMessage());
		dto.setStatus(entity.getStatus());
		dto.setCreatedAt(entity.getCreatedAt());

		return dto;
	}
}