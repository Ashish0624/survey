package com.ncert.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ncert.survey.model.SurveyData;

@Repository
public interface SurveyDataRepository extends JpaRepository<SurveyData, Integer> {
    
    List<SurveyData> findByUserIdAndQuestionnaireIdAndMobile( String userId,Integer questionnaireId ,String mobile );

}
