package com.tarifvergleich.electricity.repository;

import com.tarifvergleich.electricity.model.CustomerQueryContact;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CustomerQueryContactRepository extends JpaRepository<CustomerQueryContact, Integer> {
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CustomerQueryContact q SET q.isResolved = :isResolved, q.resolvedOn = :resolvedOn WHERE q.id = :queryId")
	Integer updateQueryStatus(
	        @Param("queryId") Integer queryId,
	        @Param("isResolved") Boolean isResolved,
    		@Param("resolvedOn") java.math.BigInteger resolvedOn
    		);
	
	List<CustomerQueryContact> findAllByOrderByCreatedOnDesc();
}