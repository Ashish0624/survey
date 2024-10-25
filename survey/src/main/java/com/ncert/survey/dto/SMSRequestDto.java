package com.ncert.survey.dto;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class SMSRequestDto implements Serializable {

	ArrayList<String> mobileNo = null;
	String smsBody = null;
	Integer stateId = null;
	Integer templateId = null;
	String userId = null;

}
