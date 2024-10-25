package com.ncert.survey.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionairResponseDto implements Serializable {

	private String questionair = null;
	private Integer stateId = null;
	private String mobileNo = null;
	private String respondentType = null;
	private String userId = null;

}
