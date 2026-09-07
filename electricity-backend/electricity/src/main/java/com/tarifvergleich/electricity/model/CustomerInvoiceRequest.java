package com.tarifvergleich.electricity.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_invoice_request")

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerInvoiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer customerId;

    private Integer connectionId;
    
    private Integer orderId;
    
    private Integer deliveryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "energy_supplier_invoice_category_id")
    @JsonIgnore
    private EnergySupplierInvoiceCategory energySupplierInvoiceCategory;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Integer status;
    
    private LocalDateTime createdAt;
}