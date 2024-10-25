package com.ncert.survey.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter 
@Setter
@Table(name = "app_version")
@EntityListeners(AuditingEntityListener.class)
public class AppVersion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private int id;
	
	@Column(name = "user_os")
	private String userOs;
	
	@Column(name = "latest_version")
	private String latestVersion;
	
	@Column(name = "is_compulsory")
	private short  isComplusory;
	
	@Column(name = "last_compulsory_version")
	private String lastCompulsoryVerion;
}
