package com.tarifvergleich.electricity.service.admin;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tarifvergleich.electricity.dto.CancellationServiceCategoryDto;
import com.tarifvergleich.electricity.dto.CancellationServiceCategoryDto.CancellationServiceCategoryAdminResponseDto;
import com.tarifvergleich.electricity.dto.CustomerRequestCounsellingDto;
import com.tarifvergleich.electricity.dto.CustomerRequestCounsellingDto.CustomerRequestCousellingResponseForAdmin;
import com.tarifvergleich.electricity.dto.CustomerServicesDto;
import com.tarifvergleich.electricity.dto.CustomerServicesDto.CustomerListOfServiceForAdminResDto;
import com.tarifvergleich.electricity.dto.EnergySupplierInvoiceCategoryDto;
import com.tarifvergleich.electricity.dto.EnergySupplierMessageCategoryDto;
import com.tarifvergleich.electricity.dto.EnergySupplierMessageCategoryDto.EnergySupplierMessageCategoryAdminResponseDto;
import com.tarifvergleich.electricity.dto.ListOfHolidaysDto;
import com.tarifvergleich.electricity.dto.ListOfHolidaysDto.ListOfHolidaysResponseDto;
import com.tarifvergleich.electricity.dto.ManageAdminDocumentDto;
import com.tarifvergleich.electricity.dto.ManageAdminDocumentDto.ManageAdminDocumentResDto;
import com.tarifvergleich.electricity.dto.ReportMeterReadingCategoryDto;
import com.tarifvergleich.electricity.dto.ReportMeterReadingCategoryDto.ReportMeterReadingCategoryAdminResponseDto;
import com.tarifvergleich.electricity.exception.InternalServerException;
import com.tarifvergleich.electricity.model.AdminUser;
import com.tarifvergleich.electricity.model.CancellationServiceCategory;
import com.tarifvergleich.electricity.model.CustomerRequestCounselling;
import com.tarifvergleich.electricity.model.CustomerServices;
import com.tarifvergleich.electricity.model.EnergySupplierInvoiceCategory;
import com.tarifvergleich.electricity.model.EnergySupplierMessageCategory;
import com.tarifvergleich.electricity.model.ListOfHolidays;
import com.tarifvergleich.electricity.model.ManageAdminDocument;
import com.tarifvergleich.electricity.model.ReportMeterReadingCategory;
import com.tarifvergleich.electricity.repository.AdminUserRepository;
import com.tarifvergleich.electricity.repository.CancellationServiceCategoryRepo;
import com.tarifvergleich.electricity.repository.CustomerRequestCounsellingRepository;
import com.tarifvergleich.electricity.repository.CustomerServicesRepository;
import com.tarifvergleich.electricity.repository.EnergySupplierInvoiceCategoryRepository;
import com.tarifvergleich.electricity.repository.EnergySupplierMessageCategoryRepository;
import com.tarifvergleich.electricity.repository.ListOfHolidaysRepository;
import com.tarifvergleich.electricity.repository.ManageAdminDocumentRepository;
import com.tarifvergleich.electricity.repository.ReportMeterReadingCategoryRepo;
import com.tarifvergleich.electricity.util.Helper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServicePointManagementService {

	private final AdminUserRepository adminUserRepo;
	private final CustomerServicesRepository customerServicesRepo;
	private final Helper helper;
	private final ListOfHolidaysRepository listOfHolidaysRepo;
	private final CustomerRequestCounsellingRepository customerRequestCounsellingRepo;
	private final ManageAdminDocumentRepository adminDocumentRepo;
	private final EnergySupplierMessageCategoryRepository energySupplierMessageCategoryRepo;
	private final EnergySupplierInvoiceCategoryRepository energySupplierInvoiceCategoryRepo;
	private final ReportMeterReadingCategoryRepo reportMeterReadingCategoryRepo;
	private final CancellationServiceCategoryRepo cancellationServiceCategoryRepo;

	@Transactional
	public Map<String, Object> addCustomerServices(CustomerServicesDto servicesDto) {

		if (servicesDto.getAdminId() == null || servicesDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (servicesDto.getServiceName() == null || servicesDto.getServiceName().isEmpty())
			throw new InternalServerException("Service name missing", HttpStatus.OK);
		if (servicesDto.getServiceType() == null || (!servicesDto.getServiceType().equalsIgnoreCase("general")
				&& !servicesDto.getServiceType().equalsIgnoreCase("delivery")
				&& !servicesDto.getServiceType().equalsIgnoreCase("all")))
			throw new InternalServerException("Service type missing", HttpStatus.OK);

		AdminUser admin = adminUserRepo.findById(servicesDto.getAdminId())
				.orElseThrow(() -> new InternalServerException("Admin not found with this credential", HttpStatus.OK));

		// Check for duplicate service name
		Optional<CustomerServices> existingServiceOpt = customerServicesRepo
				.findByServiceNameIgnoreCaseAndAdminAdminId(servicesDto.getServiceName(), servicesDto.getAdminId());
		if (existingServiceOpt.isPresent()) {
			if (servicesDto.getServiceId() == null
					|| !existingServiceOpt.get().getId().equals(servicesDto.getServiceId())) {
				throw new InternalServerException("This service already exists", HttpStatus.OK);
			}
		}

		CustomerServices service = null;
		if (servicesDto.getServiceId() != null && servicesDto.getServiceId() > 0) {
			service = customerServicesRepo.findById(servicesDto.getServiceId())
					.orElseThrow(() -> new InternalServerException("Invalid service id", HttpStatus.OK));
			if (!service.getAdmin().getAdminId().equals(admin.getAdminId()))
				throw new InternalServerException("Admin and service's admin mis-match", HttpStatus.OK);
			service.setUpdatedOn(Helper.getCurrentTimeBerlin());
		} else {
			service = new CustomerServices();
			service.setAdmin(admin);
		}

		service.setServiceName(servicesDto.getServiceName());
		service.setServiceType(servicesDto.getServiceType().toUpperCase());

		service = customerServicesRepo.save(service);

		return Map.of("res", true, "message", "Service added successfully", "serviceId", service.getId());
	}

	@Transactional
	public Map<String, Object> removeCustomerService(CustomerServicesDto servicesDto) {

		if (servicesDto.getAdminId() == null || servicesDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);
		if (servicesDto.getServiceId() == null || servicesDto.getServiceId() <= 0)
			throw new InternalServerException("Service is missing", HttpStatus.OK);

		CustomerServices service = customerServicesRepo.findById(servicesDto.getServiceId())
				.orElseThrow(() -> new InternalServerException("Customer service not found", HttpStatus.OK));

		if (!service.getAdmin().getAdminId().equals(servicesDto.getAdminId()))
			throw new InternalServerException("Admin does not contain this customer service", HttpStatus.OK);

		customerServicesRepo.deleteById(servicesDto.getServiceId());

		return Map.of("res", true, "message", "Customer service removed successfully");
	}

	public Map<String, Object> fetchServices(CustomerServicesDto servicesDto) {

		if (servicesDto.getAdminId() == null || servicesDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (servicesDto.getServiceId() != null && servicesDto.getServiceId() > 0) {

			CustomerServices service = customerServicesRepo
					.findByIdAndAdminAdminId(servicesDto.getServiceId(), servicesDto.getAdminId()).orElseThrow(
							() -> new InternalServerException("Service not found with this credential", HttpStatus.OK));

			return Map.of("res", true, "data", CustomerServicesDto.mapCustomerServiceForAdmin(service));
		}

		if (servicesDto.getPage() != null) {

			if (servicesDto.getSize() == null || servicesDto.getSize() <= 0)
				servicesDto.setSize(10);

			Pageable pageable = PageRequest.of(servicesDto.getPage() - 1, servicesDto.getSize(),
					Sort.by("addedOn").descending());

			Page<CustomerServices> servicesPage = customerServicesRepo.findAllByAdminAdminId(servicesDto.getAdminId(),
					pageable);

			List<CustomerServices> services = servicesPage.getContent();

			List<CustomerListOfServiceForAdminResDto> servicesResponse = services.stream()
					.map(CustomerServicesDto::mapCustomerServiceForAdmin).toList();

			return Map.of("res", true, "data", servicesResponse, "page", servicesPage.getPageable().getPageNumber() + 1,
					"totalPage", servicesPage.getTotalPages());

		}

		List<CustomerServices> services = customerServicesRepo
				.findAllByAdminAdminIdOrderByAddedOnDesc(servicesDto.getAdminId());

		List<CustomerListOfServiceForAdminResDto> servicesResponse = services.stream()
				.map(CustomerServicesDto::mapCustomerServiceForAdmin).toList();

		return Map.of("res", true, "data", servicesResponse);
	}

	public Map<String, Object> getServiceById(CustomerServicesDto servicesDto) {
		if (servicesDto.getAdminId() == null || servicesDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (servicesDto.getServiceId() == null || servicesDto.getServiceId() <= 0)
			throw new InternalServerException("Service id missing", HttpStatus.OK);

		CustomerServices service = customerServicesRepo
				.findByIdAndAdminAdminId(servicesDto.getServiceId(), servicesDto.getAdminId()).orElseThrow(
						() -> new InternalServerException("Service not found with this credential", HttpStatus.OK));

		return Map.of("res", true, "data", CustomerServicesDto.mapCustomerServiceForAdmin(service));
	}

	@Transactional
	public Map<String, Object> adminAddHolidays(ListOfHolidaysDto holidaysDto) {
		if (holidaysDto.getAdminId() == null || holidaysDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		Map<String, Object> dateDetail = Helper.getLocalDateTimeFromBigInteger(Helper.getCurrentTimeBerlin());

		Integer currentYear = (Integer) dateDetail.get("year");
		Integer year = holidaysDto.getStartDate().getYear();

		if (year < currentYear)
			throw new InternalServerException("Provide present or future dates", HttpStatus.OK);

		if (holidaysDto.getStartDate() == null || holidaysDto.getEndDate() == null)
			throw new InternalServerException("Provide valid starting and ending date", HttpStatus.OK);

		if (holidaysDto.getHolidayType() == null || holidaysDto.getHolidayType().isEmpty()
				|| (!holidaysDto.getHolidayType().equalsIgnoreCase("PUBLIC")
						&& !holidaysDto.getHolidayType().equalsIgnoreCase("COMPANY")
						&& !holidaysDto.getHolidayType().equalsIgnoreCase("OPTION")))
			throw new InternalServerException("Holiday type missing", HttpStatus.OK);

		LocalDate start = holidaysDto.getStartDate();
		LocalDate end = holidaysDto.getEndDate();

		if (start.isAfter(end)) {
			throw new InternalServerException("Start date cannot be after end date", HttpStatus.OK);
		}

		if (holidaysDto.getName() == null || holidaysDto.getName().isEmpty())
			throw new InternalServerException("Holiday name missing", HttpStatus.OK);

		AdminUser admin = adminUserRepo.findById(holidaysDto.getAdminId())
				.orElseThrow(() -> new InternalServerException("Admin not found with this credentials", HttpStatus.OK));

		String rangeId = null;

		if (holidaysDto.getRangeId() != null && !holidaysDto.getRangeId().isEmpty()) {

			List<ListOfHolidays> existingHolidays = listOfHolidaysRepo
					.findAllByAdminAdminIdAndRangeIdOrderByIdAsc(holidaysDto.getAdminId(), holidaysDto.getRangeId());

			if (existingHolidays == null || existingHolidays.isEmpty())
				throw new InternalServerException("Existing data not found with this credentials", HttpStatus.OK);

			rangeId = holidaysDto.getRangeId();

			Map<BigInteger, ListOfHolidays> holidayMap = existingHolidays.stream()
					.collect(Collectors.toMap(ListOfHolidays::getStartDate, h -> h));

			List<ListOfHolidays> toSave = new ArrayList<>();

			BigInteger targetStart = helper.toGermanTimestampWithDynamicTime(start, 0, 0);
			BigInteger targetEnd = helper.toGermanTimestampWithDynamicTime(end, 23, 59);

			List<ListOfHolidays> toDelete = existingHolidays.stream().filter(
					h -> h.getStartDate().compareTo(targetStart) < 0 || h.getStartDate().compareTo(targetEnd) > 0)
					.collect(Collectors.toList());

			if (!toDelete.isEmpty()) {
				listOfHolidaysRepo.deleteAll(toDelete);
			}

			for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

				BigInteger currentDayStart = helper.toGermanTimestampWithDynamicTime(date, 0, 0);
				BigInteger currentDayEnd = helper.toGermanTimestampWithDynamicTime(date, 23, 59);

				if (holidayMap.containsKey(currentDayStart)) {
					ListOfHolidays existing = holidayMap.get(currentDayStart);
					existing.setName(holidaysDto.getName());
					existing.setHolidayType(holidaysDto.getHolidayType().toUpperCase());
					existing.setYear(date.getYear());
					existing.setEndDate(currentDayEnd);
					toSave.add(existing);
				} else {
					ListOfHolidays newHoliday = ListOfHolidays.builder().name(holidaysDto.getName())
							.startDate(currentDayStart).endDate(currentDayEnd).year(date.getYear())
							.holidayType(holidaysDto.getHolidayType().toUpperCase()).rangeId(holidaysDto.getRangeId())
							.admin(admin).build();
					toSave.add(newHoliday);
				}
			}

			listOfHolidaysRepo.saveAll(toSave);
		}

		else {
			List<ListOfHolidays> toSave = new ArrayList<>();

			rangeId = Helper.getUniqueId();

			for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

				BigInteger currentDayStart = helper.toGermanTimestampWithDynamicTime(date, 0, 0);
				BigInteger currentDayEnd = helper.toGermanTimestampWithDynamicTime(date, 23, 59);

				ListOfHolidays holiday = listOfHolidaysRepo
						.findByAdminAdminIdAndStartDate(admin.getAdminId(), currentDayStart).orElse(null);

				if (holiday == null) {
					ListOfHolidays newHoliday = ListOfHolidays.builder().name(holidaysDto.getName())
							.startDate(currentDayStart).endDate(currentDayEnd).year(date.getYear())
							.holidayType(holidaysDto.getHolidayType().toUpperCase()).rangeId(rangeId).admin(admin)
							.build();
					toSave.add(newHoliday);
				} else {
					holiday.setName(holidaysDto.getName());
					holiday.setHolidayType(holidaysDto.getHolidayType().toUpperCase());
					holiday.setYear(date.getYear());
					holiday.setEndDate(currentDayEnd);
					holiday.setRangeId(rangeId);
					toSave.add(holiday);
				}
			}

			listOfHolidaysRepo.saveAll(toSave);
		}

		return Map.of("res", true, "message", "Holidays added successfully", "rangeId", rangeId);
	}

	public Map<String, Object> adminGetHolidayList(Integer adminId, Integer year) {

		if (adminId == null || adminId <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		List<ListOfHolidays> holidays = listOfHolidaysRepo.findAllByAdminAdminIdAndYearOrderByStartDateAsc(adminId,
				year);

		if (holidays == null || holidays.isEmpty())
			return Map.of("res", true, "data", List.of());

		Map<String, List<ListOfHolidaysResponseDto>> holidayResponse = holidays.stream()
				.map(ListOfHolidaysDto::mapAdminListOfHolidays).collect(Collectors
						.groupingBy(ListOfHolidaysDto::getMonthName, LinkedHashMap::new, Collectors.toList()));

		return Map.of("res", true, "data", holidayResponse);
	}

	@Transactional
	public Map<String, Object> adminDeleteHolidays(ListOfHolidaysDto holidaysDto) {

		if (holidaysDto.getAdminId() == null || holidaysDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (holidaysDto.getHolidayId() == null || holidaysDto.getHolidayId() <= 0)
			throw new InternalServerException("Holiday id missing", HttpStatus.OK);

		if (!listOfHolidaysRepo.existsByIdAndAdminAdminId(holidaysDto.getHolidayId(), holidaysDto.getAdminId()))
			throw new InternalServerException("Holiday record not found with this credential", HttpStatus.OK);

		listOfHolidaysRepo.deleteByIdAndAdminAdminId(holidaysDto.getHolidayId(), holidaysDto.getAdminId());

		return Map.of("res", true, "message", "Holiday removed successfully");
	}

	public Map<String, Object> fetchCounsellingrequets(CustomerRequestCounsellingDto requestDto) {

		if (requestDto.getAdminId() == null || requestDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		Long totalConcluded = customerRequestCounsellingRepo.countAllByAdminAdminIdAndConcluded(requestDto.getAdminId(),
				true);
		Long totalUnconcluded = customerRequestCounsellingRepo
				.countAllByAdminAdminIdAndConcluded(requestDto.getAdminId(), false);
		Long totalRequests = customerRequestCounsellingRepo.count();

		if (requestDto.getPage() != null && requestDto.getPage() > 0) {
			if (requestDto.getSize() == null || requestDto.getSize() <= 0)
				requestDto.setSize(10);

			Pageable pageable = PageRequest.of(requestDto.getPage() - 1, requestDto.getSize(),
					Sort.by("createdOn").descending());
			Page<CustomerRequestCounselling> counsellingRequests = customerRequestCounsellingRepo
					.findAllByAdminAdminIdAndOptionalConclude(requestDto.getAdminId(), requestDto.getConcluded(),
							pageable);

			Page<CustomerRequestCousellingResponseForAdmin> mappedRequest = counsellingRequests
					.map(CustomerRequestCounsellingDto::mapCustomerRequestCounsellingResponseForAdmin);

			return Map.of("res", true, "data", mappedRequest.getContent(), "page",
					mappedRequest.getPageable().getPageNumber() + 1, "totalPage", mappedRequest.getTotalPages(),
					"totalRecords", totalRequests, "totalConsluded", totalConcluded, "totalUnconcluded",
					totalUnconcluded);

		}

		List<CustomerRequestCounselling> counsellingRequests = customerRequestCounsellingRepo
				.findAllByAdminAdminIdAndOptionalConcludedOrderByCreatedOnDesc(requestDto.getAdminId(),
						requestDto.getConcluded());

		List<CustomerRequestCousellingResponseForAdmin> mappedRequest = counsellingRequests.stream()
				.map(CustomerRequestCounsellingDto::mapCustomerRequestCounsellingResponseForAdmin).toList();

		return Map.of("res", true, "data", mappedRequest, "totalRecords", totalRequests, "totalConsluded",
				totalConcluded, "totalUnconcluded", totalUnconcluded);
	}

	@Transactional
	public Map<String, Object> toggleCustomerRequestCounsellingConcluded(Integer adminId, Integer counsellingId,
			Boolean setConclusion) {

		if (adminId == null || adminId <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (counsellingId == null || counsellingId <= 0)
			throw new InternalServerException("Counselling id missing", HttpStatus.OK);

		CustomerRequestCounselling counsellingRequest = customerRequestCounsellingRepo
				.findByIdAndAdminAdminId(counsellingId, adminId).orElseThrow(
						() -> new InternalServerException("Request not found with this credential", HttpStatus.OK));

		if (setConclusion == null)
			setConclusion = !counsellingRequest.getConcluded();
		else if (setConclusion.equals(counsellingRequest.getConcluded()))
			return Map.of("res", true, "message", "Request status updated successfully");

		Integer result = customerRequestCounsellingRepo.updateConcludedByAdminAdminIdAndId(counsellingId, adminId,
				setConclusion);

		if (result == null || result <= 0)
			throw new InternalServerException("Internal Server issue", HttpStatus.OK);

		return Map.of("res", true, "message", "Request status updated successfully");
	}

	public Map<String, Object> fetchAllAdminDocuments(ManageAdminDocumentDto adminDocDto) {

		if (adminDocDto.getAdminId() == null || adminDocDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (adminDocDto.getAdminDocId() != null && adminDocDto.getAdminDocId() > 0) {
			ManageAdminDocument adminDoc = adminDocumentRepo
					.findByIdAndAdminAdminId(adminDocDto.getAdminDocId(), adminDocDto.getAdminId())
					.orElseThrow(() -> new InternalServerException("Admin document not found with this credential",
							HttpStatus.OK));

			ManageAdminDocumentResDto adminDocRes = ManageAdminDocumentDto.mapForAdmin(adminDoc);

			return Map.of("res", true, "data", adminDocRes);
		}

		else if (adminDocDto.getPage() != null && adminDocDto.getPage() > 0) {
			int size = adminDocDto.getSize() != null && adminDocDto.getSize() > 0 ? adminDocDto.getSize() : 10;

			Pageable pageable = PageRequest.of(adminDocDto.getPage() - 1, size,
					Sort.by("documentCategory").ascending());

			Page<ManageAdminDocument> adminDocs = adminDocumentRepo.findAllByAdminAdminId(adminDocDto.getAdminId(),
					pageable);

			Page<ManageAdminDocumentResDto> adminDocsRes = adminDocs.map(ManageAdminDocumentDto::mapForAdmin);

			return Map.of("res", true, "data", adminDocsRes.getContent(), "page",
					adminDocsRes.getPageable().getPageNumber() + 1, "totalPage", adminDocsRes.getTotalPages(),
					"totalRecord", adminDocsRes.getTotalElements());
		}

		List<ManageAdminDocument> adminDocs = adminDocumentRepo
				.findAllByAdminAdminIdOrderByDocumentCategoryAsc(adminDocDto.getAdminId());
		List<ManageAdminDocumentResDto> adminDocsRes = adminDocs.stream().map(ManageAdminDocumentDto::mapForAdmin)
				.toList();

		return Map.of("res", true, "data", adminDocsRes);
	}

	@Transactional
	public Map<String, Object> addSupplierMessageCategory(EnergySupplierMessageCategoryDto energyMessageCategoryDto) {

		if (energyMessageCategoryDto == null)
			throw new InternalServerException("Category data missing", HttpStatus.OK);
		if (energyMessageCategoryDto.getCategoryName() == null || energyMessageCategoryDto.getCategoryName().isEmpty())
			throw new InternalServerException("Category name missing", HttpStatus.OK);
		if (energyMessageCategoryDto.getAdminId() == null || energyMessageCategoryDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		EnergySupplierMessageCategory supplierMessageCategory = null;

		if (energyMessageCategoryDto.getSupplierMessageCategoryId() != null
				&& energyMessageCategoryDto.getSupplierMessageCategoryId() > 0) {

			supplierMessageCategory = energySupplierMessageCategoryRepo
					.findById(energyMessageCategoryDto.getSupplierMessageCategoryId()).orElse(null);
		} else {

			supplierMessageCategory = energySupplierMessageCategoryRepo.findByCategoryNameLikeAndAdminAdminId(
					energyMessageCategoryDto.getCategoryName(), energyMessageCategoryDto.getAdminId()).orElse(null);
		}

		if (supplierMessageCategory == null && (energyMessageCategoryDto.getSupplierMessageCategoryId() == null
				|| energyMessageCategoryDto.getSupplierMessageCategoryId() <= 0)) {

			AdminUser admin = adminUserRepo.findById(energyMessageCategoryDto.getAdminId()).orElseThrow(
					() -> new InternalServerException("Admin not found with this credential", HttpStatus.OK));

			supplierMessageCategory = EnergySupplierMessageCategory.builder()
					.categoryName(energyMessageCategoryDto.getCategoryName()).admin(admin).build();
		} else {
			supplierMessageCategory = energySupplierMessageCategoryRepo
					.findByIdAndAdminAdminId(energyMessageCategoryDto.getSupplierMessageCategoryId(),
							energyMessageCategoryDto.getAdminId())
					.orElseThrow(() -> new InternalServerException(
							"Energy supplier message not found with this credential", HttpStatus.OK));

			supplierMessageCategory.setCategoryName(energyMessageCategoryDto.getCategoryName());
		}

		supplierMessageCategory = energySupplierMessageCategoryRepo.save(supplierMessageCategory);

		return Map.of("res", true, "message", "Energy supplier message category added successfully");
	}

	@Transactional
	public Map<String, Object> deleteEnergySupplierMessageCategory(
			EnergySupplierMessageCategoryDto energyMessageCategoryDto) {

		if (energyMessageCategoryDto == null)
			throw new InternalServerException("Insufficient credential", HttpStatus.OK);
		if (energyMessageCategoryDto.getAdminId() == null || energyMessageCategoryDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);
		if (energyMessageCategoryDto.getSupplierMessageCategoryId() == null
				|| energyMessageCategoryDto.getSupplierMessageCategoryId() <= 0)
			throw new InternalServerException("Energy supplier message id missing", HttpStatus.OK);

		EnergySupplierMessageCategory messageCategory = energySupplierMessageCategoryRepo
				.findByIdAndAdminAdminId(energyMessageCategoryDto.getSupplierMessageCategoryId(),
						energyMessageCategoryDto.getAdminId())
				.orElseThrow(() -> new InternalServerException(
						"Energy supplier message category not found with this credential", HttpStatus.OK));
		try {
			energySupplierMessageCategoryRepo.delete(messageCategory);
			energySupplierMessageCategoryRepo.flush();
		} catch (DataIntegrityViolationException e) {
			System.err.println("Association Exists");
			throw new InternalServerException("Supplier message exists with this category", HttpStatus.OK);
		}

		return Map.of("res", true, "message", "Energy supplier message category deleted successfully");
	}

	public Map<String, Object> fetchAllEnergySupplierCategory(
			EnergySupplierMessageCategoryDto energySupplierMessageCategoryDto) {
		if (energySupplierMessageCategoryDto == null || energySupplierMessageCategoryDto.getAdminId() == null
				|| energySupplierMessageCategoryDto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (energySupplierMessageCategoryDto.getSupplierMessageCategoryId() != null
				&& energySupplierMessageCategoryDto.getSupplierMessageCategoryId() > 0) {
			EnergySupplierMessageCategory category = energySupplierMessageCategoryRepo
					.findByIdAndAdminAdminId(energySupplierMessageCategoryDto.getSupplierMessageCategoryId(),
							energySupplierMessageCategoryDto.getAdminId())
					.orElseThrow(() -> new InternalServerException(
							"Energy supplier message category not found with this credential", HttpStatus.OK));

			EnergySupplierMessageCategoryAdminResponseDto responseCategory = EnergySupplierMessageCategoryDto
					.mapForAdmin(category);

			return Map.of("res", true, "data", responseCategory);

		}

		List<EnergySupplierMessageCategory> categories = energySupplierMessageCategoryRepo
				.findAllByAdminAdminIdOrderByIdAsc(energySupplierMessageCategoryDto.getAdminId());

		List<EnergySupplierMessageCategoryAdminResponseDto> responseCategory = categories.stream()
				.map(EnergySupplierMessageCategoryDto::mapForAdmin).toList();

		return Map.of("res", true, "data", responseCategory);
	}

	@Transactional
	public Map<String, Object> addSupplierInvoiceCategory(EnergySupplierInvoiceCategoryDto dto) {
		if (dto == null || dto.getCategoryName() == null || dto.getCategoryName().isEmpty())
			throw new InternalServerException("Category name missing", HttpStatus.OK);
		if (dto.getAdminId() == null || dto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		EnergySupplierInvoiceCategory category = null;

		if (dto.getInvoiceCategoryId() != null && dto.getInvoiceCategoryId() > 0) {

			category = energySupplierInvoiceCategoryRepo.findById(dto.getInvoiceCategoryId()).orElse(null);
		} else {
			category = energySupplierInvoiceCategoryRepo
					.findByCategoryNameLikeAndAdminAdminId(dto.getCategoryName(), dto.getAdminId()).orElse(null);
		}

		if (dto.getInvoiceCategoryId() == null || dto.getInvoiceCategoryId() <= 0) {
			AdminUser admin = adminUserRepo.findById(dto.getAdminId())
					.orElseThrow(() -> new InternalServerException("Admin not found", HttpStatus.OK));
			category = EnergySupplierInvoiceCategory.builder().categoryName(dto.getCategoryName()).admin(admin).build();
		} else {

			category = energySupplierInvoiceCategoryRepo
					.findByIdAndAdminAdminId(dto.getInvoiceCategoryId(), dto.getAdminId())
					.orElseThrow(() -> new InternalServerException("Category not found", HttpStatus.OK));
			category.setCategoryName(dto.getCategoryName());
		}

		energySupplierInvoiceCategoryRepo.save(category);
		return Map.of("res", true, "message", "Invoice category saved successfully");
	}

	@Transactional
	public Map<String, Object> deleteSupplierInvoiceCategory(EnergySupplierInvoiceCategoryDto dto) {
		if (dto == null || dto.getAdminId() == null || dto.getInvoiceCategoryId() == null)
			throw new InternalServerException("Insufficient credentials", HttpStatus.OK);

		EnergySupplierInvoiceCategory category = energySupplierInvoiceCategoryRepo
				.findByIdAndAdminAdminId(dto.getInvoiceCategoryId(), dto.getAdminId())
				.orElseThrow(() -> new InternalServerException("Category not found", HttpStatus.OK));

		energySupplierInvoiceCategoryRepo.delete(category);
		return Map.of("res", true, "message", "Invoice category deleted successfully");
	}

	public Map<String, Object> fetchAllSupplierInvoiceCategory(EnergySupplierInvoiceCategoryDto dto) {
		if (dto == null || dto.getAdminId() == null || dto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		List<EnergySupplierInvoiceCategory> categories = energySupplierInvoiceCategoryRepo
				.findAllByAdminAdminIdOrderByIdAsc(dto.getAdminId());
		List<EnergySupplierInvoiceCategoryDto.InvoiceSupplierCategoryAdminResponseDto> response = categories.stream()
				.map(EnergySupplierInvoiceCategoryDto::mapForAdminRes).toList();
		return Map.of("res", true, "data", response);
	}

	@Transactional
	public Map<String, Object> addReportMeterReadingCategory(ReportMeterReadingCategoryDto dto) {
		if (dto == null || dto.getCategoryName() == null || dto.getCategoryName().isEmpty())
			throw new InternalServerException("Category name missing", HttpStatus.OK);
		if (dto.getAdminId() == null || dto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		ReportMeterReadingCategory category = null;

		if (dto.getReportMeterReadingCategoryId() != null && dto.getReportMeterReadingCategoryId() > 0) {
			category = reportMeterReadingCategoryRepo
					.findByIdAndAdminAdminId(dto.getReportMeterReadingCategoryId(), dto.getAdminId())
					.orElseThrow(() -> new InternalServerException("Category is not Found with this Category Id ",
							HttpStatus.OK));
		} else {

			category = reportMeterReadingCategoryRepo
					.findByCategoryNameLikeAndAdminAdminId(dto.getCategoryName(), dto.getAdminId()).orElse(null);
		}

		if (category == null
				&& (dto.getReportMeterReadingCategoryId() == null || dto.getReportMeterReadingCategoryId() <= 0)) {
			category = new ReportMeterReadingCategory();
			AdminUser admin = adminUserRepo.findById(dto.getAdminId()).orElseThrow(
					() -> new InternalServerException("Admin User is not Found with this Admin Id ", HttpStatus.OK));
			category.setAdmin(admin);
		}

		category.setCategoryName(dto.getCategoryName());

		reportMeterReadingCategoryRepo.save(category);

		return Map.of("res", true, "message", "Category saved successfully");
	}

	public Map<String, Object> fetchAllReportMeterReadingCategory(ReportMeterReadingCategoryDto dto) {
		if (dto == null || dto.getAdminId() == null || dto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		List<ReportMeterReadingCategory> categories = reportMeterReadingCategoryRepo
				.findAllByAdminAdminIdOrderByIdAsc(dto.getAdminId());
		List<ReportMeterReadingCategoryAdminResponseDto> response = categories.stream()
				.map(ReportMeterReadingCategoryDto::mapForAdmin).toList();
		return Map.of("res", true, "data", response);
	}

	@Transactional
	public Map<String, Object> deleteReportMeterReadingCategory(ReportMeterReadingCategoryDto dto) {
		if (dto == null || dto.getAdminId() == null || dto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (dto.getReportMeterReadingCategoryId() == null || dto.getReportMeterReadingCategoryId() <= 0)
			throw new InternalServerException("Category id missing", HttpStatus.OK);

		ReportMeterReadingCategory category = reportMeterReadingCategoryRepo
				.findById(dto.getReportMeterReadingCategoryId())
				.orElseThrow(() -> new InternalServerException("Category is not Found with this Category Id ",
						HttpStatus.OK));

		reportMeterReadingCategoryRepo.delete(category);

		return Map.of("res", true, "message", "Category deleted successfully");
	}

	@Transactional
	public Map<String, Object> addCancellationServiceCategory(CancellationServiceCategoryDto dto) {
		if (dto == null || dto.getCategoryName() == null || dto.getCategoryName().isEmpty())
			throw new InternalServerException("Category name missing", HttpStatus.OK);
		if (dto.getAdminId() == null || dto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		CancellationServiceCategory category = cancellationServiceCategoryRepo
				.findByCategoryNameLikeAndAdminAdminId(dto.getCategoryName().toUpperCase(), dto.getAdminId())
				.orElse(null);

		if (category != null
				&& (dto.getCancellationServiceCategoryId() == null || dto.getCancellationServiceCategoryId() <= 0))
			throw new InternalServerException("Category already exists", HttpStatus.OK);

		if (dto.getCancellationServiceCategoryId() == null || dto.getCancellationServiceCategoryId() <= 0) {
			category = new CancellationServiceCategory();
			AdminUser admin = adminUserRepo.findById(dto.getAdminId()).orElseThrow(
					() -> new InternalServerException("Admin User is not Found with this Admin Id ", HttpStatus.OK));
			category.setAdmin(admin);
		} else {

			if (category != null)
				throw new InternalServerException("Category already exists", HttpStatus.OK);

			category = cancellationServiceCategoryRepo.findById(dto.getCancellationServiceCategoryId()).orElseThrow(
					() -> new InternalServerException("Category is not Found with this Category Id ", HttpStatus.OK));
		}

		category.setCategoryName(dto.getCategoryName().toUpperCase());

		cancellationServiceCategoryRepo.save(category);

		return Map.of("res", true, "message", "Category saved successfully");
	}

	public Map<String, Object> fetchAllCancellationServiceCategory(CancellationServiceCategoryDto dto) {
		if (dto == null || dto.getAdminId() == null || dto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		List<CancellationServiceCategory> categories = cancellationServiceCategoryRepo
				.findAllByAdminAdminIdOrderByCategoryNameAsc(dto.getAdminId());
		List<CancellationServiceCategoryAdminResponseDto> response = categories.stream()
				.map(CancellationServiceCategoryDto::mapForAdminRes).toList();
		return Map.of("res", true, "data", response);
	}

	@Transactional
	public Map<String, Object> deleteCancellationServiceCategory(CancellationServiceCategoryDto dto) {
		if (dto == null || dto.getAdminId() == null || dto.getAdminId() <= 0)
			throw new InternalServerException("Admin id missing", HttpStatus.OK);

		if (dto.getCancellationServiceCategoryId() == null || dto.getCancellationServiceCategoryId() <= 0)
			throw new InternalServerException("Category id missing", HttpStatus.OK);

		CancellationServiceCategory category = cancellationServiceCategoryRepo
				.findById(dto.getCancellationServiceCategoryId())
				.orElseThrow(() -> new InternalServerException("Category is not Found with this Category Id ",
						HttpStatus.OK));

		cancellationServiceCategoryRepo.delete(category);

		return Map.of("res", true, "message", "Category deleted successfully");
	}
}
