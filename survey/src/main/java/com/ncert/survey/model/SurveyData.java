package com.ncert.survey.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.core.serializer.Deserializer;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(name = "sr_survey_data")
@EntityListeners(AuditingEntityListener.class)
public class SurveyData implements Serializable, Deserializer<SurveyData> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "record_id")
	private Long recordId;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "question_id")
	private Integer questionId;
	@Column(name = "questionnaire_id")
	private Integer questionnaireId;
	@Column(name = "answer")
	private String answer;
	@Column(name = "latitude")
	private String latitude;
	@Column(name = "longitude")
	private String longitude;
	@Column(name = "mobile_no")
    private String mobile;
	@Column(name = "language")
    private String language;

	@ManyToOne
	@JoinColumn(name = "state_id", nullable = false)
	private MstrState state;
	@Column(name = "created_time")
	private Date createdTime;
	@Column(name = "modified_time")
	private Date modifiedTime;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "modified_by")
	private String modifiedBy;
	
	@Override
	public SurveyData deserialize(InputStream inputStream) throws IOException {

		ObjectInputStream in = new ObjectInputStream(inputStream);
		SurveyData surveyData = null;
		// Method for deserialization of object
		try {
			surveyData = (SurveyData) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		}
		in.close();
		return surveyData;
	}

}
