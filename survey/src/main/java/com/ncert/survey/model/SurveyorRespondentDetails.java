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
import javax.validation.constraints.NotEmpty;

import org.springframework.core.serializer.Deserializer;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(name = "sr_surveyor_respondent_details")
@EntityListeners(AuditingEntityListener.class)
public class SurveyorRespondentDetails
    implements
    Serializable,
    Deserializer<SurveyorRespondentDetails>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "respdt_details_id")
    private Long      respdtDetailsId;
    @Column(name = "user_id")
    private String   userId;
    @Column(name = "first_name")
    private String    firstName;
    @Column(name = "last_name")
    private String    lastName;
    @Column(name = "respondent_mobile_number")
    private String    respondentMobileNumber;
    @Column(name = "surveyor_mobile_number")
    private String    surveyorMobileNumber;

    @Column(name = "url")
    private String    url;
    @Column(name = "status")
    private byte      status;
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false,updatable = false)
    private MstrState state;
    @Column(name = "respondent_type")
    private String    respondentType;
    @Column(name = "is_survey_submitted")
    private byte      isSurveySubmitted;
    @Column(name = "email_id")
    private String    email_id;
    @Column(name = "created_time")
    private Date      createdTime;
    @Column(name = "modified_time")
    private Date      modifiedTime;
    @Column(name = "created_by")
    private String    createdBy;
    @Column(name = "modified_by")
    private String    modifiedBy;
    @Column(name = "survey_type_id")
    private Integer surveyTypeId;
    
    @Override
    public SurveyorRespondentDetails deserialize( InputStream inputStream )
        throws IOException
    {
        ObjectInputStream in = new ObjectInputStream( inputStream );
        SurveyorRespondentDetails surveyorRespondentDetails = null;
        // Method for deserialization of object
        try
        {
            surveyorRespondentDetails = (SurveyorRespondentDetails) in.readObject();
        }
        catch ( ClassNotFoundException e )
        {
            System.out.println( "Class not found exception" );
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            System.out.println( "IO exception" );
            e.printStackTrace();
        }
        in.close();
        return surveyorRespondentDetails;
    }
}
