package com.tarifvergleich.electricity.dto;

import java.math.BigInteger;

import com.tarifvergleich.electricity.model.EnergySupplierInvoiceCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergySupplierInvoiceCategoryDto {
	 private Integer invoiceCategoryId;
    private Integer adminId;
    private String categoryName;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InvoiceSupplierCategoryAdminResponseDto {
        private Long invoiceCategoryId;
        private String categoryName;
        private BigInteger createdOn;
        private BigInteger updatedOn;
    }
    
    
    public static InvoiceSupplierCategoryAdminResponseDto mapForAdminRes(EnergySupplierInvoiceCategory category) {
    	return InvoiceSupplierCategoryAdminResponseDto.builder()
    			.invoiceCategoryId(Integer.toUnsignedLong(category.getId()))
    			.categoryName(category.getCategoryName())
    			.createdOn(category.getCreatedOn())
    			.updatedOn(category.getUpdatedOn())
    			.build();
    }
}
