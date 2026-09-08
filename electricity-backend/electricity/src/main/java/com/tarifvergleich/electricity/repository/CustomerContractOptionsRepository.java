package com.tarifvergleich.electricity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerContractOptions;

@Repository
public interface CustomerContractOptionsRepository extends JpaRepository<CustomerContractOptions, Integer> {

}