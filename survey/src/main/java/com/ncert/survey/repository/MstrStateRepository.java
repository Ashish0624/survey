package com.ncert.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ncert.survey.model.MstrState;

@Repository
public interface MstrStateRepository extends JpaRepository<MstrState, Integer> {

	MstrState findByStateId(Integer stateId);
}
