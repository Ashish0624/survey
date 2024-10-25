package com.ncert.survey.dto;

import java.io.Serializable;
import java.util.List;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SendMailRequestDto implements Serializable {

	@NotNull
	private String userId;
	@NotNull
	private Integer stateId;
	@NotNull
	private String Subject;
	@NotNull
	private boolean emailTemplateHtmlType;
	@NotNull
	private String emailTemplates;
	@NotNull
	private List<String> emailToList;
	@NotNull
	private List<String> emailCCList;
	@NotNull
	private List<String> emailBCCList;
	@NotNull
	private List<String> attachmentDataListUrls;

	
}
