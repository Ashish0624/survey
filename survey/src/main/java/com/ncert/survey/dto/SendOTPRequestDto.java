package com.ncert.survey.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SendOTPRequestDto implements Serializable {

	@NotEmpty
	private String mobileNo;

	@NotEmpty
	private Integer stateId;

	@NotEmpty
	private String userId;
	
	@NotEmpty
    private String respondentType;

}
