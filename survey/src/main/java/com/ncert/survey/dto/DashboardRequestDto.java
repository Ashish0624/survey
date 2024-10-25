package com.ncert.survey.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DashboardRequestDto implements Serializable {

	@NotNull
	private String userId;

}
