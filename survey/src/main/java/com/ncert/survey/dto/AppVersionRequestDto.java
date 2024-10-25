package com.ncert.survey.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppVersionRequestDto implements Serializable {

	@NotEmpty
	private String userOs;
	
	@NotEmpty
	private String installedVersion;
}
