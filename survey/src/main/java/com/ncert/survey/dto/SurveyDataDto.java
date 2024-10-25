package com.ncert.survey.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SurveyDataDto implements Serializable {

	@NotNull
	private String userId;
	@NotNull
	private Integer questionnaireId;
	@NotNull
	private Integer stateId;
	@NotEmpty
	private String respondentType;
	@NotEmpty
	private String mobileNo;
	@Valid
	@NotNull
	private List<QuestionAnswerDto> questionAnswerDto;
	@NotEmpty
	private String latitude;
	@NotEmpty
	private String longitude;
		

}
