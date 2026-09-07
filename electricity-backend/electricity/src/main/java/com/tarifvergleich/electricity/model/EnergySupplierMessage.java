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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "energy_supplier_messages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EnergySupplierMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(columnDefinition = "TEXT")
	private String message;

	@Column(name = "added_on")
	private BigInteger addedOn;

	@Column(name = "last_updated_on")
	private BigInteger lastUpdatedOn;

	@Column(comment = "0 - 'Open Request', 1 - 'In Progress', 2 - 'Forwaded', 3 - 'Admin Rejected'")
	private Integer status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_message_category_id")
	@JsonIgnore
	private EnergySupplierMessageCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_order_id")
	@JsonIgnore
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_delivery_id")
	@JsonIgnore
	private CustomerDelivery customerDelivery;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	@JsonIgnore
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	@JsonIgnore
	private AdminUser admin;

	@PrePersist
	protected void onCreate() {
		addedOn = Helper.getCurrentTimeBerlin();
		status = 1;
	}

	@PreUpdate
	public void updatedOn() {
		lastUpdatedOn = Helper.getCurrentTimeBerlin();
	}

}
