package com.tarifvergleich.electricity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerContractEditOptions;

@Repository
public interface CustomerContractEditOptionsRepository extends JpaRepository<CustomerContractEditOptions, Integer> {
	
	List<CustomerContractEditOptions> findAllByContractEditOptionIdIn(List<Integer> contractEditOptions);

}