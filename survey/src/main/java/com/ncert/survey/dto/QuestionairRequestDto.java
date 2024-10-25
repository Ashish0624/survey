package com.ncert.survey.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionairRequestDto implements Serializable {

	@NotEmpty
	private String respondentType = null;
	@NotNull
	private String userId = null;
	@NotEmpty
	private String mobileNo = null;
	@NotNull
	private Integer stateId = null;

}
