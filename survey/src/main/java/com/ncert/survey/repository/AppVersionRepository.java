package com.ncert.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ncert.survey.model.AppVersion;

public interface AppVersionRepository extends JpaRepository<AppVersion, Integer> {
	
	public AppVersion findByUserOs(String userOs);

}
