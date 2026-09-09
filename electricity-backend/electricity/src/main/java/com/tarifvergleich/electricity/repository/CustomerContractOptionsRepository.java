package com.tarifvergleich.electricity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerContractEditOptions;

@Repository
public interface CustomerContractOptionsRepository extends JpaRepository<CustomerContractEditOptions, Integer> {

}