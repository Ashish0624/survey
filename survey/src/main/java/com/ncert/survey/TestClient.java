package com.ncert.survey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ncert.survey.costants.NPServiceParameter;
import com.ncert.survey.dto.AppVersionRequestDto;
import com.ncert.survey.dto.DashboardDto;
import com.ncert.survey.dto.DashboardRequestDto;
import com.ncert.survey.dto.QuestionAnswerDto;
import com.ncert.survey.dto.QuestionairRequestDto;
import com.ncert.survey.dto.QuestionairResponseDto;
import com.ncert.survey.dto.RequestDto;
import com.ncert.survey.dto.ResendOTPRequestModel;
import com.ncert.survey.dto.RespondentTypeDto;
import com.ncert.survey.dto.ResponseDto;
import com.ncert.survey.dto.ResponseModel;
import com.ncert.survey.dto.SendOTPRequestDto;
import com.ncert.survey.dto.SendOTPResponseModel;
import com.ncert.survey.dto.SurveyDataDto;
import com.ncert.survey.dto.SurveyLinkDto;
import com.ncert.survey.dto.SurveyTypeDto;
import com.ncert.survey.dto.ValidateOTPRequestDto;
import com.ncert.survey.dto.ValidateOTPRequestModel;
import com.ncert.survey.utils.AppEncryptionUtil;
import com.ncert.survey.utils.SignatureUtil;

public class TestClient {
	// private static final AppEncryptionUtil ENC_UTIL = new
	// AppEncryptionUtil("KeheUber");
	private static final Logger LOGGER = Logger.getLogger(TestClient.class);
	private static final String URL =  "http://localhost:8185/ncert-service/";
//			"http://89.233.105.5:8180/ncert-service/";
//	 "http://89.233.105.90:8180/ncert-service/";
	// "https://test.ncf.inroad.in/ncert-service/";
	private static final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhamF5YWciLCJhdWQiOiJXIiwiaXNzIjoiTlBHIiwiZXhwIjoxNTkwNDk1OTA0LCJpYXQiOjE1OTA0Nzc5MDQsImp0aSI6IjEyNDUifQ.4mJQ3gHnunLERvS3la7q4sxGOkUD6m6lQiDD5pk55sKYTmbjda8F5ykO59VcdqeELrWE0nZkxHEh8JqJ99kSQw";
	private final static String[] methodNames = { "questionair", // 0
			"getOTP", // 1
			"notificationAppSendSms", // 2
			"sendOTP", // , 3
			"respondent", "surveyType", // 4
			"appVersion"
//      , "validateOTP"                                                                                                                                                                                                                                                            // 5
	};

	public static void main(String[] args) throws IOException {
//        getQuestionair();
//		getAppVersion("ios","1.0.4");
//		getSurveyType();
//		getAppVersion("android","1.0.0");
//		getAppVersion("ioS","1.0.4v"); //00.00.00 - 99.99.990
//        getRespondent(); //issue forbidden
//        sendOTP();
//       generateSurveyLink("9650335459", "8383067901", (byte)1);
//		 validateOTP();
        submitSurvey();
//        resendOTP();       
//		 dashboardAPI();//issue forbedden
//        new TestClient().calllinkWorkerRestservice();
//        System.out.println( AppEncryptionUtil.decrypt( "q0OlLcKZ4tOcDHKtf8JYvnSFrF2k1MUDGIbBK3JxBwIh5+2jZer0+Hn0EnSyhvzZ6oMh0JpG2hTnazGDzf6NjoDP/vFrijYJ6eqeqLisbgw=" ));
//        System.out.println( "out: "+AppEncryptionUtil.decrypt( "=JLBzGCANKzk1e3XeSxZoIvhYa9PVLNjp4T9UAlOSL5/KdBFEu4SJjO0bcbV5ZMNNoG9hkaS6lJvCxE3BSelqOfJ9ZNSkvZaoufg+CRNQaEh3Te3NL+w1RSoBkEgAuD/jHa853mLHFBSugJJEOscTEz+oIdTcv/NXoyLfgXuM2vY="));
		System.out.println(AppEncryptionUtil.decrypt("CERknkpHwy064vxB+nEXFw=="));
	}

	//#### KAMAL
	public static void getAppVersion(String userOs, String installedVersion) throws IOException {
		TestClient c = new TestClient();
		ObjectMapper mapper = new ObjectMapper();
		AppVersionRequestDto dto = new AppVersionRequestDto();
		dto.setUserOs(userOs);
		dto.setInstalledVersion(installedVersion);
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "en");
		String net_URL = URL + "custom/list/getAppVersion/appVersion";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[4]
				subURI[4], "list", "getAppVersion", String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		System.out.println("Signature: " + SignatureUtil.getSignature(subURI[4], "list", "getAppVersion",
				String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		HttpEntity<AppVersionRequestDto> request = new HttpEntity<AppVersionRequestDto>(dto, headers);
		c.callAppVersionRestservice(NPServiceParameter.LIST.getParameter(), "getRespondentType", request);
	}
	// ####

	public static void getRespondent() throws IOException {
		TestClient c = new TestClient();
		ObjectMapper mapper = new ObjectMapper();
		RequestDto dto = new RequestDto();
		dto.setUserId("1");
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "en");
		// headers.add("signature", SignatureUtil.getSignature(methodNames[0], "list",
		// "getRespondentType",
		// String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		String net_URL = URL + "api/list/getRespondentType/respondent";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[4]
				subURI[4], "list", "getRespondentType", String.valueOf(timestamp), mapper.writeValueAsString(dto),
				null));
		System.out.println("Signature: " + SignatureUtil.getSignature(subURI[4], "list", "getRespondentType",
				String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		HttpEntity<RequestDto> request = new HttpEntity<RequestDto>(dto, headers);
		c.callRespondentRestservice(NPServiceParameter.LIST.getParameter(), "getRespondentType", request);
	}
//***

	public static void getSurveyType() throws IOException {
		TestClient c = new TestClient();
		ObjectMapper mapper = new ObjectMapper();
		RequestDto dto = new RequestDto();
//            dto.setUserId( "1" );
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "en");
		// headers.add("signature", SignatureUtil.getSignature(methodNames[0], "list",
		// "getRespondentType",
		// String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		String net_URL = URL + "api/list/getSurveyType/surveyType";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[4]
				subURI[4], "list", "getSurveyType", String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		System.out.println("Signature: " + SignatureUtil.getSignature(subURI[4], "list", "getSurveyType",
				String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		HttpEntity<RequestDto> request = new HttpEntity<RequestDto>(dto, headers);
		c.callSurveyTypeRestservice(NPServiceParameter.LIST.getParameter(), "getSurveyType", request);
	}

	public ResponseModel<ArrayList<SurveyTypeDto>> callSurveyTypeRestservice(String operation, String className,
			HttpEntity<RequestDto> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String net_URL = URL + "api/list/getSurveyType/surveyType";
		// String net_URL = URL + "surveyApp/SaveLinksWorker";
		System.out.println(net_URL);
		ResponseModel<ArrayList<SurveyTypeDto>> roleResponse = restTemplate.postForObject(net_URL, request,
				ResponseModel.class);
		System.out.println(gson.toJson(request));
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

//******    

	public static void dashboardAPI() throws IOException {
		TestClient c = new TestClient();
		ObjectMapper mapper = new ObjectMapper();
		DashboardRequestDto dto = new DashboardRequestDto();
		dto.setUserId("9c3c32d5-77b2-40f2-a693-18abc0a8c2e4");
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "hin");
		// headers.add("signature", SignatureUtil.getSignature(methodNames[0], "list",
		// "getRespondentType",
		// String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		// String net_URL = URL +
		// "http://89.233.105.5:8180/ncert-service/api/count/dashboard/dashboard";
		String net_URL = URL + "api/count/dashboard/dashboard";

		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[4]
				subURI[4], subURI[5], subURI[6], String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		System.out.println("Signature: " + SignatureUtil.getSignature(subURI[4], subURI[5], subURI[6],
				String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		HttpEntity<DashboardRequestDto> request = new HttpEntity<DashboardRequestDto>(dto, headers);
		c.callDashboardRestservice(NPServiceParameter.LIST.getParameter(), subURI[4], request);
	}

	public static void submitSurvey() throws IOException {
		System.out.println("------------------------------- Test-submitSurvey ---------------------------");
		TestClient c = new TestClient();
		ObjectMapper mapper = new ObjectMapper();
		List<QuestionAnswerDto> list = new ArrayList<>();
		QuestionAnswerDto questionAnswerDto = new QuestionAnswerDto();
		/*
		 * questionAnswerDto.setQuestionId(2); questionAnswerDto.setAnswer("A");
		 * list.add(questionAnswerDto ); questionAnswerDto.setQuestionId(3);
		 * questionAnswerDto.setAnswer("B,C"); list.add(questionAnswerDto );
		 */
		questionAnswerDto.setQuestionId(1);
		questionAnswerDto.setAnswer("A,C");
		list.add(questionAnswerDto);
		QuestionAnswerDto questionAnswerDto1 = new QuestionAnswerDto();
		questionAnswerDto1.setQuestionId(2);
		questionAnswerDto1.setAnswer("A");
		list.add(questionAnswerDto1);
		QuestionAnswerDto questionAnswerDto2 = new QuestionAnswerDto();
		questionAnswerDto2.setQuestionId(3);
		questionAnswerDto2.setAnswer("A");
		list.add(questionAnswerDto2);
		QuestionAnswerDto questionAnswerDto3 = new QuestionAnswerDto();
		questionAnswerDto3.setQuestionId(4);
		questionAnswerDto3.setAnswer("A");
		list.add(questionAnswerDto3);
		QuestionAnswerDto questionAnswerDto4 = new QuestionAnswerDto();
		questionAnswerDto4.setQuestionId(5);
		questionAnswerDto4.setAnswer("A,C");
		list.add(questionAnswerDto4);
		SurveyDataDto requestDto = new SurveyDataDto();
		requestDto.setMobileNo(AppEncryptionUtil.encrypt("7068732070"));
		requestDto.setRespondentType("प्रौढ़ शिक्षा");
		requestDto.setStateId(23);
		requestDto.setUserId("166");
		requestDto.setQuestionnaireId(211);
		requestDto.setQuestionAnswerDto(list);
		requestDto.setLatitude("28.6490879");
		requestDto.setLongitude("77.3634703");
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "hin");
		String net_URL = URL + "custom/create/submitSurvey/submitSurvey";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(subURI[4], "create", "submitSurvey",
				String.valueOf(timestamp), mapper.writeValueAsString(requestDto), null));
		System.out.println("Signature: " + SignatureUtil.getSignature(subURI[4], "create", "submitSurvey",
				String.valueOf(timestamp), mapper.writeValueAsString(requestDto), null));
		System.out.println("Data:" + mapper.writeValueAsString(requestDto));
		HttpEntity<SurveyDataDto> request = new HttpEntity<SurveyDataDto>(requestDto, headers);
		c.callSubmitSurvey(NPServiceParameter.LIST.getParameter(), "submitSurvey", request);
	}

	public ResponseModel<String[]> callSubmitSurvey(String operation, String className,
			HttpEntity<SurveyDataDto> request) {
		System.out.println("------------------------------- Test-callSubmitSurvey ---------------------------");
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String net_URL = URL + "custom/create/submitSurvey/submitSurvey";
		ResponseModel<String[]> roleResponse = restTemplate.postForObject(net_URL, request, ResponseModel.class);
		System.out.println(gson.toJson(request));
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	public static void sendOTP() throws IOException {
		TestClient c = new TestClient();
		// RequestDto dto = new RequestDto();
		SendOTPRequestDto requestDto = new SendOTPRequestDto();
		// requestDto.setMobileNo(AppEncryptionUtil.encrypt("7683098534"));
		requestDto.setMobileNo(AppEncryptionUtil.encrypt("7683098534"));
		requestDto.setStateId(23);
		requestDto.setRespondentType("प्रौढ़ शिक्षा");
		requestDto.setUserId("166");
		ObjectMapper mapper = new ObjectMapper();
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "hin");
		String net_URL = URL + "custom/create/sendOTP/sendOTP";
//        headers.add("accept-language","en");
		// String net_URL =
		// "http://89.233.105.5:8180/ncert-service/custom/create/sendOTP/sendOTP";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[3]
				subURI[4], "create", "sendOTP", String.valueOf(timestamp), mapper.writeValueAsString(requestDto),
				null));
		System.out.println("signature" + SignatureUtil.getSignature(
				// methodNames[3]
				subURI[4], "create", "sendOTP", String.valueOf(timestamp), mapper.writeValueAsString(requestDto),
				null));
		// headers.remove( HttpHeaders.ACCEPT_LANGUAGE );
		// headers.add( "accept-language", "hin" );
		System.out.println(SignatureUtil.getSignature(
				// methodNames[3]
				subURI[4], "create", "sendOTP", String.valueOf(timestamp), mapper.writeValueAsString(requestDto)));
		HttpEntity<SendOTPRequestDto> request = new HttpEntity<SendOTPRequestDto>(requestDto, headers);
		c.callSendOTPRestservice(NPServiceParameter.CREATE.getParameter(), "sendOTP", request);
	}

	public static void resendOTP() throws IOException {
		TestClient c = new TestClient();
		// RequestDto dto = new RequestDto();
		ResendOTPRequestModel requestDto = new ResendOTPRequestModel();
		requestDto.setMobileNo(AppEncryptionUtil.encrypt("8383067901"));
//        requestDto.setMobileNo( "991132757" );
		requestDto.setTxnId("1a199b8c1f97c140da9ee61d870dfbf6a85f17b3");
		requestDto.setStateId(23);
		requestDto.setUserId("9c3c32d5-77b2-40f2-a693-18abc0a8c2e4");
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(AppEncryptionUtil.encrypt("9389740045"));
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "hin");
		// String net_URL = URL + "custom/sendOTP/create/sendOTP";
		String net_URL = URL + "custom/create/resendOTP/resendOTP";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[3]
				subURI[4], "create", "resendOTP", String.valueOf(timestamp), mapper.writeValueAsString(requestDto),
				null));
		System.out.println(SignatureUtil.getSignature(
				// methodNames[3] 1632393660689 1632393652725
				subURI[4], "create", "resendOTP", String.valueOf(timestamp), mapper.writeValueAsString(requestDto)));
		System.out.println(mapper.writeValueAsString(requestDto));
		HttpEntity<ResendOTPRequestModel> request = new HttpEntity<ResendOTPRequestModel>(requestDto, headers);
		c.callResendOTPRestservice(NPServiceParameter.CREATE.getParameter(), "resendOTP", request);
	}

	public static void validateOTP() throws IOException {
		TestClient c = new TestClient();
		// RequestDto dto = new RequestDto();
		ValidateOTPRequestModel requestDto = new ValidateOTPRequestModel();
		requestDto.setStateId(23);
		requestDto.setUserId("123");
		requestDto.setOtp(552857);
		requestDto.setTsnId("1ee389d912f0b208117678253c27452e42567262");
		requestDto.setRespondentType("6");
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(AppEncryptionUtil.encrypt("7683098534"));
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "hin");
		// String net_URL = URL + "custom/validateOTP/validate/validateOTP";
		String net_URL = URL + "custom/validate/validateOTP/validateOTP";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[5]
				subURI[4], "validate", "validateOTP", String.valueOf(timestamp), mapper.writeValueAsString(requestDto),
				null));
		HttpEntity<ValidateOTPRequestModel> request = new HttpEntity<ValidateOTPRequestModel>(requestDto, headers);
		c.callValidateOTPRestservice(NPServiceParameter.VALIDATE.getParameter(), "validateOTP", request);
	}

	public static void generateSurveyLink(String respondentMN, String surveyourMN, byte isFill) throws IOException {
		TestClient c = new TestClient();
		// RequestDto dto = new RequestDto();
		SurveyLinkDto requestDto = new SurveyLinkDto();
		// requestDto.setRespondentMobileNumber( "7973857970" );
		requestDto.setRespondentMobileNumber(AppEncryptionUtil.encrypt(respondentMN));
		requestDto.setIsfillsurvey(isFill);
		requestDto.setSurveyorMobileNumber(AppEncryptionUtil.encrypt(surveyourMN));
		requestDto.setRespondentType("2");
		requestDto.setStateId(23);
		requestDto.setUserId("166");
		requestDto.setEmailId(AppEncryptionUtil.encrypt("kamal.chadha651@gmail.com"));
		requestDto.setFirstName(AppEncryptionUtil.encrypt(" Kamal "));
		requestDto.setLastName(AppEncryptionUtil.encrypt("Chadha"));
		requestDto.setSurveyType(2);
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "en");
		ObjectMapper mapper = new ObjectMapper();
		String net_URL = URL + "api/create/generateSurveyLink/generateSurveyLink";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[0]
				subURI[4], subURI[5], subURI[6], String.valueOf(timestamp), mapper.writeValueAsString(requestDto),
				null));
		/*
		 * headers.add("signature", SignatureUtil.getSignature(methodNames[0], "list",
		 * "getQuestionnaire", String.valueOf(timestamp),
		 * mapper.writeValueAsString(requestDto), null));
		 */
		HttpEntity<SurveyLinkDto> request = new HttpEntity<SurveyLinkDto>(requestDto, headers);
		c.callLinkRestservice(NPServiceParameter.CREATE.getParameter(), "generateSurveyLink", request);
	}

	public static void generateSurveyLink2() throws IOException {
		TestClient c = new TestClient();
		// RequestDto dto = new RequestDto();
		SurveyLinkDto requestDto = new SurveyLinkDto();
		requestDto.setRespondentMobileNumber("7683098534");
		requestDto.setIsfillsurvey((byte) 1);
		requestDto.setSurveyorMobileNumber("9911327757");
		requestDto.setRespondentType("Teacher");
		requestDto.setStateId(23);
		requestDto.setUserId("299");
		requestDto.setEmailId("tarun.gariya@netprophetsglobal.com");
		requestDto.setFirstName("Shubham");
		requestDto.setLastName("IronMan");
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "hin");
		ObjectMapper mapper = new ObjectMapper();
		String net_URL = URL + "api/create/generateSurveyLink/generateSurveyLink";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[0]
				subURI[4], subURI[5], subURI[6], String.valueOf(timestamp), mapper.writeValueAsString(requestDto),
				null));
		/*
		 * headers.add("signature", SignatureUtil.getSignature(methodNames[0], "list",
		 * "getQuestionnaire", String.valueOf(timestamp),
		 * mapper.writeValueAsString(requestDto), null));
		 */
		HttpEntity<SurveyLinkDto> request = new HttpEntity<SurveyLinkDto>(requestDto, headers);
		c.callLinkRestservice(NPServiceParameter.CREATE.getParameter(), "generateSurveyLink", request);
	}

	public static void getQuestionair() throws IOException {
		LOGGER.info("------------------------------- Test-getQuestionair ---------------------------");
		TestClient c = new TestClient();
		// RequestDto dto = new RequestDto();
		ObjectMapper mapper = new ObjectMapper();
		QuestionairRequestDto requestDto = new QuestionairRequestDto();
		requestDto.setMobileNo(("9911327757"));
		requestDto.setRespondentType("प्रौढ़ शिक्षा");
		requestDto.setStateId(23);
		requestDto.setUserId("166");
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "hin");
		// String net_URL =
		// "http://89.233.105.5:8180/ncert-service/custom/list/getQuestionnaire/questionnaire";
		String net_URL = URL + "custom/list/getQuestionnaire/questionnaire";
		String subURI[] = net_URL.split("/");
		headers.add("signature", SignatureUtil.getSignature(
				// methodNames[0]
				subURI[4], "list", "getQuestionnaire", String.valueOf(timestamp), mapper.writeValueAsString(requestDto),
				null));

		HttpEntity<QuestionairRequestDto> request = new HttpEntity<QuestionairRequestDto>(requestDto, headers);
		c.callRestservice(NPServiceParameter.LIST.getParameter(), "getQuestionnaire", request);
	}

	public ResponseModel<QuestionairResponseDto> callRestservice(String operation, String className,
			HttpEntity<QuestionairRequestDto> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		System.out.println(gson.toJson(request));
		// ResponseModel<QuestionairResponseDto> roleResponse = restTemplate
		// .postForObject(URL+"custom/" + methodNames[0] + "/" + operation + "/" +
		// className, request, ResponseModel.class);
		// String net_URL =
		// "http://89.233.105.5:8180/ncert-service/custom/list/getQuestionnaire/questionnaire";
		String net_URL = URL + "custom/list/getQuestionnaire/questionnaire";
		ResponseModel<QuestionairResponseDto> roleResponse = restTemplate.postForObject(net_URL, request,
				ResponseModel.class);
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	public ResponseModel<ArrayList<RespondentTypeDto>> callRespondentRestservice(String operation, String className,
			HttpEntity<RequestDto> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String net_URL = URL + "api/list/getRespondentType/respondent";
		// String net_URL = URL + "surveyApp/SaveLinksWorker";
		System.out.println(net_URL);
		ResponseModel<ArrayList<RespondentTypeDto>> roleResponse = restTemplate.postForObject(net_URL, request,
				ResponseModel.class);
		System.out.println(gson.toJson(request));
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	//### KAMAL
	public ResponseModel<ArrayList<AppVersionRequestDto>> callAppVersionRestservice(String operation, String className,
			HttpEntity<AppVersionRequestDto> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String net_URL = URL + "custom/list/getAppVersion/appVersion";
		//	String net_URL = URL + "surveyApp/SaveLinksWorker";
		System.out.println(net_URL);
		ResponseModel<ArrayList<AppVersionRequestDto>> roleResponse = restTemplate.postForObject(net_URL, request,
				ResponseModel.class);
		System.out.println(gson.toJson(request));
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}
	//###
	public ResponseModel<String[]> calllinkWorkerRestservice() {
		TestClient c = new TestClient();
		ObjectMapper mapper = new ObjectMapper();
		RequestDto dto = new RequestDto();
		dto.setUserId("1");
		long timestamp = System.currentTimeMillis();
		HttpHeaders headers = getHeader(timestamp, "hin");
		// headers.add("signature", SignatureUtil.getSignature(methodNames[0], "list",
		// "getRespondentType",
		// String.valueOf(timestamp), mapper.writeValueAsString(dto), null));
		HttpEntity<RequestDto> request = new HttpEntity<RequestDto>(dto, headers);

		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		// String net_URL = URL + "api/list/getRespondentType/respondent";
		String net_URL = "http://89.233.105.5:8184/ncert-service/surveyApp/SaveLinksWorker";
		System.out.println(net_URL);
		ResponseModel<String[]> roleResponse = restTemplate.postForObject(net_URL, request, ResponseModel.class);
		System.out.println(gson.toJson(request));
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	public ResponseModel<DashboardDto> callDashboardRestservice(String operation, String className,
			HttpEntity<DashboardRequestDto> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String net_URL = URL + "api/count/dashboard/dashboard";
		System.out.println(gson.toJson(request));
		ResponseModel<DashboardDto> roleResponse = restTemplate.postForObject(net_URL, request, ResponseModel.class);
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	public ResponseModel<ResponseDto> callLinkRestservice(String operation, String className,
			HttpEntity<SurveyLinkDto> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String net_URL = URL + "api/create/generateSurveyLink/generateSurveyLink";
		ResponseModel<ResponseDto> roleResponse = restTemplate.postForObject(net_URL, request, ResponseModel.class);
		System.out.println(gson.toJson(request));
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	public ResponseModel<SendOTPResponseModel> callSendOTPRestservice(String operation, String className,
			HttpEntity<SendOTPRequestDto> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		System.out.println(gson.toJson(request));
		String net_URL = URL + "custom/create/sendOTP/sendOTP";
		// ResponseModel<NotificationResponseDto> roleResponse =
		// restTemplate.postForObject(URL+"custom/" + operation + "/" + className+
		// "/sendOTP" ,
		// request, ResponseModel.class);
		ResponseModel<SendOTPResponseModel> roleResponse = restTemplate.postForObject(net_URL, request,
				ResponseModel.class);
		ObjectMapper mapper = new ObjectMapper();
		// NotificationResponseDto notificationResponseDto =
		// mapper.convertValue(roleResponse.getData(), NotificationResponseDto.class);
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	public ResponseModel<SendOTPResponseModel> callResendOTPRestservice(String operation, String className,
			HttpEntity<ResendOTPRequestModel> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		System.out.println(gson.toJson(request));
		String net_URL = URL + "custom/create/resendOTP/resendOTP";
		// ResponseModel<NotificationResponseDto> roleResponse =
		// restTemplate.postForObject(URL+"custom/" + operation + "/" + className+
		// "/sendOTP" ,
		// request, ResponseModel.class);
		ResponseModel<SendOTPResponseModel> roleResponse = restTemplate.postForObject(net_URL, request,
				ResponseModel.class);
		ObjectMapper mapper = new ObjectMapper();
		// NotificationResponseDto notificationResponseDto =
		// mapper.convertValue(roleResponse.getData(), NotificationResponseDto.class);
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	public ResponseModel<ValidateOTPRequestDto> callValidateOTPRestservice(String operation, String className,
			HttpEntity<ValidateOTPRequestModel> request) {
		Gson gson = new Gson();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String net_URL = URL + "custom/validate/validateOTP/validateOTP";
		ResponseModel<ValidateOTPRequestDto> roleResponse = restTemplate.postForObject(net_URL, request,
				ResponseModel.class);
		System.out.println(gson.toJson(request));
		System.out.println(gson.toJson(roleResponse));
		return roleResponse;
	}

	private static HttpHeaders getSecureHeader(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.add("Accept", "application/json");
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private static HttpHeaders getHeader(long timestam, String language) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Accept-Language", language);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("timestamp", timestam + "");
		return headers;
	}
}
