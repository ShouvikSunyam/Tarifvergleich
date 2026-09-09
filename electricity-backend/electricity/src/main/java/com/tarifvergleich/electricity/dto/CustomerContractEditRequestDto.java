package com.tarifvergleich.electricity.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContractEditRequestDto {

	private Integer id;
		
	private List<Integer> selectedOption;
	
	private Integer deliveryId;
	
	private Integer customerId;
	
	private Integer adminId;
	
	private String lastName;
	
	private String companyName;
	
	private String title;
	
	private String firstName;
	
	private String salutation;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dob;
	
	private String others;
	
	
}
