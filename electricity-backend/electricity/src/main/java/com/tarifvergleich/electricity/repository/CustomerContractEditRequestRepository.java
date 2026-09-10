package com.tarifvergleich.electricity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerContractEditRequest;

@Repository
public interface CustomerContractEditRequestRepository extends JpaRepository<CustomerContractEditRequest, Integer> {
	
	Page<CustomerContractEditRequest> findAllByAdminAdminIdOrderByCreatedOnDesc(Integer adminId, Pageable pageable);
	List<CustomerContractEditRequest> findAllByAdminAdminIdOrderByCreatedOnDesc(Integer adminId);
		
}
