package com.tarifvergleich.electricity.service.admin;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tarifvergleich.electricity.dto.CustomerChangeDiscountRequestDto;
import com.tarifvergleich.electricity.dto.CustomerChangeDiscountRequestDto.CustomerChangeDiscountAdminResponseDto;
import com.tarifvergleich.electricity.dto.CustomerContractCancellationRequestDto;
import com.tarifvergleich.electricity.dto.CustomerContractCancellationRequestDto.CustomerContractCancellationRequestAdminResDto;
import com.tarifvergleich.electricity.dto.CustomerContractEditRequestDto;
import com.tarifvergleich.electricity.dto.CustomerContractEditRequestDto.CustomerContractEditRequestAdminResponseDto;
import com.tarifvergleich.electricity.exception.InternalServerException;
import com.tarifvergleich.electricity.model.CustomerChangeDiscountRequest;
import com.tarifvergleich.electricity.model.CustomerContractCancellationRequest;
import com.tarifvergleich.electricity.model.CustomerContractEditRequest;
import com.tarifvergleich.electricity.repository.CustomerChangeDiscountRequestRepository;
import com.tarifvergleich.electricity.repository.CustomerContractCancellationRequestRepository;
import com.tarifvergleich.electricity.repository.CustomerContractEditRequestRepository;
import com.tarifvergleich.electricity.util.FileServiceCustomer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminCustomerRequestManagementService {

	private final CustomerChangeDiscountRequestRepository customerChangeDiscountRequestRepo;
	private final CustomerContractEditRequestRepository customerContractEditRequestRepo;
	private final CustomerContractCancellationRequestRepository customerContractCancellationRequestRepo;
	private final FileServiceCustomer fileServiceCustomer;

	public Map<String, Object> fetchCustomerChangeDiscountRequests(
			CustomerChangeDiscountRequestDto changeDiscountRequestDto) {

		if (changeDiscountRequestDto.getAdminId() == null || changeDiscountRequestDto.getAdminId() <= 0)
			throw new InternalServerException("Admin credential not found", HttpStatus.OK);

		if (changeDiscountRequestDto.getPage() != null && changeDiscountRequestDto.getPage() > 0) {

			if (changeDiscountRequestDto.getSize() == null || changeDiscountRequestDto.getSize() <= 0)
				changeDiscountRequestDto.setSize(10);

			Pageable pageable = PageRequest.of(changeDiscountRequestDto.getPage() - 1,
					changeDiscountRequestDto.getSize());

			Page<CustomerChangeDiscountRequest> requests = customerChangeDiscountRequestRepo
					.findAllByAdminAdminIdOrderByCreatedOnDesc(changeDiscountRequestDto.getAdminId(), pageable);

			Page<CustomerChangeDiscountAdminResponseDto> requestsRep = requests
					.map(CustomerChangeDiscountRequestDto::mapAdminResponse);

			return Map.of("res", true, "data", requestsRep.getContent(), "page",
					requestsRep.getPageable().getPageNumber() + 1, "totalPages", requests.getTotalPages());

		}

		List<CustomerChangeDiscountRequest> requests = customerChangeDiscountRequestRepo
				.findAllByAdminAdminIdOrderByCreatedOnDesc(changeDiscountRequestDto.getAdminId());

		List<CustomerChangeDiscountAdminResponseDto> requestRes = requests.stream()
				.map(CustomerChangeDiscountRequestDto::mapAdminResponse).toList();

		return Map.of("res", true, "data", requestRes);
	}

	public Map<String, Object> fetchCustomerCntractEditRequest(CustomerContractEditRequestDto editContractDto) {

		if (editContractDto == null || editContractDto.getAdminId() == 0)
			throw new InternalServerException("Insufficient credenials", HttpStatus.OK);

		if (editContractDto.getPage() != null && editContractDto.getPage() > 0) {

			if (editContractDto.getSize() == null || editContractDto.getSize() < 1)
				editContractDto.setSize(10);

			Pageable pageable = PageRequest.of(editContractDto.getPage() - 1, editContractDto.getSize());

			Page<CustomerContractEditRequest> requests = customerContractEditRequestRepo
					.findAllByAdminAdminIdOrderByCreatedOnDesc(editContractDto.getAdminId(), pageable);

			Page<CustomerContractEditRequestAdminResponseDto> requestResp = requests
					.map(req -> CustomerContractEditRequestDto.mapAdminResponse(req, fileServiceCustomer));

			return Map.of("res", true, "data", requestResp.getContent(), "page",
					requestResp.getPageable().getPageNumber() + 1, "totalPage", requestResp.getTotalPages());
		}

		List<CustomerContractEditRequest> requests = customerContractEditRequestRepo
				.findAllByAdminAdminIdOrderByCreatedOnDesc(editContractDto.getAdminId());

		List<CustomerContractEditRequestAdminResponseDto> requestRep = requests.stream()
				.map(req -> CustomerContractEditRequestDto.mapAdminResponse(req, fileServiceCustomer)).toList();

		return Map.of("res", true, "data", requestRep);
	}
	
	public Map<String, Object> fetchCustomerContractCancellationRequest(CustomerContractCancellationRequestDto cancellationDto) {

	    if (cancellationDto == null || cancellationDto.getAdminId() == null || cancellationDto.getAdminId() == 0)
	        throw new InternalServerException("Insufficient credentials", HttpStatus.OK);

	    if (cancellationDto.getPage() != null && cancellationDto.getPage() > 0) {

	        if (cancellationDto.getSize() == null || cancellationDto.getSize() < 1)
	            cancellationDto.setSize(10);

	        Pageable pageable = PageRequest.of(cancellationDto.getPage() - 1, cancellationDto.getSize());

	        Page<CustomerContractCancellationRequest> requests = customerContractCancellationRequestRepo
	                .findAllByAdminAdminIdOrderByCreatedOnDesc(cancellationDto.getAdminId(), pageable);

	        Page<CustomerContractCancellationRequestAdminResDto> requestResp = requests
	                .map(CustomerContractCancellationRequestDto::mapAdminRes);

	        return Map.of(
	            "res", true, 
	            "data", requestResp.getContent(), 
	            "page", requestResp.getPageable().getPageNumber() + 1, 
	            "totalPage", requestResp.getTotalPages()
	        );
	    }

	    List<CustomerContractCancellationRequest> requests = customerContractCancellationRequestRepo
	            .findAllByAdminAdminIdOrderByCreatedOnDesc(cancellationDto.getAdminId());

	    List<CustomerContractCancellationRequestAdminResDto> requestResp = requests.stream()
	            .map(CustomerContractCancellationRequestDto::mapAdminRes)
	            .toList();

	    return Map.of("res", true, "data", requestResp);
	}

}
