package com.tarifvergleich.electricity.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "customer_contract_cancellation_category")
public class CustomerContractCancellationCategory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contract_cancellation_category_id")
	private Integer id;
	
	@Column(name = "categoryName")
	private String categoryName;
	
	private Integer status;
	
	@OneToMany(mappedBy = "selectedCategory")
	@JsonIgnoreProperties("selectedCategory")
	private List<CustomerContractCancellationRequest> cancellationRequests;

}
