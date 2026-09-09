package com.tarifvergleich.electricity.model;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tarifvergleich.electricity.util.Helper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "customer_contract_cancellation_request")
public class CustomerContractCancellationRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "reason_for_cancellation", columnDefinition = "TEXT")
	private String reason;

	@Column(name = "desired_date_of_cancellation")
	private BigInteger desiredDate;

	@Column(name = "termination_type", columnDefinition = "VARCHAR(255) CHECK (LOWER(termination_type) IN ('ordinary termination', 'extraordinary termination'))")
	private String terminationType;

	@Column(name = "additional_info")
	private String additionalInfo;

	@Column(comment = "1 - request submitted")
	private Integer status;

	private BigInteger createdOn;

	private BigInteger resolvedOn;

	private BigInteger rejectedOn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "contract_cancellation_selected_category_id")
	private CustomerContractCancellationCategory selectedCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "customer_delivery_id")
	private CustomerDelivery customerDelivery;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "adimin_id")
	private AdminUser admin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "contractCancellationRequest", orphanRemoval = true, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@JsonIgnoreProperties("contractCancellationRequest")
	private List<CustomerRequestCounselling> counselling;

	@PrePersist
	public void onCreate() {
		createdOn = Helper.getCurrentTimeBerlin();
		status = 1;
	}

	public void addCounsellingRequest(CustomerRequestCounselling request) {
		if (counselling == null)
			counselling = new LinkedList<CustomerRequestCounselling>();
		counselling.add(request);
		request.setContractCancellationRequest(this);		
	}

}
