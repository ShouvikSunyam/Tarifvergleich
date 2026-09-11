package com.tarifvergleich.electricity.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.tarifvergleich.electricity.dto.ServiceRequestEmailEvent;
import com.tarifvergleich.electricity.dto.ServiceRequestEmailEvent.ServiceAttachmentMailOfAcknowledgement;
import com.tarifvergleich.electricity.dto.ServiceRequestEmailEvent.ServiceResponseEmailEvent;
import com.tarifvergleich.electricity.dto.email.AttornyEmailDto;
import com.tarifvergleich.electricity.dto.email.ContractMailDto;
import com.tarifvergleich.electricity.dto.email.VerifyOtpEmail;
import com.tarifvergleich.electricity.model.ManageAdminDocument;
import com.tarifvergleich.electricity.repository.ManageAdminDocumentRepository;
import com.tarifvergleich.electricity.util.FileServiceSuperAdmin;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceRequestEmailListener {

	private final MailService mailService;
	private final ManageAdminDocumentRepository adminDocumentRepo;
	private final FileServiceSuperAdmin fileServiceSuperAdmin;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleServiceRequestEmail(ServiceRequestEmailEvent event) {

		if (event.docs() != null && !event.docs().isEmpty()) {
			List<String> attachements = event.docs().stream().map(ManageAdminDocument::getFilePath)
					.map(fileServiceSuperAdmin::getAbsolutePath).toList();
			mailService.sendMailWithAttachment(event.customerMail(), event.customerSub(), event.customerBody(),
					attachements);
		} else {
			mailService.sendMail(event.customerMail(), event.customerSub(), event.customerBody());
		}
		mailService.sendMail(event.adminMail(), event.adminSub(), event.adminBody());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleServiceResponseEmail(ServiceResponseEmailEvent event) {
		if (event.docs() != null && !event.docs().isEmpty()) {
			List<String> attachements = event.docs().stream().map(ManageAdminDocument::getFilePath)
					.map(fileServiceSuperAdmin::getAbsolutePath).toList();
			mailService.sendMailWithAttachment(event.customerMail(), event.customerSub(), event.customerBody(),
					attachements);
		} else {
			String emailContent = event.customerBody();
			mailService.sendMail(event.customerMail(), event.customerSub(), emailContent);
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleServiceAttrachmentForAcknowlegment(ServiceAttachmentMailOfAcknowledgement event) {

		List<ManageAdminDocument> adminDocPrivacy = adminDocumentRepo
				.findAllByAdminAdminIdAndDocumentCategoryLike(event.adminId(), "%PRIVACY%");
		List<ManageAdminDocument> adminDocsTerms = adminDocumentRepo
				.findAllByAdminAdminIdAndDocumentCategoryLike(event.adminId(), "%TERM%CONDITION%");

		List<String> fileUrls = new ArrayList<>();
		fileUrls.addAll(adminDocPrivacy.stream().map(e -> e.getFilePath()).map(fileServiceSuperAdmin::getAbsolutePath)
				.toList());
		fileUrls.addAll(
				adminDocsTerms.stream().map(e -> e.getFilePath()).map(fileServiceSuperAdmin::getAbsolutePath).toList());

		mailService.sendMailWithAttachment(event.customerMail(), event.customerSub(), event.custmerBody(), fileUrls);
	}

	@EventListener
	public void sendverifyEmail(VerifyOtpEmail verifyEmail) {

		if (TransactionSynchronizationManager.isActualTransactionActive()) {

			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					if (verifyEmail.docs() != null && !verifyEmail.docs().isEmpty()) {
						List<String> attachements = verifyEmail.docs().stream().map(ManageAdminDocument::getFilePath)
								.map(fileServiceSuperAdmin::getAbsolutePath).toList();
						mailService.sendMailWithAttachment(verifyEmail.to(), verifyEmail.subject(), verifyEmail.body(),
								attachements);
					} else {
						mailService.sendMail(verifyEmail.to(), verifyEmail.subject(), verifyEmail.body());
					}
				}
			});
		} else {
			if (verifyEmail.docs() != null && !verifyEmail.docs().isEmpty()) {
				List<String> attachements = verifyEmail.docs().stream().map(ManageAdminDocument::getFilePath)
						.map(fileServiceSuperAdmin::getAbsolutePath).toList();
				mailService.sendMailWithAttachment(verifyEmail.to(), verifyEmail.subject(), verifyEmail.body(),
						attachements);
			} else {
				mailService.sendMail(verifyEmail.to(), verifyEmail.subject(), verifyEmail.body());
			}
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void sendPowerOfAttorny(AttornyEmailDto attornyEmailDto) {

		if (attornyEmailDto.docs() == null && !attornyEmailDto.docs().isEmpty()) {
			List<String> attachements = attornyEmailDto.docs().stream().map(ManageAdminDocument::getFilePath)
					.map(fileServiceSuperAdmin::getAbsolutePath).toList();

			mailService.sendEmailWithBase64Attachment(attornyEmailDto.to(), attornyEmailDto.body(),
					attornyEmailDto.base64pdf(), attornyEmailDto.pdfName(), attachements);
		} else {

			mailService.sendEmailWithBase64Attachment(attornyEmailDto.to(), attornyEmailDto.body(),
					attornyEmailDto.base64pdf(), attornyEmailDto.pdfName());
		}
	}

	@EventListener
	public void sendContractMail(ContractMailDto contractMail) {

		List<String> attachments = new LinkedList<String>();
		if (contractMail.docs() != null && !contractMail.docs().isEmpty()) {
			attachments.addAll(contractMail.docs().stream().map(ManageAdminDocument::getFilePath)
					.map(fileServiceSuperAdmin::getAbsolutePath).toList());
		}
		attachments.add(contractMail.absolutePath());

		if (TransactionSynchronizationManager.isActualTransactionActive()) {

			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					mailService.sendMailWithAttachment(contractMail.to(), contractMail.subject(), contractMail.body(),
							attachments);
				}
			});
		} else {
			mailService.sendMailWithAttachment(contractMail.to(), contractMail.subject(), contractMail.body(),
					attachments);
		}
	}
}
