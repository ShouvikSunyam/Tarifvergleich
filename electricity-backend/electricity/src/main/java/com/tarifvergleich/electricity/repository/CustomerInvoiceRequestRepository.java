package com.tarifvergleich.electricity.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tarifvergleich.electricity.model.CustomerInvoiceRequest;

@Repository
public interface CustomerInvoiceRequestRepository extends JpaRepository<CustomerInvoiceRequest, Integer> {

	List<CustomerInvoiceRequest> findByDeliveryId(Integer deliveryId);

	List<CustomerInvoiceRequest> findByDeliveryIdIn(List<Integer> deliveryIds);

	@Query(value = "SELECT cir.* FROM customer_invoice_request cir"
			+ " LEFT JOIN customer c ON cir.customer_id = c.customer_id "
			+ " LEFT JOIN energy_supplier_invoice_categories ic ON cir.id = ic.energy_supplier_invoice_category_id "
			+ " WHERE LOWER(ic.category_name) LIKE LOWER(CONCAT('%', :search, '%'))"
			+ " OR LOWER(cir.message) LIKE LOWER(CONCAT('%', :search, '%'))"
			+ " OR LOWER(c.first_name) LIKE LOWER(CONCAT('%', :search, '%'))"
			+ " OR LOWER(c.last_name) LIKE LOWER(CONCAT('%', :search, '%'))"
			+ " OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))"
			+ " ORDER BY cir.id DESC"
			, nativeQuery = true)
	List<CustomerInvoiceRequest> searchInvoiceRequests(@Param("search") String search);
	
	List<CustomerInvoiceRequest> findAllByOrderByIdDesc();
}
