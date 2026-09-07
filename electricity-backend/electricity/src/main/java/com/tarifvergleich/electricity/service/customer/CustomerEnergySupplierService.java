package com.tarifvergleich.electricity.service.customer;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tarifvergleich.electricity.dto.EnergySupplierMessageCategoryDto;
import com.tarifvergleich.electricity.dto.EnergySupplierMessageCategoryDto.EnergySupplierMessageCategoryCustomerResponseDto;
import com.tarifvergleich.electricity.dto.EnergySupplierMessageDto;
import com.tarifvergleich.electricity.exception.InternalServerException;
import com.tarifvergleich.electricity.model.AdminUser;
import com.tarifvergleich.electricity.model.Customer;
import com.tarifvergleich.electricity.model.CustomerDelivery;
import com.tarifvergleich.electricity.model.CustomerOrder;
import com.tarifvergleich.electricity.model.EnergySupplierMessage;
import com.tarifvergleich.electricity.model.EnergySupplierMessageCategory;
import com.tarifvergleich.electricity.repository.CustomerOrderRepository;
import com.tarifvergleich.electricity.repository.EnergySupplierMessageCategoryRepository;
import com.tarifvergleich.electricity.repository.EnergySupplierMessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerEnergySupplierService {

	private final EnergySupplierMessageRepository energySupplierMessageRepo;
	private final CustomerOrderRepository customerOrderRepo;
	private final EnergySupplierMessageCategoryRepository energySupplierMessageCategoryRepo;

	@Transactional
	public Map<String, Object> saveSupplierMessage(EnergySupplierMessageDto supplierMessageDto) {

		if (supplierMessageDto == null)
			throw new InternalServerException("Message not found", HttpStatus.OK);
		if (supplierMessageDto.getSupplierMessageCategoryId() == null
				|| supplierMessageDto.getSupplierMessageCategoryId() <= 0)
			throw new InternalServerException("Message category missing", HttpStatus.OK);
		if (supplierMessageDto.getOrderId() == null || supplierMessageDto.getOrderId() <= 0)
			throw new InternalServerException("Customer order id missing", HttpStatus.OK);
		if (supplierMessageDto.getAdminId() == null || supplierMessageDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);
		if (supplierMessageDto.getMessage() == null || supplierMessageDto.getMessage().isEmpty())
			throw new InternalServerException("Message for supplie missing", HttpStatus.OK);
		if (supplierMessageDto.getCustomerId() == null || supplierMessageDto.getCustomerId() <= 0)
			throw new InternalServerException("Customer id missing", HttpStatus.OK);

		EnergySupplierMessageCategory category = energySupplierMessageCategoryRepo
				.findByIdAndAdminAdminId(supplierMessageDto.getSupplierMessageCategoryId(),
						supplierMessageDto.getAdminId())
				.orElseThrow(() -> new InternalServerException(
						"Supplier message category not found with this credential", HttpStatus.OK));

		EnergySupplierMessage energySupplierMessage = null;
		if (supplierMessageDto.getSupplierMesageId() == null || supplierMessageDto.getSupplierMesageId() <= 0) {

			CustomerOrder order = customerOrderRepo
					.findByOrderIdAndAdminAdminId(supplierMessageDto.getOrderId(), supplierMessageDto.getAdminId())
					.orElseThrow(() -> new InternalServerException("Customer order not found with this credential",
							HttpStatus.OK));

			if (order.getCustomerBookingDocument() == null
					|| !order.getCustomerBookingDocument().getSignedDocumentSubmitted())
				throw new InternalServerException("Signed document missing", HttpStatus.OK);

			if (!order.getCustomer().getCustomerId().equals(supplierMessageDto.getCustomerId()))
				throw new InternalServerException("Customer not found with this credential", HttpStatus.OK);

			if (order.getEnergySupplierMessages() != null && order.getEnergySupplierMessages().size() > 0)
				throw new InternalServerException("A message for the energy supplier is already added", HttpStatus.OK);

			AdminUser admin = order.getAdmin();

			CustomerDelivery delivery = order.getDelivery();

			Customer customer = order.getCustomer();

			energySupplierMessage = EnergySupplierMessage.builder().message(supplierMessageDto.getMessage())
					.category(category).customerOrder(order).customerDelivery(delivery).customer(customer).admin(admin)
					.build();

		} else {
			energySupplierMessage = energySupplierMessageRepo
					.findByIdAndCustomerCustomerIdAndAdminAdminId(supplierMessageDto.getSupplierMesageId(),
							supplierMessageDto.getCustomerId(), supplierMessageDto.getAdminId())
					.orElseThrow(() -> new InternalServerException(
							"Energy supplier message not found with this credential", HttpStatus.OK));

			energySupplierMessage.setCategory(category);
			energySupplierMessage.setMessage(supplierMessageDto.getMessage());
		}

		energySupplierMessage = energySupplierMessageRepo.save(energySupplierMessage);

		return Map.of("res", true, "supplierMessageId", energySupplierMessage.getId(), "message",
				"Message added successfully");
	}

	public Map<String, Object> fetchEnergySupplierMessageCategory(
			EnergySupplierMessageCategoryDto energySupplierMessageCategoryDto) {
		if (energySupplierMessageCategoryDto == null || energySupplierMessageCategoryDto.getAdminId() == null
				|| energySupplierMessageCategoryDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		List<EnergySupplierMessageCategory> categories = energySupplierMessageCategoryRepo
				.findAllByAdminAdminIdOrderByIdAsc(energySupplierMessageCategoryDto.getAdminId());

		List<EnergySupplierMessageCategoryCustomerResponseDto> responseCategory = categories.stream()
				.map(EnergySupplierMessageCategoryDto::mapForGeneral).toList();

		return Map.of("res", true, "data", responseCategory);
	}
}
