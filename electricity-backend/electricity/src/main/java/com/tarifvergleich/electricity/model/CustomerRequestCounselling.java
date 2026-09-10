package com.tarifvergleich.electricity.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tarifvergleich.electricity.util.Helper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customer_request_counselling_schedules")
public class CustomerRequestCounselling {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "day_of_week")
	private String weekDay;

	@Column(name = "time_slot")
	private String timeSlot;

	private String description;

	@Column(name = "schedule_date")
	private BigInteger scheduleDate;
	
	@Column(name = "concluded")
	private Boolean concluded;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "admin_id")
	private AdminUser admin;
	
	@ManyToOne
	@JoinColumn(name = "customer_order_id")
	private CustomerOrder customerOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "contract_cancellation_request_id")
	private CustomerContractCancellationRequest contractCancellationRequest;

	@Column(name = "created_on")
	private BigInteger createdOn;

	@PrePersist
	protected void onCreate() {
		createdOn = Helper.getCurrentTimeBerlin();
		concluded = false;
	}

}