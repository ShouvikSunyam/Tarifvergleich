package com.tarifvergleich.electricity.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tarifvergleich.electricity.exception.InternalServerException;
import com.tarifvergleich.electricity.model.AdminEmailManagement;
import com.tarifvergleich.electricity.model.Customer;
import com.tarifvergleich.electricity.model.CustomerAttorny;
import com.tarifvergleich.electricity.model.CustomerOrder;
import com.tarifvergleich.electricity.model.CustomerServiceRequest;
import com.tarifvergleich.electricity.repository.AdminEmailManagementRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailBodyRender {

	private final AdminEmailManagementRepository adminEmailManagementRepo;
	private final CustomEmailTemplate customEmailTemplate;

	public Map<String, Object> verifyOtpBody(String otp) {

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%VERIFICATION_OTP%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{OTP}", otp);

		String emailBody = customEmailTemplate.generateEmailHtml(adminEmailManagement.getTitle(),
				adminEmailManagement.getSubtitle(), tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", adminEmailManagement.getTitle());
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> conscent365AdvisorBody(String concentUrl, Customer customer) {
		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%CONSENT_360_ADVISOR_SERVICE%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String tempEmailBody = adminEmailManagement.getEmailContent();
		tempEmailBody = tempEmailBody.replace("{SALUTATION}", customer.getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", customer.getLastName());
		tempEmailBody = tempEmailBody.replace("{CONFIRMATION_URL}", concentUrl);
		String emailBody = customEmailTemplate.generateEmailHtml(adminEmailManagement.getTitle(),
				adminEmailManagement.getSubtitle(), tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", adminEmailManagement.getTitle());
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> beratervollmachtBody(CustomerAttorny customerAttorny) {
		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%BERATERVOLLMACHT%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String tempEmailBody = adminEmailManagement.getEmailContent();
		tempEmailBody = tempEmailBody.replace("{SALUTATION}", customerAttorny.getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", customerAttorny.getLastName());
		String emailBody = customEmailTemplate.generateEmailHtml(adminEmailManagement.getTitle(),
				adminEmailManagement.getSubtitle(), tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", adminEmailManagement.getTitle());
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> serviceAnfrageBody(CustomerServiceRequest customerServiceRequest) {

		if (customerServiceRequest == null)
			throw new InternalServerException("Customer service request not found for email body", HttpStatus.OK);

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%SERVICE_ANFRAGE%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String tempEmailBody = adminEmailManagement.getEmailContent();

		Map<String, Object> dateTimeMap = Helper.getLocalDateTimeFromBigInteger(customerServiceRequest.getCreatedOn());

		String formattedDateTime = dateTimeMap.get("monthName").toString() + " " + dateTimeMap.get("date").toString()
				+ " " + dateTimeMap.get("year").toString() + ", at " + dateTimeMap.get("hour").toString() + ":"
				+ dateTimeMap.get("minute").toString() + " " + dateTimeMap.get("amPm").toString();

		tempEmailBody = tempEmailBody.replace("{DATE_TIME}", formattedDateTime);
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_EMAIL}", customerServiceRequest.getCustomer().getEmail());
		tempEmailBody = tempEmailBody.replace("{SERVICE_CONTENT}", customerServiceRequest.getDescription());

		String title = adminEmailManagement.getTitle().replace("{TICKET_NUMBER}",
				customerServiceRequest.getTicketNumber());

		String emailBody = customEmailTemplate.generateEmailHtml(title, adminEmailManagement.getSubtitle(),
				tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();

		response.put("body", emailBody);
		response.put("title", title);
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> orderSignatureBody(Customer customer, String securedToken, Integer customerOrderId) {

		if (customer == null || securedToken == null || securedToken.isEmpty())
			throw new InternalServerException("Error building email body", HttpStatus.OK);

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%ORDER_SIGNATURE%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}",
				customer.getFirstName() + " " + customer.getLastName());

		String signatureUrl = "http://192.168.0.131:4200/order-signature?token=" + securedToken;

		String htmlButtonBlock = "<!--[if mso]>"
				+ "<v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\""
				+ signatureUrl
				+ "\" style=\"height:50px;v-text-anchor:middle;width:240px;\" arcsize=\"10%\" strokecolor=\"#1b5e20\" fillcolor=\"#2e7d32\">"
				+ "<w:anchorlock/>"
				+ "<center style=\"color:#ffffff;font-family:sans-serif;font-size:16px;font-weight:bold;\">Vertrag unterschreiben</center>"
				+ "</v:roundrect>" + "<![endif]-->" + "<a href=\"" + signatureUrl
				+ "\" style=\"background-color:#2e7d32; border-radius:5px; color:#ffffff; display:inline-block; font-family:sans-serif; font-size:16px; font-weight:bold; line-height:50px; text-align:center; text-decoration:none; width:240px; -webkit-text-size-adjust:none; mso-hide:all; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">Vertrag unterschreiben</a>";

		tempEmailBody = tempEmailBody.replace("|Vertrag unterschreiben|", htmlButtonBlock);

		String title = adminEmailManagement.getTitle().replace("{ORDER_NUMBER}", customerOrderId.toString());

		String emailBody = customEmailTemplate.generateEmailHtml(title, adminEmailManagement.getSubtitle(),
				tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", title);
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> contractConfirmationEmailBody(CustomerOrder order) {

		if (order == null)
			throw new InternalServerException("Error creating email body", HttpStatus.OK);

		Map<String, Object> dateTimeMap = Helper.getLocalDateTimeFromBigInteger(order.getAdminPlacedOrderOn());

		String formattedDateTime = dateTimeMap.get("monthName").toString() + " " + dateTimeMap.get("date").toString()
				+ " " + dateTimeMap.get("year").toString() + ", at " + dateTimeMap.get("hour").toString() + ":"
				+ dateTimeMap.get("minute").toString() + " " + dateTimeMap.get("amPm").toString();

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%SERVICE_ANFRAGE_ANTWORT%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String title = adminEmailManagement.getTitle().replace("{ORDER_NUMBER}", order.getId().toString());

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{SALUTATION}", order.getCustomer().getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", order.getCustomer().getLastName());
		tempEmailBody = tempEmailBody.replace("{DATE_TIME}", formattedDateTime);
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_EMAIL}", order.getCustomer().getEmail());
		tempEmailBody = tempEmailBody.replace("{ORDER_NUMBER}", order.getOrderId().toString());

		String emailBody = customEmailTemplate.generateEmailHtml(title, adminEmailManagement.getSubtitle(),
				tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", title);
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> signedContractNotificationBody(CustomerOrder order) {
		if (order == null)
			throw new InternalServerException("Error creating email body", HttpStatus.OK);

		Map<String, Object> dateTimeMap = Helper.getLocalDateTimeFromBigInteger(order.getAdminPlacedOrderOn());

		String formattedDateTime = dateTimeMap.get("monthName").toString() + " " + dateTimeMap.get("date").toString()
				+ " " + dateTimeMap.get("year").toString() + ", at " + dateTimeMap.get("hour").toString() + ":"
				+ dateTimeMap.get("minute").toString() + " " + dateTimeMap.get("amPm").toString();

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%SIGNED_CONTRACT_NOTIFICATION%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String title = adminEmailManagement.getTitle().replace("{ORDER_NUMBER}", order.getOrderId().toString());

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{SALUTATION}", order.getCustomer().getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", order.getCustomer().getLastName());
		tempEmailBody = tempEmailBody.replace("{DATE_TIME}", formattedDateTime);
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_EMAIL}", order.getCustomer().getEmail());
		tempEmailBody = tempEmailBody.replace("{ORDER_NUMBER}", order.getOrderId().toString());

		String emailBody = customEmailTemplate.generateEmailHtml(title, adminEmailManagement.getSubtitle(),
				tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", title);
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> contractUploadReminderBody() {
		return null;
	}

	public Map<String, Object> serviceRequestResponseBody(CustomerServiceRequest serviceRequest) {

		if (serviceRequest == null)
			throw new InternalServerException("Error creating email body", HttpStatus.OK);

		Map<String, Object> dateTimeMap = Helper.getLocalDateTimeFromBigInteger(serviceRequest.getCreatedOn());

		String formattedDateTime = dateTimeMap.get("monthName").toString() + " " + dateTimeMap.get("date").toString()
				+ " " + dateTimeMap.get("year").toString() + ", at " + dateTimeMap.get("hour").toString() + ":"
				+ dateTimeMap.get("minute").toString() + " " + dateTimeMap.get("amPm").toString();

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%SERVICE_ANFRAGE_ANTWORT%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String title = adminEmailManagement.getTitle().replace("{TICKET_NUMBER}", serviceRequest.getTicketNumber());

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{SALUTATION}", serviceRequest.getCustomer().getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", serviceRequest.getCustomer().getLastName());
		tempEmailBody = tempEmailBody.replace("{DATE_TIME}", formattedDateTime);
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_EMAIL}", serviceRequest.getCustomer().getEmail());

		String emailBody = customEmailTemplate.generateEmailHtml(title, adminEmailManagement.getSubtitle(),
				tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", title);
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> serviceRequestReopenBody(CustomerServiceRequest serviceRequest) {

		if (serviceRequest == null)
			throw new InternalServerException("Error creating email body", HttpStatus.OK);

		Map<String, Object> dateTimeMap = Helper.getLocalDateTimeFromBigInteger(serviceRequest.getCreatedOn());

		String formattedDateTime = dateTimeMap.get("monthName").toString() + " " + dateTimeMap.get("date").toString()
				+ " " + dateTimeMap.get("year").toString() + ", at " + dateTimeMap.get("hour").toString() + ":"
				+ dateTimeMap.get("minute").toString() + " " + dateTimeMap.get("amPm").toString();

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%SERVICE_ANFRAGE_REOPENED%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String title = adminEmailManagement.getTitle().replace("{TICKET_NUMBER}", serviceRequest.getTicketNumber());

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{SALUTATION}", serviceRequest.getCustomer().getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", serviceRequest.getCustomer().getLastName());
		tempEmailBody = tempEmailBody.replace("{DATE_TIME}", formattedDateTime);
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_EMAIL}", serviceRequest.getCustomer().getEmail());

		String emailBody = customEmailTemplate.generateEmailHtml(title, adminEmailManagement.getSubtitle(),
				tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", title);
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> resetPasswordConfirmationBody(Customer customer) {

		if (customer == null)
			throw new InternalServerException("Error creating email body", HttpStatus.OK);

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%RESET_PASSWORD_CONFIRMATION%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{SALUTATION}", customer.getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", customer.getLastName());

		String emailBody = customEmailTemplate.generateEmailHtml(adminEmailManagement.getTitle(),
				adminEmailManagement.getSubtitle(), tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", adminEmailManagement.getTitle());
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> forgotPasswordBody(Customer customer, String token) {

		if (customer == null)
			throw new InternalServerException("Error creating email body", HttpStatus.OK);

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%FORGOT_PASSWORD_LINK%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{SALUTATION}", customer.getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", customer.getLastName());
		tempEmailBody = tempEmailBody.replace("{RESET_LINK}",
				"http://192.168.0.131:4200/forgot-old-password?token=" + token);

		String emailBody = customEmailTemplate.generateEmailHtml(adminEmailManagement.getTitle(),
				adminEmailManagement.getSubtitle(), tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", adminEmailManagement.getTitle());
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> businessCustomerSubAccountRegistrationBody(Customer customer, String token) {

		if (token == null || token.isEmpty())
			throw new InternalServerException("Token not found for email body", HttpStatus.OK);
		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%BUSINESS_CUSTOMER_SUB_ACCOUNT_REGISTRATION%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{INVITATION_LINK}",
				"http://localhost:4200/create-sub-accounts?token=" + token);
		tempEmailBody = tempEmailBody.replace("{COMPANY_NAME}", customer.getCompanyName());

		String title = adminEmailManagement.getTitle().replace("{COMPANY_NAME}", customer.getCompanyName());

		String emailBody = customEmailTemplate.generateEmailHtml(title, adminEmailManagement.getSubtitle(),
				tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", title);
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}

	public Map<String, Object> emailNotificationDeactivationBody(Customer customer, String orderId) {

		if (customer == null)
			throw new InternalServerException("Error creating email body", HttpStatus.OK);

		AdminEmailManagement adminEmailManagement = adminEmailManagementRepo
				.findByCategoryCategorySlugLike("%DEACTIVATING_EMAIL_REMINDER%")
				.orElseThrow(() -> new InternalServerException("Error finding Email body", HttpStatus.OK));

		String title = adminEmailManagement.getTitle().replace("{ORDER_NUMBER}", orderId);

		String tempEmailBody = adminEmailManagement.getEmailContent();

		tempEmailBody = tempEmailBody.replace("{SALUTATION}", customer.getSalutation());
		tempEmailBody = tempEmailBody.replace("{CUSTOMER_NAME}", customer.getLastName());
		tempEmailBody = tempEmailBody.replace("{ORDER_NUMBER}", orderId);

		String emailBody = customEmailTemplate.generateEmailHtml(title, adminEmailManagement.getSubtitle(),
				tempEmailBody);

		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("body", emailBody);
		response.put("title", adminEmailManagement.getTitle());
		response.put("docs", adminEmailManagement.getDocuments());

		return response;
	}
}
