package com.avensys.rts.employerdetailsservice.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.employerdetailsservice.customresponse.HttpResponse;
import com.avensys.rts.employerdetailsservice.interceptor.JwtTokenInterceptor;

@FeignClient(name = "user-service", url = "${api.user.url}", configuration = JwtTokenInterceptor.class)
public interface UserAPIClient {

	@GetMapping("/{id}")
	HttpResponse getUserById(@PathVariable("id") Integer id);

	@GetMapping("/email/{email}")
	HttpResponse getUserByEmail(@PathVariable("email") String email);

}