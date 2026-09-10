package com.tarifvergleich.electricity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerRequestCounselling;

@Repository
public interface CustomerRequestCounsellingRepository extends JpaRepository<CustomerRequestCounselling, Integer> {

	List<CustomerRequestCounselling> findAllByAdminAdminIdOrderByCreatedOnDesc(Integer adminId);

	@Query("SELECT c FROM CustomerRequestCounselling c WHERE c.admin.adminId = :adminId AND (:concluded IS NULL OR c.concluded = :concluded) ORDER BY c.createdOn DESC")
	List<CustomerRequestCounselling> findAllByAdminAdminIdAndOptionalConcludedOrderByCreatedOnDesc(
			@Param("adminId") Integer adminId, @Param("concluded") Boolean concluded);

	Page<CustomerRequestCounselling> findAllByAdminAdminId(Integer adminId, Pageable pageable);

	@Query("SELECT c FROM CustomerRequestCounselling c WHERE c.admin.adminId = :adminId AND (:concluded IS NULL OR c.concluded = :concluded)")
	Page<CustomerRequestCounselling> findAllByAdminAdminIdAndOptionalConclude(@Param("adminId") Integer adminId,
			@Param("concluded") Boolean concluded, Pageable pageable);

	Long countAllByAdminAdminIdAndConcluded(Integer adminId, Boolean concluded);

	@Modifying
	@Query("UPDATE CustomerRequestCounselling c SET c.concluded = :concluded WHERE c.admin.adminId = :adminId AND id = :id")
	Integer updateConcludedByAdminAdminIdAndId(@Param("id") Integer id, @Param("adminId") Integer adminId,
			@Param("concluded") Boolean concluded);

	Optional<CustomerRequestCounselling> findByIdAndAdminAdminId(Integer id, Integer adminId);

}
