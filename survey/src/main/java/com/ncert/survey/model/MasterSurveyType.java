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
@Table(name = "master_survey_type")
@EntityListeners(AuditingEntityListener.class)
public class MasterSurveyType implements Serializable, Deserializer {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_type_id")
	private Integer surveyTypeId;
	
	@Column(name = "survey_type_en")
	private String surveyTypeEn;
	
	@Column(name = "survey_type_hin")
    private String  surveyTypeHin;
    
    @Column(name = "survey_type_guj")
    private String  surveyTypeGuj;
    
    @Column(name = "survey_type_asm")
    private String  surveyTypeAsm;
    
    @Column(name = "survey_type_kas")
    private String  surveyTypeKas;
    
    @Column(name = "survey_type_doi")
    private String  surveyTypeDoi;
    
    @Column(name = "survey_type_bod")
    private String  surveyTypeBod;

    @Column(name = "survey_type_pan")
    private String  surveyTypePan;

    @Column(name = "survey_type_tel")
    private String  surveyTypeTel;
    
    @Column(name = "survey_type_kan")
    private String  surveyTypeKan;
    
    @Column(name = "survey_type_mar")
    private String  surveyTypeMar;
    
    @Column(name = "survey_type_mal")
    private String  surveyTypeMal;
    
    @Column(name = "survey_type_mni")
    private String  surveyTypeMni;
    
    @Column(name = "survey_type_urd")
    private String  surveyTypeUrd;
    
    @Column(name = "created_time")
    private Date    createdTime;
    
    @Column(name = "modified_time")
    private Date    modifiedTime;
    
    @Column(name = "created_by")
    private String  createdBy;
    
    @Column(name = "modified_by")
    private String  modifiedBy;
    
    @Column(name = "status")
    private byte  status;
    
    @Override
    public MasterSurveyType deserialize(InputStream inputStream) throws IOException {
        
        ObjectInputStream in = new ObjectInputStream(inputStream);
        MasterSurveyType mstrSurveyType =null;
        // Method for deserialization of object
        try {
        	mstrSurveyType = (MasterSurveyType)in.readObject();
        } catch (ClassNotFoundException e) {
           	//change_vishal
//        	LOGGER.info("RespondentScheduler  :: Exception :: "+Calendar.getInstance().getTime()+e.toString());
            System.out.println("Class not found exception");
            e.printStackTrace();
        } catch (IOException e) {
           	//change_vishal
//        	LOGGER.info("RespondentScheduler  :: Exception :: "+Calendar.getInstance().getTime()+e.toString());
            System.out.println("IO exception");
            e.printStackTrace();
        }
        in.close();
        return mstrSurveyType;
    }
    
}





















