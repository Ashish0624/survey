package com.ncert.survey.dto;

import java.io.Serializable;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class ValidateOTPRequestModel implements Serializable {
	
	@NotNull
	private Integer otp;
	
	@NotNull
	private Integer stateId;
	
	@NotNull
	private String userId;
	
	@NotNull
	private String tsnId;
	
	@NotNull
	private String respondentType;

}
