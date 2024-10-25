package com.ncert.survey.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.core.serializer.Deserializer;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.ToString;

@Entity
@ToString
@Table(name = "master_state")
@EntityListeners(AuditingEntityListener.class)
public class MstrState implements Serializable, Deserializer<MstrState> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "state_id")
	private Integer stateId;
	@Column(name = "state_name")
	private String stateName;
	@Column(name = "state_code")
	private String stateCode;
	@Column(name = "created_time")
	private Date createdTime;
	@Column(name = "modified_time")
	private Date modifiedTime;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "modified_by")
	private String modifiedBy;
	@Column(name = "status")
	private String status;
	@Column(name = "state_ut")
	private String stateUt;

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Override
	public MstrState deserialize(InputStream inputStream) throws IOException {

		ObjectInputStream in = new ObjectInputStream(inputStream);
		MstrState mStrState = null;
		// Method for deserialization of object
		try {
			mStrState = (MstrState) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		}
		in.close();
		return mStrState;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStateUt() {
		return stateUt;
	}

	public void setStateUt(String stateUt) {
		this.stateUt = stateUt;
	}

}
