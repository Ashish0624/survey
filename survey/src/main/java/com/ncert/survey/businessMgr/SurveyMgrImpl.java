package com.ncert.survey.businessMgr;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.ncert.survey.config.Translator;
import com.ncert.survey.costants.APPServiceCode;
import com.ncert.survey.costants.IAppConstants;
import com.ncert.survey.dto.AppVersionRequestDto;
import com.ncert.survey.dto.AppVersionResponseDto;
import com.ncert.survey.dto.DashboardDto;
import com.ncert.survey.dto.DashboardRequestDto;
import com.ncert.survey.dto.ErrorModel;
import com.ncert.survey.dto.NotificationResponseDto;
import com.ncert.survey.dto.NotificationServiceResponseModel;
import com.ncert.survey.dto.OtpServiceResponseModel;
import com.ncert.survey.dto.OtpServicesErrorModel;
import com.ncert.survey.dto.QuestionAnswerDto;
import com.ncert.survey.dto.ResendOTPRequestModel;
import com.ncert.survey.dto.ResponseDto;
import com.ncert.survey.dto.ResponseModel;
import com.ncert.survey.dto.SMSRequestDto;
import com.ncert.survey.dto.SendMailRequestDto;
import com.ncert.survey.dto.SendOTPRequestDto;
import com.ncert.survey.dto.SendOTPResponseDto;
import com.ncert.survey.dto.SendOTPResponseModel;
import com.ncert.survey.dto.SurveyDataDto;
import com.ncert.survey.dto.SurveyLinkDto;
import com.ncert.survey.dto.ValidateOTPRequestDto;
import com.ncert.survey.dto.ValidateOTPRequestModel;
import com.ncert.survey.model.AppVersion;
import com.ncert.survey.model.MstrState;
import com.ncert.survey.model.SurveyData;
import com.ncert.survey.model.SurveyorRespondentDetails;
import com.ncert.survey.repository.AppVersionRepository;
import com.ncert.survey.service.MstrStateService;
import com.ncert.survey.service.SurveyDataService;
import com.ncert.survey.service.SurveyorRespondentDetailsService;
import com.ncert.survey.utils.AppEncryptionUtil;
import com.ncert.survey.utils.SignatureUtil;
import com.ncert.survey.utils.StringUtils;
import com.ncert.survey.utils.Validation;
import com.ncert.survey.utils.ValidationUtil;

public class SurveyMgrImpl
{
    private static final Logger              LOGGER = Logger.getLogger( SurveyMgrImpl.class );
    @Autowired
    private Validation                       validation;
    @Autowired
    private SurveyorRespondentDetailsService surveyorRespondentDetailsService;
    @Autowired
    private SurveyDataService                surveyDataService;
    @Autowired
    private RedisMgrImpl                     redisMgrImpl;
    @Autowired
    private ValidationUtil					 validationUtil;
    @Autowired
    private AppVersionRepository 			 appVersionRepository;
    @Autowired
    private MstrStateService                 mstrStateService;
    @Value("${notification.send.mail.url}")
    private String                           NOTIFICATION_SEND_MAIL_URL;
    @Value("${notification.send.sms.url}")
    private String                           NOTIFICATION_SEND_SMS_URL;
    @Value("${otp.getOTP.url}")
    private String                           OTP_GETOTP_URL;
    @Value("${otp.validateOTP.url}")
    private String                           OTP_VALIDATEOTP_URL;
    @Value("${short.url.generation}")
    private String                           SHORT_URL_GENERATION;
    @Value("${otp.resend.url}")
    private String                           OTP_RESEND_URL;
    @Value("${web.url.generation}")
    private String                           WEB_URL_GENERATION;
    @Value("${smstemplate.linkgeneration.surveyor}")
    private String                           SMSTEMPLATE_LINKGENERATION_SURVEYOR;
    @Value("${smstemplate.linkgeneration.respondent}")
    private String                           SMSTEMPLATE_LINKGENERATION_RESPONDENT;
    @Value("${smstemplate.sendOtp}")
    private String                           SMSTEMPLATE_SENDOTP;
    @Value("${smstemplateId.linkgeneration.surveyor}")
    private int                              SMSTEMPLATEID_LINKGENERATION_SURVEYOR;
    @Value("${smstemplateId.linkgeneration.respondent}")
    private int                              SMSTEMPLATEID_LINKGENERATION_RESPONDENT;
    @Value("${smstemplateId.sendOtp}")
    private int                              SMSTEMPLATEID_SENDOTP;
    public ResponseModel<DashboardDto> dashboardData( DashboardRequestDto dashboardRequestDto, Translator translator )
        throws Exception
    {
    	LOGGER.info( "dashboardData :: request :: " +dashboardRequestDto.getUserId());
        ResponseModel<DashboardDto> responseModel = new ResponseModel<>();
        List<ErrorModel> errorDtos = new ArrayList<>();
        DashboardDto dashboardDto = null;
        APPServiceCode serviceCode = null;
        LOGGER.info( "#@123@#------------------------------- SurveyMgrImpl : Dashboard ---------------------------" );
        if ( StringUtils.isValidObj( dashboardRequestDto ) && !StringUtils.isEmpty( dashboardRequestDto.getUserId() ) )
        {
            try
            {
                dashboardDto = surveyorRespondentDetailsService.getDashboardData( dashboardRequestDto.getUserId() );
                responseModel.setData( dashboardDto );
            }
            catch ( Exception ex )
            {
            	LOGGER.error( "dashboardData :: Exception :- " +ex);
                //LOGGER.info( "dashboardData Exception :- " + ex );
                ex.printStackTrace();
                serviceCode = APPServiceCode.NP_SERVICE_999;
                errorDtos = setErrorData( serviceCode, errorDtos, translator );
            }
        }
        else
        {
            serviceCode = APPServiceCode.NP_SERVICE_997;
            errorDtos = setErrorData( serviceCode, errorDtos, translator );
            LOGGER.info( "dashboardData ::" +"dashboardData :- Invalid Request : User id is not valid" );
            //LOGGER.info( "dashboardData :- Invalid Request : User id is not valid" );
            LOGGER.info( "dashboardData :: "+"dashboardData :- Response : Status Code" + serviceCode.getStatusCode() + "Status Desc : "
                    + serviceCode.getStatusDesc() );
        }
        if ( StringUtils.isValidCollection( errorDtos ) )
        {
            responseModel.setErrors( errorDtos );
        }
        LOGGER.info( "dashboardData :: response" +	responseModel.toString() );
        return responseModel;
    }

    public ResponseModel<ResponseDto> generateSurveyLink( SurveyLinkDto surveyLinkDto,
                                                          Translator translator,
                                                          String language )
        throws Exception
    {
    	LOGGER.info( "generateSurveyLink :: request :: " + surveyLinkDto.toString() );
        ResponseModel<ResponseDto> responseModel = new ResponseModel<>();
        ResponseDto responseDto = new ResponseDto();
        List<ErrorModel> errorDtos = new ArrayList<>();
        String link;
        SurveyorRespondentDetails surveyorRespondentDetails = new SurveyorRespondentDetails();
        APPServiceCode serviceCode = null;
      //  System.out
      //          .println( "------------------------------- Survey Mgr Impl : Generate Survey Link ---------------------------" );
        serviceCode = validation.generateSurveyLink( surveyLinkDto, language );
        LOGGER.info( "generateSurveyLink :: service code:- "+serviceCode);
        if ( !StringUtils.isValidObj( serviceCode ) )
        {
            //            MstrState state = redisMgrImpl.getState( surveyLinkDto.getStateId() );
            MstrState state = mstrStateService.findById( surveyLinkDto.getStateId() );
            if ( StringUtils.isValidObj( state ) )
            {
                SurveyorRespondentDetails dbSurveyorRespondentDetails = surveyorRespondentDetailsService
                        .findByRespondentMobile( surveyLinkDto.getRespondentMobileNumber() );
                if ( !StringUtils.isValidObj( dbSurveyorRespondentDetails ) )
                {
                    surveyorRespondentDetails.setUserId( surveyLinkDto.getUserId() );
                    surveyorRespondentDetails.setRespondentType( redisMgrImpl.getRespondentList( "en" )[Integer
                            .parseInt( surveyLinkDto.getRespondentType() )] );
                    surveyorRespondentDetails.setRespondentMobileNumber( surveyLinkDto.getRespondentMobileNumber() );
                    surveyorRespondentDetails.setFirstName( surveyLinkDto.getFirstName() );
                    surveyorRespondentDetails.setLastName( surveyLinkDto.getLastName() );
                    surveyorRespondentDetails.setState( state );
                    surveyorRespondentDetails.setSurveyTypeId(surveyLinkDto.getSurveyType());
                    
                    
                    link = linkGenerator( surveyLinkDto.getUserId(), surveyLinkDto.getRespondentMobileNumber(),
                                          redisMgrImpl.getRespondentList( "en" )[Integer
                                                  .parseInt( surveyLinkDto.getRespondentType() )],
                                          state.getStateId() ,surveyLinkDto.getSurveyType());
                    if ( StringUtils.isNotEmpty( link ) )
                    {
                        LOGGER.info( "gnerateSurveyLink :: Link:- " +link);
    
                        surveyorRespondentDetails.setUrl( link );
                        surveyorRespondentDetails.setIsSurveySubmitted( (byte) 0 );
                        surveyorRespondentDetails.setEmail_id( surveyLinkDto.getEmailId() );
                        surveyorRespondentDetails.setStatus( (byte) IAppConstants.active );
                        surveyorRespondentDetails.setCreatedBy( String.valueOf( surveyLinkDto.getUserId() ) );
                        surveyorRespondentDetails.setCreatedTime( new Date() );
                        String shortLink = getlinkShortner( link );
                        LOGGER.info( "generateSurveyLink :: shortLink :- "+ shortLink);
                        if ( surveyLinkDto.getIsfillsurvey() == IAppConstants.FILL_A_SURVEY )
                            surveyorRespondentDetails
                                    .setSurveyorMobileNumber( surveyLinkDto.getSurveyorMobileNumber() );
                        serviceCode = redisMgrImpl.putSurveyLink( surveyorRespondentDetails );
                        if ( StringUtils.isValidObj( serviceCode ) )
                        {
                            errorDtos = setErrorData( serviceCode, errorDtos, translator );
                        }
                        else
                            errorDtos = sendLinkGeneratedSMS( shortLink, surveyLinkDto, language, translator );
                    }
                    else
                    {
                        serviceCode = APPServiceCode.NP_SERVICE_995;
                        errorDtos = setErrorData( serviceCode, errorDtos, translator );
                        LOGGER.error( "generateSurveyLink  :: " + serviceCode );
                    }
                }
                else
                {
                    if ( StringUtils.compareRegularExp( surveyLinkDto.getUserId(),
                                                        dbSurveyorRespondentDetails.getUserId() ) )
                    {
                        if ( dbSurveyorRespondentDetails.getIsSurveySubmitted() != IAppConstants.SURVEY_SUBMITTED )
                        {
                            String shortLink = getlinkShortner( dbSurveyorRespondentDetails.getUrl() );
                            errorDtos = sendLinkGeneratedSMS( shortLink, surveyLinkDto, language, translator );
                            LOGGER.info( "generateSurveyLink :: " + surveyLinkDto.toString());                           
                        }
                        else
                        {
                            serviceCode = APPServiceCode.NP_SERVICE_003;
                            errorDtos = setErrorData( serviceCode, errorDtos, translator );
                            System.out.println( "#@123@#saveSurveyData :- Survey is already submitted" );
                            LOGGER.info( "generateSurveyLink :: response :: " + serviceCode + " :: " + serviceCode.getStatusDesc());
                        }
                    }
                    else
                    {
                        serviceCode = APPServiceCode.NP_SERVICE_021;
                        errorDtos = setErrorData( serviceCode, errorDtos, translator );
                        //                        System.out.println( "saveSurveyData :- Survey link is already generated for this user" );
                        LOGGER.error( "generateSurveyLink :: response :: " + serviceCode + " :: "+ serviceCode.getStatusDesc() );
                    }
                }
            }
            else
            {
                serviceCode = APPServiceCode.NP_SERVICE_012;
                errorDtos = setErrorData( serviceCode, errorDtos, translator );
                LOGGER.error( "generateSurveyLink :: response :: " + serviceCode );
            }
        }
        else
        {
            errorDtos = setErrorData( serviceCode, errorDtos, translator );
            LOGGER.info( "generateSurveyLink :: response :: " + serviceCode.getStatusCode() + " :: " + serviceCode.getStatusDesc() );
        }
        if ( StringUtils.isValidObj( errorDtos ) && errorDtos.size() > 0 )
        {
            responseModel.setErrors( errorDtos );
            System.out.println( "#@123@#Has error in generateSurveyLink: " + errorDtos.toString() );
        }
        else
        {
            responseDto.setStatusCode( APPServiceCode.NP_SERVICE_001.getStatusCode() );
            responseDto.setStatusDesc( translator.toLocale( APPServiceCode.NP_SERVICE_001.getStatusCode() ) );
            responseModel.setData( responseDto );
            LOGGER.info( "generateSurveyLink :: response :: " + responseDto.getStatusCode() + " :: " + responseDto.getStatusDesc() );
        }
        LOGGER.info("generateSurveyLink :: response :: "+responseModel);
        return responseModel;
    }

    public ResponseModel<ResponseDto> saveSurveyData( SurveyDataDto inDto, Translator translator, String language )
        throws Exception
    {
    	LOGGER.info( "saveSurveyData :: request" );

        ResponseModel<ResponseDto> responseModel = new ResponseModel<>();
        SurveyorRespondentDetails surveyorRespondentDetails = new SurveyorRespondentDetails();
        List<ErrorModel> errorDtos = new ArrayList<>();
        List<SurveyData> surveyDatas = new ArrayList<>();
        APPServiceCode serviceCode = null;
        //        System.out.println( "------------------------------- Survey Mgr Impl ---------------------------" );
        try
        {
            if(StringUtils.isEmpty( language ) ) {
                language=IAppConstants.EN_LANGUAGE;
            }
            System.out.println( "#@123@#Language: "+language );
            serviceCode = validation.saveSurveyDataValidation( inDto, language );
            if ( !StringUtils.isValidObj( serviceCode ) )
            {
                //                MstrState state = redisMgrImpl.getState( inDto.getStateId() );
                MstrState state = mstrStateService.findById( inDto.getStateId() );
                if ( StringUtils.isValidObj( state ) )
                {
                    /*surveyorRespondentDetails = surveyorRespondentDetailsService
                            .findByUserIdAndRespondentTypeAndRespondentMobileNumber( inDto.getUserId(),
                                                                                     inDto.getRespondentType(),
                                                                                     inDto.getMobileNo() );
                    */
                    surveyorRespondentDetails = surveyorRespondentDetailsService
                            .findByUseridAndRespondentMobile( inDto.getUserId(), inDto.getMobileNo() );
                    if ( StringUtils.isValidObj( surveyorRespondentDetails ) )
                    {
                        if ( StringUtils.isValidObj( surveyorRespondentDetails.getIsSurveySubmitted() )
                                && surveyorRespondentDetails.getIsSurveySubmitted() == IAppConstants.SURVEY_SUBMITTED )
                        {
                            List<SurveyData> savedDataList = surveyDataService
                                    .findByUserIdAndQuestionnaireIdAndMobile( inDto.getUserId(),
                                                                              inDto.getQuestionnaireId(),
                                                                              inDto.getMobileNo() );
                            if ( StringUtils.isValidCollection( savedDataList )
                                    && savedDataList.size() > IAppConstants.ZERO )
                            {
                                serviceCode = APPServiceCode.NP_SERVICE_003;
                                errorDtos = setErrorData( serviceCode, errorDtos, translator );
                                LOGGER.error( "saveSurveyData "       + " :: "+ serviceCode+"saveSurveyData :- Survey id already submitted for the user" );
                            }
                            else
                            {
                                if ( inDto.getQuestionAnswerDto().size() > 0 )
                                {
                                    for ( QuestionAnswerDto dto : inDto.getQuestionAnswerDto() )
                                    {
                                        SurveyData surveyData = new SurveyData();
                                        surveyData.setUserId( inDto.getUserId() );
                                        surveyData.setQuestionnaireId( inDto.getQuestionnaireId() );
                                        surveyData.setQuestionId( dto.getQuestionId() );
                                        surveyData.setAnswer( dto.getAnswer() );
                                        surveyData.setLatitude( inDto.getLatitude() );
                                        surveyData.setLongitude( inDto.getLongitude() );
                                        surveyData.setState( state );
                                        surveyData.setMobile( inDto.getMobileNo() );
                                        surveyData.setCreatedBy( String.valueOf( inDto.getUserId() ) );
                                        surveyData.setCreatedTime( new Date() );
                                        surveyData.setLanguage( language );
                                        surveyDatas.add( surveyData );
                                    }
                                    serviceCode = redisMgrImpl.putSurveyData( surveyDatas );
                                }
                                else
                                {
                                    serviceCode = APPServiceCode.NP_SERVICE_001;
                                }
                                if ( !serviceCode.getStatusCode()
                                        .equalsIgnoreCase( APPServiceCode.NP_SERVICE_001.getStatusCode() ) )
                                    errorDtos = setErrorData( serviceCode, errorDtos, translator );
                            }
                        }
                        else
                        {
                            if ( inDto.getQuestionAnswerDto().size() > 0 )
                            {
                                for ( QuestionAnswerDto dto : inDto.getQuestionAnswerDto() )
                                {
                                    SurveyData surveyData = new SurveyData();
                                    surveyData.setUserId( inDto.getUserId() );
                                    surveyData.setQuestionnaireId( inDto.getQuestionnaireId() );
                                    surveyData.setQuestionId( dto.getQuestionId() );
                                    surveyData.setAnswer( dto.getAnswer() );
                                    surveyData.setLatitude( inDto.getLatitude() );
                                    surveyData.setLongitude( inDto.getLongitude() );
                                    surveyData.setState( state );
                                    surveyData.setMobile( inDto.getMobileNo() );
                                    surveyData.setCreatedBy( String.valueOf( inDto.getUserId() ) );
                                    surveyData.setCreatedTime( new Date() );
                                    surveyData.setLanguage( language );
                                    surveyDatas.add( surveyData );
                                }
                                serviceCode = redisMgrImpl.putSurveyData( surveyDatas );
                            }
                            else
                            {
                                serviceCode = APPServiceCode.NP_SERVICE_001;
                                LOGGER.info( "saveSurveyData "+ " :: "+ serviceCode +" :: qustion ans dto null ");
                            }
                            if ( !serviceCode.getStatusCode()
                                    .equalsIgnoreCase( APPServiceCode.NP_SERVICE_001.getStatusCode() ) )
                                errorDtos = setErrorData( serviceCode, errorDtos, translator );
                            else
                            {
                                surveyorRespondentDetails.setIsSurveySubmitted( IAppConstants.SURVEY_SUBMITTED );
                                surveyorRespondentDetails.setModifiedBy( String.valueOf( inDto.getUserId() ) );
                                surveyorRespondentDetails.setModifiedTime( new Date() );
                                surveyorRespondentDetailsService
                                        .saveSurveyorRespondentDetails( surveyorRespondentDetails );
                                // LOGGER.info( "surveyorRespondentDetails: " + surveyorRespondentDetails );
                                // System.out.println( "surveyorRespondentDetails: " + surveyorRespondentDetails
                                // );
                                // surveyorRespondentDetailsService
                                // .updateSurveyorRespondentDetails( IAppConstants.SURVEY_SUBMITTED,
                                // String.valueOf( inDto.getUserId() ),
                                // new Date(), surveyorRespondentDetails
                                // .getRespdtDetailsId() );
                            }
                        }
                    }
                    else
                    {
                        serviceCode = APPServiceCode.NP_SERVICE_004;
                        errorDtos = setErrorData( serviceCode, errorDtos, translator );
                        LOGGER.error( "saveSurveyData :: response :: "+serviceCode);
                    }
                }
                else
                {
                    serviceCode = APPServiceCode.NP_SERVICE_012;
                    errorDtos = setErrorData( serviceCode, errorDtos, translator );
                    LOGGER.info( "saveSurveyData :: response ::  "+serviceCode);
                }
            }
            else
            {
                errorDtos = setErrorData( serviceCode, errorDtos, translator );
                LOGGER.info( "saveSurveyData :: response :: "+serviceCode);
            }
            if ( StringUtils.isValidCollection( errorDtos ) )
                responseModel.setErrors( errorDtos );
            else
            {
                ResponseDto resDto = new ResponseDto( APPServiceCode.NP_SERVICE_001
                        .getStatusCode(), translator.toLocale( APPServiceCode.NP_SERVICE_001.getStatusCode() ) );
                responseModel.setData( resDto );
                LOGGER.info( "saveSurveyData :: response :: success :: "
                        +" :: "+ serviceCode );
            }
            LOGGER.info( "saveSurveyData ::  Response : Status Code" + serviceCode.getStatusCode() + "Status Desc : "
                    + serviceCode.getStatusDesc() + " Response: " + responseModel );
            //System.out.println( "#@123@#saveSurveyData :- Response : Status Code" + serviceCode.getStatusCode()
            //        + "Status Desc : " + serviceCode.getStatusDesc() + " Response: " + responseModel );
        }
        catch ( Exception ex )
        {
            LOGGER.error( "saveSurveyData :: exception :: "
                     + " :: " + ex );
            ex.getStackTrace();
            errorDtos = setErrorData( APPServiceCode.NP_SERVICE_999, errorDtos, translator );
            responseModel.setErrors( errorDtos );
            return responseModel;
        }
        return responseModel;
    }

    public String linkGenerator( String userID, String phoneNumber, String respondentType, Integer stateId, Integer surveyType )
        throws Exception
    {
    	LOGGER.info( "linkGenerator :: " );
//        System.out.println( userID + "  " + phoneNumber + " " + respondentType + "  " + stateId + "  " + surveyType );
        //        String BASEURL = "https://test.ncf.inroad.in/survey/#/?value="; 
        // "http://89.233.105.5:8180/ncert-service/survey";
        // String BASEURL = WEB_URL_GENERATION;
        if ( !StringUtils.isEmpty( phoneNumber ) && !StringUtils.isEmpty( userID )
                && !StringUtils.isEmpty( respondentType ) && stateId > 0 )
                //&& surveyType > 0)
        {
            String encodedURL = WEB_URL_GENERATION + AppEncryptionUtil.encrypt( "userId=" + userID + "&mobileNo="
                    + phoneNumber + "&respondentType=" + respondentType + "&stateId=" + stateId );
//            + "&surveyType" + surveyType
            return encodedURL;
        }
        return null;
    }

    public List<ErrorModel> sendLinkGeneratedMail( String shortUrl,
                                                   SurveyorRespondentDetails surveyorRespondentDetails,
                                                   String language,
                                                   Translator translator )
    {
        List<ErrorModel> errorModels = null;
        SendMailRequestDto sendMailDto = new SendMailRequestDto();
        sendMailDto.setUserId( surveyorRespondentDetails.getUserId() );
        sendMailDto.setStateId( surveyorRespondentDetails.getState().getStateId() );
        sendMailDto.setSubject( "NCERT | TESTING SURVEY MAIL" );
        List<String> mail = new ArrayList<>();
        mail.add( surveyorRespondentDetails.getEmail_id() );
        sendMailDto.setEmailToList( mail );
        sendMailDto.setEmailTemplates( "<p>" + shortUrl + " is your surveyURL </p>" );
        sendMailDto.setEmailTemplateHtmlType( true );
        errorModels = sendMail( sendMailDto, language, translator );
        return errorModels;
    }

    public List<ErrorModel> sendLinkGeneratedSMS( String shortUrl,
                                                  SurveyLinkDto surveyLinkDto,
                                                  String language,
                                                  Translator translator )
    {
    	LOGGER.info( "sendLinkGeneratedSMS"); 
        APPServiceCode serviceCode = null;
        ArrayList<ErrorModel> errorModels = null;
        SMSRequestDto smsRequestDto = new SMSRequestDto();
        ArrayList<String> mobileNo = new ArrayList<>();
        smsRequestDto.setUserId( surveyLinkDto.getUserId() );
        smsRequestDto.setStateId( surveyLinkDto.getStateId() );
        if ( surveyLinkDto.getIsfillsurvey() == IAppConstants.FILL_A_SURVEY )
        {
            smsRequestDto.setTemplateId( SMSTEMPLATEID_LINKGENERATION_SURVEYOR );
            mobileNo.add( surveyLinkDto.getSurveyorMobileNumber() );
            smsRequestDto.setMobileNo( mobileNo );
            smsRequestDto.setSmsBody( SMSTEMPLATE_LINKGENERATION_SURVEYOR
                    .replace( "#respondentPhoneNumber#", AppEncryptionUtil
                            .decrypt( surveyLinkDto.getRespondentMobileNumber() ) ).replace( "#URL#", shortUrl ) );
        }
        else
        {
            smsRequestDto.setTemplateId( SMSTEMPLATEID_LINKGENERATION_RESPONDENT );
            mobileNo.add( surveyLinkDto.getRespondentMobileNumber() );
            smsRequestDto.setMobileNo( mobileNo );
            smsRequestDto.setSmsBody( SMSTEMPLATE_LINKGENERATION_RESPONDENT.replace( "#URL#", shortUrl ) );
        }
    	LOGGER.info( "sendLinkGeneratedSMS :: "+ "smsRequestDto " + smsRequestDto );
        System.out.println( "#@13@# smsRequestDto " + smsRequestDto );
        serviceCode = sendSms( smsRequestDto, language );
        
        LOGGER.info( "sendLinkGeneratedSMS :: "+ "Service Code recived from SMS: " + serviceCode );
        System.out.println( "#@13@# Service Code recived from SMS: " + serviceCode );
        if ( StringUtils.isValidObj( serviceCode ) )
        {
            errorModels = new ArrayList<>();
            ErrorModel errorModel = new ErrorModel();
            errorModel.setCode( serviceCode.getStatusCode() );
            errorModel.setMessage( translator.toLocale( serviceCode.getStatusCode() ) );
            errorModels.add( errorModel );
        }
        
        return errorModels;
    }

    public ResponseModel<SendOTPResponseModel> otpRequest( @Valid SendOTPRequestDto sendOTPRequestDto,
                                                           Translator translator,
                                                           String language )
        throws Exception
    {
    	LOGGER.info( "otpRequest :: request " + " :: " + sendOTPRequestDto.toString() );
        System.out.println( "#@123@# ------------------------------- otpRequest ---------------------------" );
        APPServiceCode serviceCode = null;
        ErrorModel errorModel = null;
        // details validation
        ResponseModel<SendOTPResponseModel> response = new ResponseModel<SendOTPResponseModel>();
        serviceCode = validation.otpRequest( sendOTPRequestDto );
        if ( !StringUtils.isValidObj( serviceCode ) )
        {
            //            MstrState state = redisMgrImpl.getState( sendOTPRequestDto.getStateId() );
            MstrState state = mstrStateService.findById( sendOTPRequestDto.getStateId() );
            LOGGER.info( "otpRequest :: "+"------------------------------- stateId ---------------------------" + state );
            if ( StringUtils.isValidObj( state ) )
            {
                if ( StringUtils.isValidObj( sendOTPRequestDto ) )
                {
                    SurveyorRespondentDetails userData = surveyorRespondentDetailsService
                            .findByRespondentMobile( sendOTPRequestDto.getMobileNo() );
                    System.out.println( "#@123@# UserData: " + userData );
                    if ( StringUtils.isValidObj( userData )
                            && userData.getState().getStateId().equals( sendOTPRequestDto.getStateId() )
                            && userData.getUserId().equals( sendOTPRequestDto.getUserId() ) )
                    {
                        if ( userData.getIsSurveySubmitted() == (byte) 1 )
                        {
                            serviceCode = APPServiceCode.NP_SERVICE_003;
                        }
                        else
                        {
                            System.out.println( "#@123@# sendOTPRequestDto: " + sendOTPRequestDto.toString() );
                            OtpServiceResponseModel<SendOTPResponseDto> res = fetchOTP( sendOTPRequestDto, language );
                            if ( StringUtils.isValidObj( res ) )
                            {
                                System.out.println( "#@123@#Response from fetch OTP:" + res );
                                if ( StringUtils.isValidObj( res.getErrors() ) && res.getErrors().size() > 0 )
                                {
                                    List<ErrorModel> errors = new ArrayList<ErrorModel>();
                                    for ( int i = 0; i < res.getErrors().size(); i++ )
                                    {
                                        ErrorModel error = new ErrorModel();
                                        error.setCode( res.getErrors().get( i ).getStatusCode() );
                                        error.setMessage( res.getErrors().get( i ).getStatusDescription() );
                                        errors.add( error );
                                    }
                                    response.setErrors( errors );
                                    return response;
                                }
                                else
                                {
                                    ObjectMapper mapper = new ObjectMapper();
                                    SendOTPResponseDto sendOTPResponseDto = mapper
                                            .convertValue( res.getData(), SendOTPResponseDto.class );
                                    SendOTPResponseModel sendOTPResponseModel = new SendOTPResponseModel();
//                                    sendOTPResponseModel.setTsnId( sendOTPResponseDto.getTsnId() );
//                                    response.setData( sendOTPResponseModel );
                                    SMSRequestDto smsRequestDto = new SMSRequestDto();
                                    ArrayList<String> mobileNo = new ArrayList<>();
                                    mobileNo.add( sendOTPRequestDto.getMobileNo() );
                                    smsRequestDto.setMobileNo( mobileNo );
//                                    smsRequestDto.setSmsBody( "Your one-time password is " + sendOTPResponseDto.getOtp()
//                                            + ". Please use this one-time password(OTP) for NPC Tech Platform. Thank you, Team NPC" );
                                    smsRequestDto.setSmsBody( SMSTEMPLATE_SENDOTP.replace( "#otp#", sendOTPResponseDto.getOtp()+"" ) );
                                    smsRequestDto.setStateId( sendOTPRequestDto.getStateId() );
                                    smsRequestDto.setTemplateId( SMSTEMPLATEID_SENDOTP );
                                    smsRequestDto.setUserId( sendOTPRequestDto.getUserId() );
                                    System.out.println( "sendMailDto " + smsRequestDto );
                                    serviceCode = sendSms( smsRequestDto, language );
                                    System.out.println( "Service Code recived from SMS: " + serviceCode );
                                    if(!StringUtils.isValidObj(serviceCode)) {
                                        sendOTPResponseModel.setTsnId( sendOTPResponseDto.getTsnId() );
                                        response.setData( sendOTPResponseModel );
                                        System.out.println( "Response: " + response );
                                    }
                                }
                            }
                            else
                            {
                                serviceCode = APPServiceCode.NP_SERVICE_999;
                                LOGGER.error( "otpRequest :: error :: " +serviceCode);
                            }
                        }
                    }
                    else
                    {
                        serviceCode = APPServiceCode.NP_SERVICE_004;
                        LOGGER.info( "otpRequest :: error :: "+ serviceCode );
                    }
                }
                if ( StringUtils.isValidObj( serviceCode ) )
                {
                    List<ErrorModel> errors = new ArrayList<ErrorModel>();
                    ErrorModel error = new ErrorModel();
                    error.setCode( serviceCode.getStatusCode() );
                    error.setMessage( translator.toLocale( serviceCode.getStatusCode() ) );
                    errors.add( error );
                    response.setErrors( errors );
                    LOGGER.info( "otpRequest ::" + error.getMessage() );
                }
                System.out.println( "#@123@# Final Response: " + response );
                LOGGER.info( "otpRequest :: response :: "+ response);
                return response;
            }
            else
            {
                List<ErrorModel> errors = new ArrayList<ErrorModel>();
                ErrorModel error = new ErrorModel();
                error.setCode( APPServiceCode.NP_SERVICE_012.getStatusCode() );
                error.setMessage( translator.toLocale( error.getCode() ) );
                errors.add( error );
                LOGGER.info( "otpRequest :: response  " + error.getCode() + " :: "+ error.getMessage() );
                response.setErrors( errors );
                return response;
            }
        }
        else
        {
            List<ErrorModel> errors = new ArrayList<ErrorModel>();
            ErrorModel error = new ErrorModel();
            error.setCode( serviceCode.getStatusCode() );
            error.setMessage( translator.toLocale( serviceCode.getStatusCode() ) );
            errors.add( error );
            
            response.setErrors( errors );
            LOGGER.info( "otpRequest :: response :: " + serviceCode.getStatusCode() + " :: " + response.toString() );
            return response;
        }
    }

    private List<ErrorModel> sendMail( SendMailRequestDto sendMailDto, String language, Translator translator )
    {
        List<ErrorModel> errorModels = new ArrayList<>();
        NotificationServiceResponseModel<NotificationResponseDto> response = new NotificationServiceResponseModel<>();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate( requestFactory );
        ObjectMapper mapper = new ObjectMapper();
        // String url =
        // "http://89.233.105.5:8182/ncert-service/notificationAppSendMail/save/sendEmail";
        // String url =
        // NOTIFICATION_SEND_MAIL_URL;//"http://test.ncf.inroad.in:18181/custom/notificationAppSendSms/save/sendEmail";
        String[] uris = NOTIFICATION_SEND_MAIL_URL.split( "/" );
        long timestamp = System.currentTimeMillis();
        HttpHeaders headers = getHeader( timestamp, language );
        try
        {
            headers.add( "signature",
                         SignatureUtil.getSignature( uris[4], uris[5], uris[6], String.valueOf( timestamp ),
                                                     mapper.writeValueAsString( sendMailDto ), null ) );
            HttpEntity<SendMailRequestDto> request = new HttpEntity<SendMailRequestDto>( sendMailDto, headers );
            response = restTemplate.postForObject( NOTIFICATION_SEND_MAIL_URL, request,
                                                   NotificationServiceResponseModel.class );
        }
        catch ( Exception e )
        {
            System.out.println( "Exception in SendNotificationMail " + e.getMessage() );
            errorModels = setErrorData( APPServiceCode.NP_SERVICE_999, errorModels, translator );
            e.printStackTrace();
            return errorModels;
        }
        if ( StringUtils.isValidObj( response ) && StringUtils.isValidCollection( response.getErrors() ) )
        {
            errorModels.addAll( response.getErrors() );
        }
        return errorModels;
    }

    private APPServiceCode sendSms( SMSRequestDto sendSmsDto, String language )
    {
    	LOGGER.info( "sendSMS :: " +sendSmsDto.toString());
        APPServiceCode serviceCode = null;
        NotificationServiceResponseModel<NotificationResponseDto> response = new NotificationServiceResponseModel<>();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate( requestFactory );
        ObjectMapper mapper = new ObjectMapper();
        // String url =
        // "http://89.233.105.5:8182/ncert-service/notificationAppSendSms/save/sendSms";
        //        String url = "http://test.ncf.inroad.in:18181/custom/notificationAppSendSms/save/sendSms";
        // "http://192.168.3.240:8182/api/v1/sendMail";
        String[] uris = NOTIFICATION_SEND_SMS_URL.split( "/" );
        long timestamp = System.currentTimeMillis();
        HttpHeaders headers = getHeader( timestamp, language );
        try
        {
            headers.add( "signature",
                         SignatureUtil.getSignature( uris[4], uris[5], uris[6], String.valueOf( timestamp ),
                                                     mapper.writeValueAsString( sendSmsDto ), null ) );
            HttpEntity<SMSRequestDto> request = new HttpEntity<SMSRequestDto>( sendSmsDto, headers );
            response = restTemplate.postForObject( NOTIFICATION_SEND_SMS_URL, request,
                                                   NotificationServiceResponseModel.class );
            LOGGER.info( "sendSMS  :: " + " :: sendSMS_response:-" + response );
            if ( StringUtils.isValidObj( response.getData() ) )
            {
                NotificationResponseDto notificationResponseDto = mapper.convertValue( response.getData(),
                                                                                       NotificationResponseDto.class );
                if ( !StringUtils.equals( notificationResponseDto.getCode(), "NP_NOTIFICATION_001" ) )
                    serviceCode = APPServiceCode.NP_SERVICE_994;
                LOGGER.info( "sendSMS  :: "  + serviceCode );
            }
            else
            {
                serviceCode = APPServiceCode.NP_SERVICE_994;
                LOGGER.error( "sendSMS :: error :: " + " :: " + serviceCode );
            }
        }
        catch ( Exception e )
        {
            System.out.println( "Exception in SendOTPResponseDto " + e.getMessage() );
            LOGGER.error( "sendSMS :: Exception :: " + " :: " + e );
            e.printStackTrace();
            return APPServiceCode.NP_SERVICE_999;
        }
        return serviceCode;
    }

    private OtpServiceResponseModel<SendOTPResponseDto> fetchOTP( @Valid SendOTPRequestDto sendOTPRequestDto,
                                                                  String language )
    {
    	 LOGGER.info( "fetchOTP :: " + sendOTPRequestDto.toString() );
        OtpServiceResponseModel<SendOTPResponseDto> response = new OtpServiceResponseModel<SendOTPResponseDto>();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate( requestFactory );
        ObjectMapper mapper = new ObjectMapper();
        // String url = "http://89.233.105.5:8181/otp/getOTP/save/OTPServiceImpl";
        // String url =
        // "http://test.ncf.inroad.in:18181/custom/getOTP/save/OTPServiceImpl";
        String[] uris = OTP_GETOTP_URL.split( "/" );
        long timestamp = System.currentTimeMillis();
        HttpHeaders headers = getHeader( timestamp, language );
        try
        {
            headers.add( "signature",
                         SignatureUtil.getSignature( uris[4], uris[5], uris[6], String.valueOf( timestamp ),
                                                     mapper.writeValueAsString( sendOTPRequestDto ), null ) );
            HttpEntity<SendOTPRequestDto> request = new HttpEntity<SendOTPRequestDto>( sendOTPRequestDto, headers );
            response = restTemplate.postForObject( OTP_GETOTP_URL, request, OtpServiceResponseModel.class );
        }
        catch ( Exception e )
        {
            LOGGER.info( "fetchOTP :: exception :: " + e );
            System.out.println( "#@123@# Exception in SendOTPResponseDto " + e.getMessage() );
            e.printStackTrace();
            OtpServicesErrorModel error = new OtpServicesErrorModel();
            error.setStatusCode( APPServiceCode.NP_SERVICE_999.getStatusCode() );
            error.setStatusDescription( APPServiceCode.NP_SERVICE_999.getStatusDesc() );
            List<OtpServicesErrorModel> errors = new ArrayList<OtpServicesErrorModel>();
            errors.add( error );
            response.setErrors( errors );
        }
        return response;
    }

    public ResponseModel<SendOTPResponseDto> validateOTP( @Valid ValidateOTPRequestModel validateOTPRequestModel,
                                                          Translator translator,
                                                          String language )
        throws Exception
    {
    	LOGGER.info( "validateOTP :: request"+" :: "+validateOTPRequestModel.toString());
        System.out.println( "#@123@#------------------------------- validateOTP ---------------------------" );
        List<ErrorModel> errors = new ArrayList<ErrorModel>();
        ResponseModel<SendOTPResponseDto> response = new ResponseModel<>();
        APPServiceCode serviceCode = validation.validateOtp( validateOTPRequestModel );
        ValidateOTPRequestDto validateOTPRequestdto = null;
        if ( !StringUtils.isValidObj( serviceCode ) )
        {
            LOGGER.info( "validateOTP :: request");
            validateOTPRequestdto = new ValidateOTPRequestDto();
            validateOTPRequestdto.setOtp( validateOTPRequestModel.getOtp() );
            validateOTPRequestdto.setStateId( validateOTPRequestModel.getStateId() );
            validateOTPRequestdto.setTsnId( validateOTPRequestModel.getTsnId() );
            validateOTPRequestdto.setUserId( validateOTPRequestModel.getUserId() );
            OtpServiceResponseModel<SendOTPResponseDto> res = callValidateOtp( validateOTPRequestdto, language );
            if ( StringUtils.isValidObj( res ) )
            {
                //            System.out.println( "Response from fetch validate OTP:" + res );
                if ( StringUtils.isValidCollection( res.getErrors() ) )
                {
                    for ( OtpServicesErrorModel errorModel : res.getErrors() )
                    {
                        ErrorModel error = new ErrorModel();
                        error.setCode( errorModel.getStatusCode() );
                        error.setMessage( errorModel.getStatusDescription() );
                        errors.add( error );
                    }
                }
                else
                {
                    ObjectMapper mapper = new ObjectMapper();
                    SendOTPResponseDto sendOTPResponseDto = mapper.convertValue( res.getData(),
                                                                                 SendOTPResponseDto.class );
                    sendOTPResponseDto.setQuestionair( redisMgrImpl
                            .getQuestionnaireList( validateOTPRequestModel.getRespondentType(), language ) );
                    response.setData( sendOTPResponseDto );
                    LOGGER.info( "validateOTP :: response :: " + response  );
                }
            }
            else
            {
                errors = setErrorData( APPServiceCode.NP_SERVICE_999, errors, translator );
                LOGGER.error( "validateOTP :: response :: " + APPServiceCode.NP_SERVICE_999 );
            }
        }
        else
        {
            errors = setErrorData( serviceCode, errors, translator );
            LOGGER.info( "validateOTP :: response "+" :: "+ serviceCode  );
        }
        if ( StringUtils.isValidCollection( errors ) )
        {
            response.setErrors( errors );
        }
        LOGGER.info( "validateOTP :: response :: "+ response  );
        return response;
    }

    public OtpServiceResponseModel<SendOTPResponseDto> callValidateOtp( ValidateOTPRequestDto validateOTPRequestDto,
                                                                        String language )
    {
    	LOGGER.info( "survey_ncf :: validateOTP::SurveyMgrImpl::callValidateOtp :: response :: " + validateOTPRequestDto.toString()  );
        OtpServiceResponseModel<SendOTPResponseDto> response = new OtpServiceResponseModel<SendOTPResponseDto>();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate( requestFactory );
        ObjectMapper mapper = new ObjectMapper();
        // String url = "http://89.233.105.5:8181/otp/validateOTP/save/OTPServiceImpl";
        // String url =
        // "http://test.ncf.inroad.in:18181/custom/validateOTP/save/OTPServiceImpl";
        String[] uris = OTP_VALIDATEOTP_URL.split( "/" );
        long timestamp = System.currentTimeMillis();
        HttpHeaders headers = getHeader( timestamp, language );
        try
        {
            headers.add( "signature",
                         SignatureUtil.getSignature( uris[4], uris[5], uris[6], String.valueOf( timestamp ),
                                                     mapper.writeValueAsString( validateOTPRequestDto ), null ) );
            HttpEntity<ValidateOTPRequestDto> request = new HttpEntity<ValidateOTPRequestDto>( validateOTPRequestDto,
                                                                                               headers );
            response = restTemplate.postForObject( OTP_VALIDATEOTP_URL, request, OtpServiceResponseModel.class );
        }
        catch ( Exception e )
        {
            LOGGER.info( "survey_ncf :: validateOTP :: Exception :: " + e + " :: "
                    + IAppConstants.dtf.format( LocalDateTime.now() ) );
            e.printStackTrace();
            OtpServicesErrorModel error = new OtpServicesErrorModel();
            error.setStatusCode( APPServiceCode.NP_SERVICE_999.getStatusCode() );
            error.setStatusDescription( APPServiceCode.NP_SERVICE_999.getStatusCode() );
            List<OtpServicesErrorModel> errors = new ArrayList<OtpServicesErrorModel>();
            errors.add( error );
            response.setErrors( errors );
        }
        return response;
    }

    public String getlinkShortner( String longUrl )
    {
    	LOGGER.info( "getlinkShortner");
        String shortUrl = "";
        if ( StringUtils.isNotEmpty( longUrl ) )
        {
            String encodeUrl = encodeUrl( longUrl );
            redisMgrImpl.putUrl( encodeUrl, longUrl );
            // shortUrl = "http://89.233.105.5:8180/ncert-service/custom/" + encodeUrl;
            //            shortUrl = "test.ncf.inroad.in/a/" + encodeUrl;
            shortUrl = SHORT_URL_GENERATION + encodeUrl;
            // shortUrl="localhost:8185/"+encodeUrl;
            // shortUrl = "89.233.105.5:8180/a/" + encodeUrl;
        }
        return shortUrl;
    }

    public String encodeUrl( String url )
    {
        String encodedUrl = "";
        LocalDateTime time = LocalDateTime.now();
        encodedUrl = Hashing.murmur3_32().hashString( url.concat( time.toString() ), StandardCharsets.UTF_8 )
                .toString();
        return encodedUrl;
    }

    private HttpHeaders getHeader( long timestam, String language )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Accept", "application/json" );
        headers.add( "Accept-Language", language );
        headers.setContentType( MediaType.APPLICATION_JSON );
        headers.add( "timestamp", timestam + "" );
        return headers;
    }

    private List<ErrorModel> setErrorData( APPServiceCode serviceCode,
                                           List<ErrorModel> errorModels,
                                           Translator translator )
    {
        ErrorModel errorDto = new ErrorModel();
        errorDto.setCode( serviceCode.getStatusCode() );
        errorDto.setMessage( translator.toLocale( serviceCode.getStatusCode() ) );
        errorModels.add( errorDto );
        return errorModels;
    }

    public ResponseModel<SendOTPResponseModel> resendOtpRequest( @Valid ResendOTPRequestModel resendOTPRequestModel,
                                                                 Translator translator,
                                                                 String language )
        throws Exception
    {
    	 LOGGER.info( "resendOtpRequest :: request "+resendOTPRequestModel.toString());
        System.out
                .println( "#@123@#------------------------------- SurveyMdrlImpl : resendOtpRequest ---------------------------" );
        APPServiceCode serviceCode = null;
        ResponseModel<SendOTPResponseModel> response = new ResponseModel<SendOTPResponseModel>();
        serviceCode = validation.resendOtpValidate( resendOTPRequestModel );
        if ( !StringUtils.isValidObj( serviceCode ) )
        {
            //            MstrState state = redisMgrImpl.getState( resendOTPRequestModel.getStateId() );
            MstrState state = mstrStateService.findById( resendOTPRequestModel.getStateId() );
            LOGGER.info( "#@123@#------------------------------- stateId ---------------------------" + state );
            if ( StringUtils.isValidObj( state ) )
            {
                LOGGER.info( "resendOtpRequest :: request :: " + resendOTPRequestModel.toString() );
                if ( StringUtils.isValidObj( resendOTPRequestModel ) )
                {
                    OtpServiceResponseModel<SendOTPResponseDto> res = fetchResendOTP( resendOTPRequestModel, language );
                    if ( StringUtils.isValidObj( res ) )
                    {
                        System.out.println( "#@123#@Response from fetch OTP:" + res );
                        if ( StringUtils.isValidObj( res.getErrors() ) && res.getErrors().size() > 0 )
                        {
                            List<ErrorModel> errors = new ArrayList<ErrorModel>();
                            for ( int i = 0; i < res.getErrors().size(); i++ )
                            {
                                ErrorModel error = new ErrorModel();
                                error.setCode( res.getErrors().get( i ).getStatusCode() );
                                error.setMessage( res.getErrors().get( i ).getStatusDescription() );
                                errors.add( error );
                                LOGGER.info( "resendOtpRequest ::"+ " response :: error :: " + " :: " + error.getMessage() );
                            }
                            response.setErrors( errors );
                            return response;
                        }
                        else
                        {
                            ObjectMapper mapper = new ObjectMapper();
                            SendOTPResponseDto sendOTPResponseDto = mapper.convertValue( res.getData(),
                                                                                         SendOTPResponseDto.class );
                            // response.setData(sendOTPResponseDto);
                            SendOTPResponseModel sendOTPResponseModel = new SendOTPResponseModel();
                            sendOTPResponseModel.setTsnId( sendOTPResponseDto.getTsnId() );
                            response.setData( sendOTPResponseModel );
                            System.out.println( "#@123@#Response: " + response );
                            SMSRequestDto smsRequestDto = new SMSRequestDto();
                            ArrayList<String> mobileNo = new ArrayList<>();
                            mobileNo.add( resendOTPRequestModel.getMobileNo() );
                            smsRequestDto.setMobileNo( mobileNo );
//                            smsRequestDto.setSmsBody( "Your one-time password is " + sendOTPResponseDto.getOtp()
//                                    + ". Please use this one-time password(OTP) for NPC Tech Platform. Thank you, Team NPC" );
                            smsRequestDto.setSmsBody( SMSTEMPLATE_SENDOTP.replace( "#otp#", sendOTPResponseDto.getOtp()+"" ) );
                            smsRequestDto.setStateId( resendOTPRequestModel.getStateId() );
                            
                            smsRequestDto.setTemplateId( SMSTEMPLATEID_SENDOTP );
                            smsRequestDto.setUserId( resendOTPRequestModel.getUserId() );
                            System.out.println( "sendMailDto " + smsRequestDto );
                            serviceCode = sendSms( smsRequestDto, language );
                            System.out.println( "#@123@# Service Code recived from SMS: " + serviceCode );
                            LOGGER.info( "resendOtpRequest :: request :: success :: "+" :: " + APPServiceCode.NP_SERVICE_001 );
                        }
                    }
                    else
                    {
                        serviceCode = APPServiceCode.NP_SERVICE_999;
                        LOGGER.error( "resendOtpRequest :: response :: error :: "+ resendOTPRequestModel.toString() + " :: "
                                 + " :: " + serviceCode );
                    }
                }
                if ( StringUtils.isValidObj( serviceCode ) )
                {
                    List<ErrorModel> errors = new ArrayList<ErrorModel>();
                    ErrorModel error = new ErrorModel();
                    error.setCode( serviceCode.getStatusCode() );
                    error.setMessage( translator.toLocale( serviceCode.getStatusCode() ) );
                    errors.add( error );
                    response.setErrors( errors );
                    //                    LOGGER.info( "getOTPRequest :- Response : Status Code " + error.getCode() + " Status Desc : "
                    //                            + error.getMessage() );
                    //                    System.out.println( "getRespondentType :- Response : Status Code " + error.getCode()
                    //                            + " Status Desc : " + error.getMessage() );
                    LOGGER.info( "resendOtpRequest :: response :: error :: "+  " :: " + serviceCode );
                }
                System.out.println( "#@123@#Final Response: " + response );
                return response;
            }
            else
            {
                List<ErrorModel> errors = new ArrayList<ErrorModel>();
                ErrorModel error = new ErrorModel();
                error.setCode( APPServiceCode.NP_SERVICE_012.getStatusCode() );
                error.setMessage( translator.toLocale( APPServiceCode.NP_SERVICE_012.getStatusCode() ) );
                errors.add( error );
                // LOGGER.info("getRespondentType :- Response : Status Code" +
                // serviceCode.getStatusCode()
                // + "Status Desc : " + serviceCode.getStatusDesc() + "StateId invalid");
                LOGGER.info( "resendOtpRequest :: response :: error :: "+ " :: " + serviceCode );
                response.setErrors( errors );
                return response;
            }
        }
        //
        else
        {
            List<ErrorModel> errors = new ArrayList<ErrorModel>();
            ErrorModel error = new ErrorModel();
            error.setCode( serviceCode.getStatusCode() );
            error.setMessage( translator.toLocale( serviceCode.getStatusCode() ) );
            errors.add( error );
            //            LOGGER.info( "getRespondentType :- Response : Status Code" + serviceCode.getStatusCode() + "Status Desc : "
            //                    + serviceCode.getStatusDesc() );
            LOGGER.info( "resendOtpRequest :: response :: "+ " :: " + serviceCode );
            response.setErrors( errors );
            return response;
        }
    }

    private OtpServiceResponseModel<SendOTPResponseDto> fetchResendOTP( @Valid ResendOTPRequestModel resendOTPRequestModel,
                                                                        String language )
    {
    	LOGGER.info( "fetchResendOTP :: "+resendOTPRequestModel.toString());
        OtpServiceResponseModel<SendOTPResponseDto> response = new OtpServiceResponseModel<SendOTPResponseDto>();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate( requestFactory );
        ObjectMapper mapper = new ObjectMapper();
        // String url =
        // "http://test.ncf.inroad.in:18181/custom/resendOTP/save/OTPServiceImpl";
        String[] uris = OTP_RESEND_URL.split( "/" );
        long timestamp = System.currentTimeMillis();
        HttpHeaders headers = getHeader( timestamp, language );
        try
        {
            headers.add( "signature",
                         SignatureUtil.getSignature( uris[4], uris[5], uris[6], String.valueOf( timestamp ),
                                                     mapper.writeValueAsString( resendOTPRequestModel ), null ) );
            HttpEntity<ResendOTPRequestModel> request = new HttpEntity<ResendOTPRequestModel>( resendOTPRequestModel,
                                                                                               headers );
            response = restTemplate.postForObject( OTP_RESEND_URL, request, OtpServiceResponseModel.class );
            LOGGER.info( "fetchResendOTP :: response:- "+response);
        }
        catch ( Exception e )
        {
            System.out.println( "Exception in SendOTPResponseDto " + e.getMessage() );
            e.printStackTrace();
            LOGGER.error( "fetchResendOTP :: Exception :: " + e.getMessage());
            OtpServicesErrorModel error = new OtpServicesErrorModel();
            error.setStatusCode( APPServiceCode.NP_SERVICE_999.getStatusCode() );
            error.setStatusDescription( APPServiceCode.NP_SERVICE_999.getStatusDesc() );
            List<OtpServicesErrorModel> errors = new ArrayList<OtpServicesErrorModel>();
            errors.add( error );
            response.setErrors( errors );
        }
        return response;
    }
    
	// ################### Changes by KAMAL

	public ResponseModel<AppVersionResponseDto> appVersionData(AppVersionRequestDto appVersionRequestDto,
			Translator translator, String Language) throws Exception {
		LOGGER.info( "appVersionData :: request :: "+appVersionRequestDto.toString());
		ResponseModel<AppVersionResponseDto> responseModel = new ResponseModel<>();
		List<ErrorModel> errorDtos = new ArrayList<>();
		AppVersion appVersion = new AppVersion();
		AppVersionResponseDto appVersionResponseDto = new AppVersionResponseDto();
		APPServiceCode serviceCode = null;
		LOGGER.info("#@123@# ------------------------------- SurveyMgrImpl : AppData ---------------------------");

		try {
			serviceCode = validation.appVersion(appVersionRequestDto, Language); 
			if (!(StringUtils.isValidObj(serviceCode))) 
			{
				System.out.println(appVersionRequestDto.getUserOs().toString());
				appVersion = appVersionRepository.findByUserOs(appVersionRequestDto.getUserOs().toString().toLowerCase());
				System.out.println("appversion: "+appVersion);
				if(StringUtils.isValidObj(appVersion)) {
					int version = validationUtil.compareVersions(appVersionRequestDto.getInstalledVersion(), appVersion.getLastCompulsoryVerion());
					int updateVersion = validationUtil.compareVersions(appVersionRequestDto.getInstalledVersion(), appVersion.getLatestVersion());
					System.out.println("version :::: "+version+"  updateVersion ::: "+updateVersion);
				
					appVersionResponseDto.setLatestVersion(appVersion.getLatestVersion());
					if(version>=0) {
						appVersionResponseDto.setCompulsory(false);
						if(updateVersion>=0) {
							appVersionResponseDto.setUpdateAvailable(false);
						}else {
							appVersionResponseDto.setUpdateAvailable(true);
						}
					}else {
						appVersionResponseDto.setCompulsory(true);
						appVersionResponseDto.setUpdateAvailable(true);
					}
					
//					if(version>=0 || appVersion.getIsComplusory() == 0 || updateVersion ==1) {
//						appVersionResponseDto.setCompulsory(false);
//					}
//					else {
//						appVersionResponseDto.setCompulsory(true);
//					}
//					if(updateVersion>=0) {
//						appVersionResponseDto.setUpdateAvailable(false);
//					}
//					else {
//						appVersionResponseDto.setUpdateAvailable(true);
//					}
				}
				else
				{
					serviceCode = APPServiceCode.NP_SERVICE_997;
					errorDtos = setErrorData(serviceCode, errorDtos, translator);
				}
				
				responseModel.setData(appVersionResponseDto);
				System.out.println("RESPONSE ::::: "+responseModel);
			}

			else 
			{
				errorDtos = setErrorData(serviceCode, errorDtos, translator);
				LOGGER.info( "appVersionData :: "+"appVersionData :- Response : Status Code" + serviceCode.getStatusCode() + 
							"Status Desc : "+ serviceCode.getStatusDesc());
			}
		} catch (Exception ex) {
			LOGGER.error( "appVersionData :: "+"appVersionData :- Exception : "+ ex+" : Status Code : " + serviceCode.getStatusCode() + 
					" : Status Desc : "	+ serviceCode.getStatusDesc());
			ex.printStackTrace();
			serviceCode = APPServiceCode.NP_SERVICE_999;
			errorDtos = setErrorData(serviceCode, errorDtos, translator);
		}
		if (StringUtils.isValidCollection(errorDtos)) {
			responseModel.setErrors(errorDtos);
		}
		LOGGER.info("appVersionData :: response:- "+responseModel);
		return responseModel; // out
	}

}
