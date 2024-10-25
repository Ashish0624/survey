package com.ncert.survey.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NotificationServiceResponseModel<T> implements Serializable {

	private List<ErrorModel> errors = null;

	private T data = null;

}
