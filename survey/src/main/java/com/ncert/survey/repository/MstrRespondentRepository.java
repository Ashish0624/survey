package com.ncert.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ncert.survey.model.MstrRespondent;

@Repository
public interface MstrRespondentRepository extends JpaRepository<MstrRespondent, Integer>
{
    MstrRespondent findByRespondentTypeId(Integer respondentTypeId);
}
