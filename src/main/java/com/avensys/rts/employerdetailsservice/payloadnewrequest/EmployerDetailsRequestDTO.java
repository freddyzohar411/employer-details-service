package com.avensys.rts.employerdetailsservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployerDetailsRequestDTO {

	private String entityType;
	private Integer entityId;

	// Form Submission
	private String formData;
	private Integer formId;

	private Long createdBy;
	private Long updatedBy;
}
