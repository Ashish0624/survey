package com.ncert.survey.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(name = "master_questionnaire")
@EntityListeners(AuditingEntityListener.class)
public class QuestionnaireData {
	
	
	@Id
	@Column(name = "id")
	Integer id;
	
	@Column(name = "questionnaire_id")
	Integer questionnaireId;
	
	@Column(name = "question_id")
	Integer questionId;
	@Column(name = "question_name")
	String questionName;
	@Column(name = "option_1")
	String option_1;
	
	@Column(name = "option_2")
	String option_2;
	
	@Column(name = "option_3")
	String option_3;
	
	@Column(name = "option_4")
	String option_4;
	
	@Column(name = "option_5")
	String option_5;
	
	@Column(name = "option_6")
	String option_6;
	
	@Column(name = "option_7")
	String option_7;
	
	@Column(name = "option_8")
	String option_8;
	
	@Column(name = "option_9")
	String option_9;
	
	@Column(name = "option_10")
	String option_10;
	
	@Column(name = "option_11")
	String option_11;
	
	@Column(name = "option_12")
	String option_12;
	
	@Column(name = "option_13")
	String option_13;
	
	@Column(name = "option_14")
	String option_14;
	
	@Column(name = "option_15")
	String option_15;
	
	@Column(name = "respondent_type")
	String respondentType;
	
	@Column(name = "question_type")
	String questionType;
	
	@Column(name = "max_selectable_options")
	Integer maxSelectableOption;
	
	@Column(name = "language")
	String language;
	
	@Column(name = "created_time")
	private Date createdTime;
	@Column(name = "modified_time")
	private Date modifiedTime;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "modified_by")
	private String modifiedBy;
	
	@Column(name = "status")
	short status;
	

}
