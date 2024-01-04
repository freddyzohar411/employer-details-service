package com.avensys.rts.employerdetailsservice.controller;

import org.slf4j.Logger;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.avensys.rts.employerdetailsservice.constant.MessageConstants;
import com.avensys.rts.employerdetailsservice.payloadnewrequest.EmployerDetailsRequestDTO;
import com.avensys.rts.employerdetailsservice.payloadnewresponse.EmployerDetailsResponseDTO;
import com.avensys.rts.employerdetailsservice.service.EmployerDetailsServiceImpl;
import com.avensys.rts.employerdetailsservice.util.JwtUtil;
import com.avensys.rts.employerdetailsservice.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/employer-details")
public class EmployerDetailsController {

	private final Logger log = LoggerFactory.getLogger(EmployerDetailsController.class);
	private final EmployerDetailsServiceImpl employerDetailsServiceImpl;
	private final MessageSource messageSource;

	@Autowired
	private JwtUtil jwtUtil;

	public EmployerDetailsController(EmployerDetailsServiceImpl employerDetailsServiceImpl, MessageSource messageSource) {
		this.employerDetailsServiceImpl = employerDetailsServiceImpl;
		this.messageSource = messageSource;
	}

	@PostMapping("/add")
	public ResponseEntity<Object> createEmployerDetails(@Valid @RequestBody EmployerDetailsRequestDTO employerDetailsRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		log.info("Create Employer Details : Controller ");
		Long userId = jwtUtil.getUserId(token);
		employerDetailsRequestDTO.setCreatedBy(userId);
		employerDetailsRequestDTO.setUpdatedBy(userId);
		EmployerDetailsResponseDTO employerDetailsResponseDTO = employerDetailsServiceImpl.createEmployerDetails(employerDetailsRequestDTO);
		return ResponseUtil.generateSuccessResponse(employerDetailsResponseDTO, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("/entity/{entityType}/{entityId}")
	public ResponseEntity<Object> getEmployerDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId) {
		log.info("Get Employer Details by entity type and entity id : Controller ");
		return ResponseUtil.generateSuccessResponse(
				employerDetailsServiceImpl.getEmployerDetailsByEntityTypeAndEntityId(entityType, entityId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteEmployerDetails(@PathVariable Integer id) {
		log.info("Delete Employer Details : Controller ");
		employerDetailsServiceImpl.deleteEmployerDetails(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateEmployerDetails(@PathVariable Integer id,
			@Valid @RequestBody EmployerDetailsRequestDTO employerDetailsRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		log.info("Update Employer Details : Controller ");
		Long userId = jwtUtil.getUserId(token);
		employerDetailsRequestDTO.setUpdatedBy(userId);
		EmployerDetailsResponseDTO employerDetailsResponseDTO = employerDetailsServiceImpl.updateEmployerDetails(id, employerDetailsRequestDTO);
		return ResponseUtil.generateSuccessResponse(employerDetailsResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * This endpoint is to delete Employer Details by entity type and entity id
	 * 
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	@DeleteMapping("/entity/{entityType}/{entityId}")
	public ResponseEntity<Object> deleteEmployerDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId) {
		log.info("Delete Employer Details by entity type and entity id : Controller ");
		employerDetailsServiceImpl.deleteEmployerDetailsByEntityTypeAndEntityId(entityType, entityId);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
}
