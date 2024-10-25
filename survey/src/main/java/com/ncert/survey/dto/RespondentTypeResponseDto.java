package com.ncert.survey.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RespondentTypeResponseDto {

	private String statusCode = null;
	private String statusDesc = null;
	private String[] respondents = null;


}
