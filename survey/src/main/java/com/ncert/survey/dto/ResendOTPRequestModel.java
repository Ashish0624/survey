package com.ncert.survey.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResendOTPRequestModel implements Serializable  {
	
	@NotEmpty
	private String txnId;
	
	@NotEmpty
	private String mobileNo;
	
	@NotEmpty
	private Integer stateId;

	@NotEmpty
	private String userId;
	
	
	
	
}
