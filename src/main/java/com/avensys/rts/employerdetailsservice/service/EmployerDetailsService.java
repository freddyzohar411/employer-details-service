package com.avensys.rts.employerdetailsservice.service;

import java.util.List;

import com.avensys.rts.employerdetailsservice.payloadnewrequest.EmployerDetailsRequestDTO;
import com.avensys.rts.employerdetailsservice.payloadnewresponse.EmployerDetailsResponseDTO;

public interface EmployerDetailsService {

    EmployerDetailsResponseDTO createEmployerDetails(EmployerDetailsRequestDTO contactNewRequestDTO);

    EmployerDetailsResponseDTO getEmployerDetailsById(Integer id);

    EmployerDetailsResponseDTO updateEmployerDetails(Integer id, EmployerDetailsRequestDTO contactNewRequestDTO);

    void deleteEmployerDetails(Integer id);

    List<EmployerDetailsResponseDTO> getEmployerDetailsByEntityTypeAndEntityId(String entityType, Integer entityId);

    void deleteEmployerDetailsByEntityTypeAndEntityId(String entityType, Integer entityId);
}
