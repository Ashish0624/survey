package com.ncert.survey.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncert.survey.model.SurveyData;
import com.ncert.survey.repository.SurveyDataRepository;

@Service
public class SurveyDataService
{
    @Autowired
    private SurveyDataRepository surveyDataRepository;
    
    public List<SurveyData> findByUserIdAndQuestionnaireIdAndMobile( String userId,Integer questionnaireId ,String mobile ){
        return surveyDataRepository.findByUserIdAndQuestionnaireIdAndMobile( userId, questionnaireId, mobile );
    }

    
}
