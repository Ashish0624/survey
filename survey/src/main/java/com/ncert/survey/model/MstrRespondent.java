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
import javax.persistence.Table;

import org.springframework.core.serializer.Deserializer;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
@Table(name = "master_respondent")
@EntityListeners(AuditingEntityListener.class)
public class MstrRespondent implements Serializable, Deserializer<MstrRespondent> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "respondent_type_id")
	private Integer respondentTypeId;

	@Column(name = "respondent_type_en")
	private String respondentTypeEn;

	@Column(name = "respondent_type_hin")
	private String respondentTypeHin;
	
    @Column(name = "respondent_type_guj")
    private String  respondentTypeGuj;
    
    @Column(name = "respondent_type_asm")
    private String  respondentTypeAsm;
    
    @Column(name = "respondent_type_kas")
    private String  respondentTypeKas;
    
    @Column(name = "respondent_type_doi")
    private String  respondentTypeDoi;
    
    @Column(name = "respondent_type_bod")
    private String  respondentTypeBod;

    @Column(name = "respondent_type_pan")
    private String  respondentTypePan;

    @Column(name = "respondent_type_tel")
    private String  respondentTypeTel;
    
    @Column(name = "respondent_type_kan")
    private String  respondentTypeKan;
    
    @Column(name = "respondent_type_mar")
    private String  respondentTypeMar;
    
    @Column(name = "respondent_type_mal")
    private String  respondentTypeMal;
    
    @Column(name = "respondent_type_mni")
    private String  respondentTypeMni;
    
    @Column(name = "respondent_type_urd")
    private String  respondentTypeUrd;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "modified_time")
	private Date modifiedTime;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "status")
	private byte status;
	
	@Column(name = "questionnaire_id")
    private Integer questionnaireId;

	@Override
	public MstrRespondent deserialize(InputStream inputStream) throws IOException {

		ObjectInputStream in = new ObjectInputStream(inputStream);
		MstrRespondent mstrRespondent = null;
		// Method for deserialization of object
		try {
			mstrRespondent = (MstrRespondent) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		}
		in.close();
		return mstrRespondent;
	}
}
