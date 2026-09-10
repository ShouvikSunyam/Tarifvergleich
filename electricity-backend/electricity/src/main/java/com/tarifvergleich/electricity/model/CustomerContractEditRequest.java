package com.tarifvergleich.electricity.model;

import java.math.BigInteger;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
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
@Table(name = "customer_contract_edit_request")
public class CustomerContractEditRequest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_contract_edit_id")
	private Integer id;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "request_payload", columnDefinition = "jsonb")
	private JsonNode requestpayload;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_contract_edit_option_id")
	@JsonIgnore
	private CustomerContractEditOptions selectedOption;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_delivery_id")
	@JsonIgnore
	private CustomerDelivery delivery;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	@JsonIgnore
	private Customer customer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	@JsonIgnore
	private AdminUser admin;
	
	@Column(name = "request_status", comment = "1 - request created, 2 - resolved")
	private Integer requestStatus;
	
	@Column(name = "created_on")
	private BigInteger createdOn;
	
	@Column(name = "resolved_on")
	private BigInteger resolvedOn;
	
	@Column(name = "rejected_on")
	private BigInteger rejectedOn;
	
	
	@PrePersist
	protected void onCreate() {
		createdOn = Helper.getCurrentTimeBerlin();
		requestStatus = 1;
	}
	

}
