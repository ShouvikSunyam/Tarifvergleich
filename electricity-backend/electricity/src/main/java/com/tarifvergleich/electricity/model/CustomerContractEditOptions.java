package com.tarifvergleich.electricity.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "contract_edit_options")
public class CustomerContractEditOptions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contract_edit_option_id")
	private Integer contractEditOptionId;

	@Column(name = "contract_edit_option_name")
	private String contractEditOptionName;

	@OneToMany(mappedBy = "selectedOption", orphanRemoval = true)
	@JsonIgnoreProperties("selectedOption")
	private List<CustomerContractEditRequest> requests;
	
	private Integer status;
	
}