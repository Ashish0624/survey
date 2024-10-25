package com.ncert.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncert.survey.model.MstrState;
import com.ncert.survey.repository.MstrStateRepository;

@Service
public class MstrStateService {

	@Autowired
	private MstrStateRepository mstrStateRepository;

	public MstrState findById(Integer stateid) {
		return mstrStateRepository.findByStateId(stateid);
	}
}
