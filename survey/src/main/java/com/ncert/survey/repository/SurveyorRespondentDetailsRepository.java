package com.ncert.survey.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ncert.survey.model.SurveyorRespondentDetails;

@Repository
public interface SurveyorRespondentDetailsRepository
    extends
    JpaRepository<SurveyorRespondentDetails, Integer>
{
        @Query(value = "select count(*) ,sum(case when surveyor_mobile_number is not null then 1 else 0 end) , sum(case when is_survey_submitted = 1 then 1 else 0 end) as TotalSurveySubmitted  from public.sr_surveyor_respondent_details where user_id = :userId", nativeQuery = true)
	    List<Object[]> findDashboardData( @Param("userId") String userId );

    SurveyorRespondentDetails findByUserIdAndRespondentTypeAndRespondentMobileNumber( String userId,
                                                                            String respondentType,
                                                                            String respondentMobileNumber );
    
    SurveyorRespondentDetails findByUserIdAndRespondentMobileNumber( String userId, String respondentMobileNumber );

    SurveyorRespondentDetails findByRespondentMobileNumber( String respondentMobileNumber );

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE public.sr_surveyor_respondent_details SET modified_time= :modifiedTime , modified_by= :modifiedBy , is_survey_submitted= :isurveySubmitted WHERE respdt_details_id = :respdtDetailsId", nativeQuery = true)
    void updateSurveyorRespondentDetails( @Param("isurveySubmitted") byte isurveySubmitted,
                                          @Param("modifiedBy") String modifiedBy,
                                          @Param("modifiedTime") Date modifiedTime,
                                          @Param("respdtDetailsId") Long respdtDetailsId );
}
