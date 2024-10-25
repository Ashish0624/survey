package com.ncert.survey.dto;

import java.io.Serializable;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidateOTPRequestDto implements Serializable {
	
	private Integer otp;
	private Integer stateId;
	private String userId;
	private String tsnId;
	
}
