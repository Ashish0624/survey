package com.ncert.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ncert.survey.model.MasterSurveyType;

@Repository
public interface SurveyTypeRepository 
	extends 
	JpaRepository<MasterSurveyType, Integer> {

}
