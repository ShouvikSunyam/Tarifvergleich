package com.tarifvergleich.electricity.dto.response;

import java.math.BigInteger;
import java.util.List;

import com.tarifvergleich.electricity.dto.CustomerDto.CustomerShortDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerQueryContactResponseDTO {
    private Integer customerQueryContactId;
    private String salutation;
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String inquiry;
    private BigInteger createdOn;
    private Boolean isResolved;
    private BigInteger resolvedOn;
    private String categoryName;
    private Integer CategoryId;
    private CustomerShortDetail customer;
    private List<CustomerShortDetail> customers;
}
