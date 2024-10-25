package com.ncert.survey.exception;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.google.gson.Gson;
import com.ncert.survey.dto.ErrorModel;
import com.ncert.survey.dto.ResponseModel;
import com.ncert.survey.utils.ApiError;

@ControllerAdvice
public class GlobalExceptionHandler {
	final static Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> customException(CustomException ex, WebRequest request) {
		ApiError errorDetails = new ApiError(HttpStatus.OK, ex.getMessage(), request.getDescription(false));
		errorDetails.setServiceCode(ex.getAppServiceCode());
		return buildResponseEntity(errorDetails);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
		ApiError errorDetails = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(errorDetails);
	}

	/*
	 * @ExceptionHandler(NoHandlerFoundException.class) public ResponseEntity<?>
	 * unhandledPath( final NoHandlerFoundException ex, WebRequest request ) {
	 * ApiError errorDetails = new ApiError( HttpStatus.NOT_FOUND, ex.getMessage(),
	 * request.getDescription( false ) ); return new ResponseEntity<>( errorDetails,
	 * HttpStatus.NOT_FOUND ); }
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> globleExcpetionHandler(Exception ex, WebRequest request) {
		LOGGER.error("globleExcpetionHandler ", ex);
		ApiError errorDetails = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
				request.getDescription(false));
		return buildResponseEntity(errorDetails);
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		Gson gson = new Gson();
		ResponseModel responseDto = new ResponseModel();
		// responseDto.setApiError( apiError );
		// responseDto.setStatusCode( apiError.getServiceCode().getStatusCode() );
		// responseDto.setStatusDesc( apiError.getServiceCode().getStatusDesc() );
		// responseDto.setDisplayMsg( apiError.getServiceCode().getStatusDesc() );
		// TODO:Comment for Audit
		// responseDto.setHttpStatus( apiError.getStatus() );
		// responseDto.setHttpStatus( HttpStatus.OK );
		ErrorModel error = new ErrorModel();
		error.setCode(apiError.getServiceCode().getStatusCode());
		error.setMessage(apiError.getServiceCode().getStatusDesc());
		List<ErrorModel> errors = new ArrayList<ErrorModel>();
		errors.add(error);
		responseDto.setErrors(errors);
		LOGGER.info("===== Response for Service : " + gson.toJson(responseDto));
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
