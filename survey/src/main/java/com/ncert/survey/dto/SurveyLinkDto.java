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
public class SurveyLinkDto implements Serializable {

	@NotNull
	private String userId;
	@NotNull
	private String respondentType;
//	@NotEmpty
	private String emailId = null;
//	@NotEmpty
	private String firstName = null;
//	@NotEmpty
	private String lastName = null;
	@NotNull
	private Integer stateId;
    @NotEmpty
    private String respondentMobileNumber;
    private String surveyorMobileNumber;
    @NotEmpty
    private byte isfillsurvey;
//    @NotEmpty
    private Integer surveyType; 


}
