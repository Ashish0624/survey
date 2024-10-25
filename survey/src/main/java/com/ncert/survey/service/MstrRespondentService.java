package com.ncert.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncert.survey.model.MstrRespondent;
import com.ncert.survey.repository.MstrRespondentRepository;

@Service
public class MstrRespondentService
{
    @Autowired
    private MstrRespondentRepository mstrRespondentRepository;
    public MstrRespondent findById( Integer respondentId )
    {
        return mstrRespondentRepository.findByRespondentTypeId( respondentId );
    }
}
