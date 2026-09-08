package com.tarifvergleich.electricity.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "contract_options")
public class CustomerContractOptions {

	@Id
	@Column(name = "contract_options_id")
	private Integer contractOptionsId;

	@Column(name = "contract_options_name")
	private String contractOptionsName;
}