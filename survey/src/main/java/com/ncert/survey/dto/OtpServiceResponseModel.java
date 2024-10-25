
package com.ncert.survey.dto;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import org.springframework.core.serializer.Deserializer;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class OtpServiceResponseModel<T> implements Serializable, Deserializer<OtpServiceResponseModel<T>> {

	private List<OtpServicesErrorModel> errors = null;

	private T data = null;

	@Override
	public OtpServiceResponseModel<T> deserialize(InputStream inputStream) throws IOException {

		ObjectInputStream in = new ObjectInputStream(inputStream);
		OtpServiceResponseModel<T> otpServiceResponseModel = null;
		// Method for deserialization of object
		try {
			otpServiceResponseModel = (OtpServiceResponseModel<T>) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		}
		in.close();
		return otpServiceResponseModel;
	}
}
