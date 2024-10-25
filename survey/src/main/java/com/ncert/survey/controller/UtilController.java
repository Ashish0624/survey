package com.ncert.survey.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ncert.survey.businessMgr.RedisMgrImpl;
import com.ncert.survey.businessMgr.SurveyMgrImpl;
import com.ncert.survey.config.Translator;
import com.ncert.survey.costants.APPServiceCode;
import com.ncert.survey.dto.ErrorModel;
import com.ncert.survey.dto.ResponseModel;
import com.ncert.survey.dto.URLShortnerDto;
import com.ncert.survey.utils.StringUtils;

@RestController
@RequestMapping("${utilcontroller.linkshortner.url}")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UtilController {

	private static final Logger LOGGER = Logger.getLogger(AppController.class);
	private static final String APPLICATION_JSON = "application/json";
	private static final String LINK_SHORTNER = "/custom/shortLink";
	private static final String LINK_SHORTNER_REDIRECT = "/{shortLink}";
	// shortUrl = "http://89.233.105.5:8180/ncert-service/custom/" + encodeUrl;
	// test.ncf.inroad.in/encodedURL
	@Autowired
	private RedisMgrImpl redisMgrImpl;
	@Autowired
	private SurveyMgrImpl surveyMgrImpl;
	private Translator translator;

	public UtilController(Translator translator) {
		super();
		this.translator = translator;
	}

	// @CheckSignature
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = LINK_SHORTNER, method = { RequestMethod.POST }, consumes = APPLICATION_JSON)
	public @ResponseBody ResponseModel<URLShortnerDto> getShortLink(@Valid @RequestBody URLShortnerDto requestDto,
			HttpServletRequest inRequest, HttpServletResponse inResponse) throws Exception {
		ResponseModel<URLShortnerDto> responseModel = new ResponseModel<>();
		URLShortnerDto response = new URLShortnerDto();
		String shortUrl = surveyMgrImpl.getlinkShortner(requestDto.getUrl());
		if (StringUtils.isNotEmpty(shortUrl)) {
			response.setUrl(shortUrl);
			responseModel.setData(response);
		} else {
			List<ErrorModel> errorModels = new ArrayList<>();
			ErrorModel errorModel = new ErrorModel();
			errorModel.setCode(APPServiceCode.NP_SERVICE_999.getStatusCode());
			errorModel.setMessage(translator.toLocale(APPServiceCode.NP_SERVICE_999.getStatusCode()));
			errorModels.add(errorModel);
			responseModel.setErrors(errorModels);
		}
		return responseModel;
	}

	@RequestMapping(value = LINK_SHORTNER_REDIRECT, method = { RequestMethod.GET })
	public ResponseEntity<?> redirectURL(@PathVariable("shortLink") String shortLink, HttpServletResponse response)
			throws Exception {
	    LOGGER.info( "Inside redirectURL with shortURL: "+shortLink );
		String longUrl = "";
		if (StringUtils.isNotEmpty(shortLink)) {
			longUrl = redisMgrImpl.getLongUrl(shortLink);
			if (StringUtils.isNotEmpty(longUrl)) {
				response.sendRedirect(longUrl);
			}
		}
		return null;
	}
}
