package com.tarifvergleich.electricity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerChangeDiscountRequest;


@Repository
public interface CustomerChangeDiscountRequestRepository
        extends JpaRepository<CustomerChangeDiscountRequest, Integer> {
	
	List<CustomerChangeDiscountRequest> findByCustomerDeliveryIdIn(
	        List<Integer> deliveryIds);
	
	Page<CustomerChangeDiscountRequest> findAllByAdminAdminIdOrderByCreatedOnDesc(Integer adminId, Pageable pageable);
	
	List<CustomerChangeDiscountRequest> findAllByAdminAdminIdOrderByCreatedOnDesc(Integer adminId);

}