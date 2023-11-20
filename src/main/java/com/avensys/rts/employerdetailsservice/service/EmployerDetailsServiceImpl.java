package com.avensys.rts.employerdetailsservice.service;

import java.util.List;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avensys.rts.employerdetailsservice.APIClient.FormSubmissionAPIClient;
import com.avensys.rts.employerdetailsservice.APIClient.UserAPIClient;
import com.avensys.rts.employerdetailsservice.customresponse.HttpResponse;
import com.avensys.rts.employerdetailsservice.entity.EmployerDetailsEntity;
import com.avensys.rts.employerdetailsservice.entity.UserEntity;
import com.avensys.rts.employerdetailsservice.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.employerdetailsservice.payloadnewrequest.EmployerDetailsRequestDTO;
import com.avensys.rts.employerdetailsservice.payloadnewresponse.FormSubmissionsResponseDTO;
import com.avensys.rts.employerdetailsservice.payloadnewresponse.EmployerDetailsResponseDTO;
import com.avensys.rts.employerdetailsservice.repository.EmployerDetailsRepository;
import com.avensys.rts.employerdetailsservice.repository.UserRepository;
import com.avensys.rts.employerdetailsservice.util.MappingUtil;

import jakarta.transaction.Transactional;

@Service
public class EmployerDetailsServiceImpl implements EmployerDetailsService {

	private final String CANDIDATE_EMPLOYER_TYPE = "candidate_employer";

	private final Logger log = LoggerFactory.getLogger(EmployerDetailsServiceImpl.class);
	private final EmployerDetailsRepository employerDetailsRepository;

	@Autowired
	private UserAPIClient userAPIClient;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@Autowired
	private UserRepository userRepository;

	public EmployerDetailsServiceImpl(EmployerDetailsRepository employerDetailsRepository, UserAPIClient userAPIClient,
			FormSubmissionAPIClient formSubmissionAPIClient) {
		this.employerDetailsRepository = employerDetailsRepository;
		this.userAPIClient = userAPIClient;
		this.formSubmissionAPIClient = formSubmissionAPIClient;
	}

	@Override
	@Transactional
	public EmployerDetailsResponseDTO createEmployerDetails(EmployerDetailsRequestDTO employerDetailsRequestDTO) {
		log.info("Creating EmployerDetails: service");
		System.out.println("EmployerDetails: " + employerDetailsRequestDTO);
		EmployerDetailsEntity savedEmployerDetailsEntityy = employerDetailsRequestDTOToEmployerDetailsEntity(employerDetailsRequestDTO);

		// Save form data to form submission microservice
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(employerDetailsRequestDTO.getCreatedBy());
		formSubmissionsRequestDTO.setFormId(employerDetailsRequestDTO.getFormId());
		formSubmissionsRequestDTO
				.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(employerDetailsRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(savedEmployerDetailsEntityy.getId());
		formSubmissionsRequestDTO.setEntityType(employerDetailsRequestDTO.getEntityType());
		HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		savedEmployerDetailsEntityy.setFormSubmissionId(formSubmissionData.getId());

		return employerDetailsEntityToEmployerDetailsResponseDTO(savedEmployerDetailsEntityy);
	}

	@Override
	public EmployerDetailsResponseDTO getEmployerDetailsById(Integer id) {
		EmployerDetailsEntity employerDetailsEntityFound = employerDetailsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("EmployerDetails not found"));
		return employerDetailsEntityToEmployerDetailsResponseDTO(employerDetailsEntityFound);
	}

	@Override
	@Transactional
	public EmployerDetailsResponseDTO updateEmployerDetails(Integer id, EmployerDetailsRequestDTO employerDetailsRequestDTO) {
		EmployerDetailsEntity employerDetailsEntityFound = employerDetailsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("EmployerDetails not found"));
		EmployerDetailsEntity updatedEmployerDetailsEntity = updateEmployerDetailsRequestDTOToEmployerDetailsEntity(employerDetailsEntityFound,
				employerDetailsRequestDTO);

		// Update form submission
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(employerDetailsRequestDTO.getUpdatedBy());
		formSubmissionsRequestDTO.setFormId(employerDetailsRequestDTO.getFormId());
		formSubmissionsRequestDTO
				.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(employerDetailsRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(updatedEmployerDetailsEntity.getId());
		formSubmissionsRequestDTO.setEntityType(employerDetailsRequestDTO.getEntityType());
		HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.updateFormSubmission(updatedEmployerDetailsEntity.getFormSubmissionId(), formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		updatedEmployerDetailsEntity.setFormSubmissionId(formSubmissionData.getId());
		return employerDetailsEntityToEmployerDetailsResponseDTO(updatedEmployerDetailsEntity);
	}

	@Override
	@Transactional
	public void deleteEmployerDetails(Integer id) {
		EmployerDetailsEntity employerDetailsEntityFound = employerDetailsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("EmployerDetails not found"));
		employerDetailsRepository.delete(employerDetailsEntityFound);
	}

	@Override
	public List<EmployerDetailsResponseDTO>getEmployerDetailsByEntityTypeAndEntityId(String entityType, Integer entityId) {
		List<EmployerDetailsEntity> employerDetailsEntityList = employerDetailsRepository.findByEntityTypeAndEntityId(entityType,
				entityId);
		List<EmployerDetailsResponseDTO> employerDetailsResponseDTOList = employerDetailsEntityList.stream()
				.map(this::employerDetailsEntityToEmployerDetailsResponseDTO).toList();
		return employerDetailsResponseDTOList;
	}

	@Override
	@Transactional
	public void deleteEmployerDetailsByEntityTypeAndEntityId(String entityType, Integer entityId) {
		List<EmployerDetailsEntity> employerDetailsEntityList = employerDetailsRepository.findByEntityTypeAndEntityId(entityType,
				entityId);
		if (!employerDetailsEntityList.isEmpty()) {
			// Delete each Employer Details form submission before deleting
			employerDetailsEntityList.forEach(employerDetailsEntity -> {
				formSubmissionAPIClient.deleteFormSubmission(employerDetailsEntity.getFormSubmissionId());
				employerDetailsRepository.delete(employerDetailsEntity);
			});
		}
	}

	private EmployerDetailsResponseDTO employerDetailsEntityToEmployerDetailsResponseDTO(EmployerDetailsEntity employerDetailsEntity) {
		EmployerDetailsResponseDTO employerDetailsResponseDTO = new EmployerDetailsResponseDTO();
		employerDetailsResponseDTO.setId(employerDetailsEntity.getId());
		employerDetailsResponseDTO.setCreatedAt(employerDetailsEntity.getCreatedAt());
		employerDetailsResponseDTO.setUpdatedAt(employerDetailsEntity.getUpdatedAt());
		employerDetailsResponseDTO.setEntityType(employerDetailsEntity.getEntityType());
		employerDetailsResponseDTO.setEntityId(employerDetailsEntity.getEntityId());
		employerDetailsResponseDTO.setFormId(employerDetailsEntity.getFormId());
		employerDetailsResponseDTO.setFormSubmissionId(employerDetailsEntity.getFormSubmissionId());

		// Get created by User data from user microservice
		Optional<UserEntity> userEntity = userRepository.findById(employerDetailsEntity.getCreatedBy());
		UserEntity userData = userEntity.get();
		employerDetailsResponseDTO.setCreatedBy(userData.getFirstName() + " " + userData.getLastName());

		// Get updated by user data from user microservice
		if (employerDetailsEntity.getUpdatedBy() == employerDetailsEntity.getCreatedBy()) {
			employerDetailsResponseDTO.setUpdatedBy(userData.getFirstName() + " " + userData.getLastName());
		} else {
			userEntity = userRepository.findById(employerDetailsEntity.getUpdatedBy());
			userData = userEntity.get();
			employerDetailsResponseDTO.setUpdatedBy(userData.getFirstName() + " " + userData.getLastName());
		}

		// Get form submission data
		HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.getFormSubmission(employerDetailsEntity.getFormSubmissionId());
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
		employerDetailsResponseDTO
				.setSubmissionData(MappingUtil.convertJsonNodeToJSONString(formSubmissionData.getSubmissionData()));
		return employerDetailsResponseDTO;
	}

	private EmployerDetailsEntity updateEmployerDetailsRequestDTOToEmployerDetailsEntity(EmployerDetailsEntity employerDetailsEntity,
			EmployerDetailsRequestDTO employerDetailsRequestDTO) {
		employerDetailsEntity.setEntityType(employerDetailsRequestDTO.getEntityType());
		employerDetailsEntity.setEntityId(employerDetailsRequestDTO.getEntityId());
		employerDetailsEntity.setUpdatedBy(employerDetailsRequestDTO.getUpdatedBy());
		employerDetailsEntity.setFormId(employerDetailsRequestDTO.getFormId());
		return employerDetailsRepository.save(employerDetailsEntity);
	}

	private EmployerDetailsEntity employerDetailsRequestDTOToEmployerDetailsEntity(EmployerDetailsRequestDTO employerDetailsRequestDTO) {
		EmployerDetailsEntity employerDetailsEntity = new EmployerDetailsEntity();
		employerDetailsEntity.setEntityType(employerDetailsRequestDTO.getEntityType());
		employerDetailsEntity.setEntityId(employerDetailsRequestDTO.getEntityId());
		employerDetailsEntity.setCreatedBy(employerDetailsRequestDTO.getCreatedBy());
		employerDetailsEntity.setUpdatedBy(employerDetailsRequestDTO.getUpdatedBy());
		employerDetailsEntity.setFormId(employerDetailsRequestDTO.getFormId());
		return employerDetailsRepository.save(employerDetailsEntity);
	}

}
