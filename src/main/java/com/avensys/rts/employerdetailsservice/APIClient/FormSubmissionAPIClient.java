package com.avensys.rts.employerdetailsservice.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.avensys.rts.employerdetailsservice.customresponse.HttpResponse;
import com.avensys.rts.employerdetailsservice.interceptor.JwtTokenInterceptor;
import com.avensys.rts.employerdetailsservice.payloadnewrequest.FormSubmissionsRequestDTO;

@FeignClient(name = "form-service", url = "${api.form-submission.url}", configuration = JwtTokenInterceptor.class)
public interface FormSubmissionAPIClient {
    @PostMapping("")
    HttpResponse addFormSubmission(@RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

    @GetMapping("/{formSubmissionId}")
    HttpResponse getFormSubmission(@PathVariable int formSubmissionId);

    @PutMapping("/{formSubmissionId}")
    HttpResponse updateFormSubmission(@PathVariable int formSubmissionId, @RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

    @DeleteMapping("/{formSubmissionId}")
    HttpResponse deleteFormSubmission(@PathVariable int formSubmissionId);

}
