package com.tarifvergleich.electricity.dto;

import java.math.BigInteger;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tarifvergleich.electricity.dto.CustomerDto.CustomerShortDetail;
import com.tarifvergleich.electricity.model.CustomerOrder;
import com.tarifvergleich.electricity.model.CustomerRequestCounselling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CustomerRequestCounsellingDto {
	
	private Long egonOrderId;
	private Integer counsellingId;
    private String mobileNumber;
    private String weekDay;
    private String timeSlot;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;
    private BigInteger createdOn;
    private Integer customerId;  
    private Integer adminId;
    private Boolean concluded;
    
    private Integer page;
    private Integer size;
    
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class CustomerRequestCousellingResponseForAdmin {
    	private Integer cousellingId;
        private String mobileNumber;
        private String weekDay;
        private String timeSlot;
        private String description;
        private BigInteger scheduleDate;
        private BigInteger createdOn;
        private CustomerShortDetail customer;  
        private Boolean concluded;
        private Integer deliveryId;
    }
    
    public static CustomerRequestCousellingResponseForAdmin mapCustomerRequestCounsellingResponseForAdmin(CustomerRequestCounselling request) {
    	if(request == null) return null;
    	
    	return CustomerRequestCousellingResponseForAdmin.builder()
    			.cousellingId(request.getId())
    			.mobileNumber(request.getMobileNumber())
    			.weekDay(request.getWeekDay())
    			.timeSlot(request.getTimeSlot())
    			.description(request.getDescription())
    			.scheduleDate(request.getScheduleDate())
    			.createdOn(request.getCreatedOn())
    			.concluded(request.getConcluded())
    			.customer(request.getCustomer() != null ? CustomerDto.customerShortResponse(request.getCustomer()) : null)
    			.build();
    }
    
    public static CustomerRequestCousellingResponseForAdmin mapCustomerRequestCounsellingResponseForAdminWithDelivery(CustomerRequestCounselling request) {
    	if(request == null) return null;
    	
    	CustomerOrder order = request.getCustomerOrder();
    	Integer deliveryId = null;
    	
    	if(order != null)
    		deliveryId = order.getDelivery().getId();
    	
    	return CustomerRequestCousellingResponseForAdmin.builder()
    			.cousellingId(request.getId())
    			.mobileNumber(request.getMobileNumber())
    			.weekDay(request.getWeekDay())
    			.timeSlot(request.getTimeSlot())
    			.description(request.getDescription())
    			.scheduleDate(request.getScheduleDate())
    			.createdOn(request.getCreatedOn())
    			.concluded(request.getConcluded())
    			.customer(request.getCustomer() != null ? CustomerDto.customerShortResponse(request.getCustomer()) : null)
    			.deliveryId(deliveryId)
    			.build();
    }
}
