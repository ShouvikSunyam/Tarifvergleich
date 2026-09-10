package com.tarifvergleich.electricity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerContractCancellationRequest;

@Repository
public interface CustomerContractCancellationRequestRepository
		extends JpaRepository<CustomerContractCancellationRequest, Integer> {

	Page<CustomerContractCancellationRequest> findAllByAdminAdminIdOrderByCreatedOnDesc(Integer adminId, Pageable pageable);

	List<CustomerContractCancellationRequest> findAllByAdminAdminIdOrderByCreatedOnDesc(Integer adminId);

}
