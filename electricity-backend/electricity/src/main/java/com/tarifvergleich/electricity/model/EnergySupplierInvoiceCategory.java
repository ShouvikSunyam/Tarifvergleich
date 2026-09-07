package com.tarifvergleich.electricity.model;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tarifvergleich.electricity.util.Helper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "energy_supplier_invoice_categories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EnergySupplierInvoiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "category_name")
    private String categoryName;
    
    @Column(name = "created_on")
    private BigInteger createdOn;
    
    @Column(name = "updated_on")
    private BigInteger updatedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    @JsonIgnore
    private AdminUser admin;
    
    @OneToMany(mappedBy = "energySupplierInvoiceCategory",  cascade = { CascadeType.PERSIST, CascadeType.REMOVE,
			CascadeType.MERGE }, orphanRemoval = true)
    @JsonIgnoreProperties("energySupplierInvoiceCategory")
    private List<CustomerInvoiceRequest> customerInvoiceRequests;

    @PrePersist
    protected void onCreate() {
        createdOn = Helper.getCurrentTimeBerlin();
    }

    @PreUpdate
    public void updatedon() {
        this.updatedOn = Helper.getCurrentTimeBerlin();
    }
}
