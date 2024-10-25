package com.ncert.survey.dto;

import java.io.Serializable;

import lombok.ToString;

import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto implements Serializable {
	private String statusCode = null;
	private String statusDesc = null;
	
}
