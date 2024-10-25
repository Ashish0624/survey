package com.ncert.survey.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ncert.survey.businessMgr.RedisMgrImpl;
import com.ncert.survey.businessMgr.SurveyMgrImpl;
import com.ncert.survey.config.Translator;
import com.ncert.survey.costants.APPServiceCode;
import com.ncert.survey.costants.CheckSignature;
import com.ncert.survey.dto.AppVersionRequestDto;
import com.ncert.survey.dto.AppVersionResponseDto;
import com.ncert.survey.dto.DashboardDto;
import com.ncert.survey.dto.DashboardRequestDto;
import com.ncert.survey.dto.ErrorModel;
import com.ncert.survey.dto.NotificationResponseDto;
import com.ncert.survey.dto.QuestionairRequestDto;
import com.ncert.survey.dto.QuestionairResponseDto;
import com.ncert.survey.dto.RequestDto;
import com.ncert.survey.dto.ResendOTPRequestModel;
import com.ncert.survey.dto.RespondentTypeDto;
import com.ncert.survey.dto.ResponseDto;
import com.ncert.survey.dto.ResponseModel;
import com.ncert.survey.dto.SendOTPRequestDto;
import com.ncert.survey.dto.SendOTPResponseDto;
import com.ncert.survey.dto.SendOTPResponseModel;
import com.ncert.survey.dto.SurveyDataDto;
import com.ncert.survey.dto.SurveyLinkDto;
import com.ncert.survey.dto.SurveyTypeDto;
import com.ncert.survey.dto.URLShortnerDto;
import com.ncert.survey.dto.ValidateOTPRequestDto;
import com.ncert.survey.dto.ValidateOTPRequestModel;
import com.ncert.survey.model.MasterSurveyType;
import com.ncert.survey.utils.StringUtils;

@RestController
@RequestMapping("/ncert-service")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AppController
{
    private static final Logger LOGGER                 = Logger.getLogger( AppController.class );
    private static final String APPLICATION_JSON       = "application/json";
    private static final String GET_RESPONDENT_TYPE    = "/api/{serviceParam}/{requestParam}/respondent";
    private static final String GET_SURVEY_TYPE		   = "/api/{serviceParam}/{requestParam}/surveyType";
    private static final String GET_APP_VERSION		   = "/custom/{serviceParam}/{requestParam}/appVersion";
    private static final String GET_QUESTIONNAIRE      = "/custom/{serviceParam}/{requestParam}/questionnaire";
    private static final String SUBMIT_SURVEY          = "/custom/{serviceParam}/{requestParam}/submitSurvey";
    private static final String GENERATE_SURVEY_LINK   = "/api/{serviceParam}/{requestParam}/generateSurveyLink";
    private static final String SEND_OTP               = "/custom/{serviceParam}/{requestParam}/sendOTP";
    private static final String DASHBOARD_DATA         = "/api/{serviceParam}/{requestParam}/dashboard";
    private static final String VALIDATE_OTP           = "/custom/{serviceParam}/{requestParam}/validateOTP";
    private static final String RESEND_OTP             = "/custom/{serviceParam}/{requestParam}/resendOTP";
    private static final String LINK_SHORTNER          = "/custom/{serviceParam}/{requestParam}/shortLink";
    private static final String LINK_SHORTNER_REDIRECT = "/custom/{shortLink}";
    @Autowired
    private RedisMgrImpl        redisMgrImpl;
    @Autowired
    private SurveyMgrImpl       surveyMgrImpl;
    private Translator          translator;
    public AppController( Translator translator )
    {
        super();
        this.translator = translator;
    }

    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = GET_RESPONDENT_TYPE, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<ArrayList<RespondentTypeDto>> getRespondentType( @RequestHeader Map httpHeaders,
                                                                    @PathVariable(value = "serviceParam") String inServiceParam,
                                                                    @PathVariable(value = "requestParam") String inClientParam,
                                                                    @Valid @RequestBody RequestDto requestDto,
                                                                    HttpServletRequest inRequest,
                                                                    HttpServletResponse inResponse )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return redisMgrImpl.getRespondentTypes( translator, inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE ) );
    }
    
 // ***   
    
    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = GET_SURVEY_TYPE, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<ArrayList<SurveyTypeDto>> getSurveyType( @RequestHeader Map httpHeaders,
                                                                    @PathVariable(value = "serviceParam") String inServiceParam,
                                                                    @PathVariable(value = "requestParam") String inClientParam,
                                                                    @Valid @RequestBody RequestDto requestDto,
                                                                    HttpServletRequest inRequest,
                                                                    HttpServletResponse inResponse )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return redisMgrImpl.getSurveyTypes( translator, inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE ) );
    }
    
//  ******
    
    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = GET_QUESTIONNAIRE, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<QuestionairResponseDto> getQuestionnaire( @RequestHeader Map httpHeaders,
                                                                                 @PathVariable(value = "serviceParam") String inServiceParam,
                                                                                 @PathVariable(value = "requestParam") String inClientParam,
                                                                                 @Valid @RequestBody QuestionairRequestDto requestDto,
                                                                                 HttpServletRequest inRequest,
                                                                                 HttpServletResponse inResponse )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return redisMgrImpl.processQuestionnaire( requestDto, translator,
                                                  inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE ) );
    }

    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = SEND_OTP, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<SendOTPResponseModel> sendOTP( @RequestHeader Map httpHeaders,
                                                                      @PathVariable(value = "serviceParam") String inServiceParam,
                                                                      @PathVariable(value = "requestParam") String inClientParam,
                                                                      @Valid @RequestBody SendOTPRequestDto sendOTPRequestDto,
                                                                      HttpServletRequest inRequest,
                                                                      HttpServletResponse inResponse )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return surveyMgrImpl.otpRequest( sendOTPRequestDto, translator,
                                         inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE ) );
    }

    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = GENERATE_SURVEY_LINK, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<ResponseDto> generateSurveyLink( @RequestHeader Map httpHeaders,
                                                                        @PathVariable(value = "serviceParam") String inServiceParam,
                                                                        @PathVariable(value = "requestParam") String inClientParam,
                                                                        @Valid @RequestBody SurveyLinkDto surveyLinkDto,
                                                                        HttpServletRequest inRequest,
                                                                        HttpServletResponse inResponse )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return surveyMgrImpl.generateSurveyLink( surveyLinkDto, translator,
                                                 inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE ) );
    }

    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = SUBMIT_SURVEY, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<ResponseDto> submitSurvey( @RequestHeader Map httpHeaders,
                                                                  @PathVariable(value = "serviceParam") String inServiceParam,
                                                                  @PathVariable(value = "requestParam") String inClientParam,
                                                                  @Valid @RequestBody SurveyDataDto surveyDataDto,
                                                                  HttpServletRequest inRequest,
                                                                  HttpServletResponse inResponse )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return surveyMgrImpl.saveSurveyData( surveyDataDto, translator,
                                             inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE )  );
    }

    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = DASHBOARD_DATA, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<DashboardDto> dashboardData( @RequestHeader Map httpHeaders,
                                                                    @PathVariable(value = "serviceParam") String inServiceParam,
                                                                    @PathVariable(value = "requestParam") String inClientParam,
                                                                    @Valid @RequestBody DashboardRequestDto dashboardDto,
                                                                    HttpServletRequest inRequest,
                                                                    HttpServletResponse inResponse )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return surveyMgrImpl.dashboardData( dashboardDto, translator );
    }

    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = VALIDATE_OTP, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<SendOTPResponseDto> verifyOTP( @RequestHeader Map httpHeaders,
                                                                      @PathVariable(value = "serviceParam") String inServiceParam,
                                                                      @PathVariable(value = "requestParam") String inClientParam,
                                                                      @Valid @RequestBody ValidateOTPRequestModel validateOTPRequestModel,
                                                                      HttpServletRequest inRequest,
                                                                      HttpServletResponse inResponse,
                                                                      @RequestHeader("accept-language") String language )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return surveyMgrImpl.validateOTP( validateOTPRequestModel, translator,
                                          inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE ) );
    }

    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = RESEND_OTP, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<SendOTPResponseModel> resendOTP( @RequestHeader Map httpHeaders,
                                                                        @PathVariable(value = "serviceParam") String inServiceParam,
                                                                        @PathVariable(value = "requestParam") String inClientParam,
                                                                        @Valid @RequestBody ResendOTPRequestModel resendOTPRequestModel,
                                                                        HttpServletRequest inRequest,
                                                                        HttpServletResponse inResponse,
                                                                        @RequestHeader("accept-language") String language )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return surveyMgrImpl.resendOtpRequest( resendOTPRequestModel, translator,
                                               inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE ) );
    }

    //	@CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = LINK_SHORTNER, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<URLShortnerDto> getShortLink( @RequestHeader Map httpHeaders,
                                                                     @PathVariable(value = "serviceParam") String inServiceParam,
                                                                     @PathVariable(value = "requestParam") String inClientParam,
                                                                     @Valid @RequestBody URLShortnerDto requestDto,
                                                                     HttpServletRequest inRequest,
                                                                     HttpServletResponse inResponse )
        throws Exception
    {
        ResponseModel<URLShortnerDto> responseModel = new ResponseModel<>();
        URLShortnerDto response = new URLShortnerDto();
        String shortUrl = surveyMgrImpl.getlinkShortner( requestDto.getUrl() );
        if ( StringUtils.isNotEmpty( shortUrl ) )
        {
            response.setUrl( shortUrl );
            responseModel.setData( response );
        }
        else
        {
            List<ErrorModel> errorModels = new ArrayList<>();
            ErrorModel errorModel = new ErrorModel();
            errorModel.setCode( APPServiceCode.NP_SERVICE_999.getStatusCode() );
            errorModel.setMessage( translator.toLocale( APPServiceCode.NP_SERVICE_999.getStatusCode() ) );
            errorModels.add( errorModel );
            responseModel.setErrors( errorModels );
        }
        return responseModel;
    }

    @RequestMapping(value = LINK_SHORTNER_REDIRECT, method =
    { RequestMethod.GET })
    public ResponseEntity<?> redirectURL( @PathVariable String shortLink, HttpServletResponse response )
        throws Exception
    {
        String longUrl = "";
        if ( StringUtils.isNotEmpty( shortLink ) )
        {
            longUrl = redisMgrImpl.getLongUrl( shortLink );
            if ( StringUtils.isNotEmpty( longUrl ) )
            {
                response.sendRedirect( longUrl );
            }
        }
        return null;
    }
    
    @CheckSignature
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = GET_APP_VERSION, method =
    { RequestMethod.POST }, consumes = APPLICATION_JSON)
    public @ResponseBody ResponseModel<AppVersionResponseDto> getAppVersion( @RequestHeader Map httpHeaders,
                                                                    @PathVariable(value = "serviceParam") String inServiceParam,
                                                                    @PathVariable(value = "requestParam") String inClientParam,
                                                                    @Valid @RequestBody AppVersionRequestDto appVersionRequestDto,
                                                                    HttpServletRequest inRequest,
                                                                    HttpServletResponse inResponse )
        throws Exception
    {
        System.out.println( httpHeaders.toString() );
        return surveyMgrImpl.appVersionData( appVersionRequestDto,translator, inRequest.getHeader( HttpHeaders.ACCEPT_LANGUAGE )); //appVersionData
    }
}
