package com.tarifvergleich.electricity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerContractCancellationCategory;

@Repository
public interface CustomerContractCancellationCategoryRepository extends JpaRepository<CustomerContractCancellationCategory, Integer> {

}
