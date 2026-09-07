package com.tarifvergleich.electricity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.EnergySupplierMessage;

@Repository
public interface EnergySupplierMessageRepository extends JpaRepository<EnergySupplierMessage, Integer> {

	Optional<EnergySupplierMessage> findByIdAndCustomerCustomerIdAndAdminAdminId(Integer id, Integer customerId,
			Integer adminId);

	List<EnergySupplierMessage> findAllByAdminAdminIdOrderByAddedOnDesc(Integer adminId);

	Page<EnergySupplierMessage> findAllByAdminAdminId(Integer adminId, Pageable pageable);
}
