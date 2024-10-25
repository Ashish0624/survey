package com.ncert.survey.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ncert.survey.costants.APPServiceCode;
import com.ncert.survey.costants.IAppConstants;
import com.ncert.survey.dto.AppVersionRequestDto;
import com.ncert.survey.dto.ResendOTPRequestModel;
import com.ncert.survey.dto.SMSRequestDto;
import com.ncert.survey.dto.SendOTPRequestDto;
import com.ncert.survey.dto.SurveyDataDto;
import com.ncert.survey.dto.SurveyLinkDto;
import com.ncert.survey.dto.ValidateOTPRequestModel;

import io.netty.util.internal.StringUtil;

@Component
public class Validation
{
	 private static final Logger              LOGGER = Logger.getLogger( Validation.class);
	
    private APPServiceCode serviceCode;
    @Autowired
    ValidationUtil         validateUtil;
    public APPServiceCode generateSurveyLink( SurveyLinkDto surveyLinkDto, String language )
        throws Exception
    {
    	LOGGER.info( "survey_ncf :: genarateSurveyLink::Validation::generateSurveyLink ");
        if ( StringUtils.isValidObj( surveyLinkDto ) )
        {
            if ( !validateUtil.isMobileValid( surveyLinkDto.getRespondentMobileNumber() ) )
            {
                return serviceCode.NP_SERVICE_008;
            }
            
            if(StringUtils.isNumeric( surveyLinkDto.getRespondentType() )) {
                
                if ( !validateUtil.isValidRespondentType( Integer.parseInt( surveyLinkDto.getRespondentType() ) ) )
                {
                    return serviceCode.NP_SERVICE_015;
                }

            }else {
                return serviceCode.NP_SERVICE_015;                
            }

// This is temporary comment. We have to uncomment after complete live on production with version API.             

//            if(StringUtils.isValidObj( surveyLinkDto.getSurveyType() )) {
//            	if( !validateUtil.isValidSurveyType( surveyLinkDto.getSurveyType()))
//            	{
//            		return serviceCode.NP_SERVICE_022;
//            	}
//            }
            
            if ( validateUtil.isFillSurveyValid( surveyLinkDto.getIsfillsurvey() ) )
            {
                if ( surveyLinkDto.getIsfillsurvey() == IAppConstants.FILL_A_SURVEY
                        && !validateUtil.isMobileValid( surveyLinkDto.getSurveyorMobileNumber() ) )
                {
                    return serviceCode.NP_SERVICE_008;
                }
            }
            /*
             * We are not using these values at our end so stored it as it is
             *             
             *             if ( surveyLinkDto.getEmailId() != null )
            {
                if ( !validateUtil.isEmailValid( surveyLinkDto.getEmailId() ) )
                {
                    return serviceCode.NP_SERVICE_009;
                }
            }
            if ( surveyLinkDto.getFirstName() != null )
            {
                if ( !validateUtil.isNameValid( AppEncryptionUtil.decrypt( surveyLinkDto.getFirstName() ) ) )
                {
                    return serviceCode.NP_SERVICE_010;
                }
            }
            if ( surveyLinkDto.getLastName() != null )
            {
                if ( !validateUtil.isNameValid( AppEncryptionUtil.decrypt( surveyLinkDto.getLastName() ) ) )
                {
                    return serviceCode.NP_SERVICE_011;
                }
            }*/
            if ( StringUtils.isEmpty( surveyLinkDto.getUserId() ) )
            {
                return serviceCode.NP_SERVICE_013;
            }
            if ( !validateUtil.isRespondentValid( surveyLinkDto.getRespondentType() ) )
            {
                return serviceCode.NP_SERVICE_015;
            }
        }
        else
        {
            return serviceCode.NP_SERVICE_997;
        }
        return null;
    }
    // ######## sendOTP_Validations ################

    public APPServiceCode otpRequest( SendOTPRequestDto sendOTPRequestDto ) throws Exception
    {
        if ( StringUtils.isValidObj( sendOTPRequestDto ) )
        {
            if ( !validateUtil.isMobileValid( sendOTPRequestDto.getMobileNo() ) )
            {
                return serviceCode.NP_SERVICE_008;
            }
            if ( StringUtils.isEmpty( sendOTPRequestDto.getUserId() ) )
            {
                return serviceCode.NP_SERVICE_013;
            }
            
            if ( !validateUtil.isValidRespondentType( sendOTPRequestDto.getRespondentType(),
                                                      IAppConstants.ENGLISH_LANGUAGE ) )
            {
                return serviceCode.NP_SERVICE_015;
            }
        }
        else
        {
            return serviceCode.NP_SERVICE_997;
        }
        return null;
    }

    // ############# Resend OTP validations ##################
    public APPServiceCode resendOtpValidate( ResendOTPRequestModel resendOTPRequestModel )
    {
    	LOGGER.info( "resendOtpValidate ");
        if ( StringUtils.isValidObj( resendOTPRequestModel ) )
        {
            if ( !validateUtil.isMobileValid( resendOTPRequestModel.getMobileNo() ) )
            {
                return serviceCode.NP_SERVICE_008;
            }
            if ( StringUtils.isEmpty( resendOTPRequestModel.getUserId() ) )
            {
                return serviceCode.NP_SERVICE_013;
            }
            if ( StringUtils.isEmpty( resendOTPRequestModel.getTxnId() ) )
            {
                return serviceCode.NP_SERVICE_016;
            }
        }
        else
        {
            return serviceCode.NP_SERVICE_997;
        }
        return null;
    }
    // ############# save survey data validations ##################

    public APPServiceCode saveSurveyDataValidation( SurveyDataDto surveyDataDto, String language ) throws Exception
    {
        if ( StringUtils.isValidObj( surveyDataDto ) )
        {
            if ( StringUtils.isEmpty( surveyDataDto.getUserId() ) )
            {
                return serviceCode.NP_SERVICE_013;
            }
            if(!StringUtils.isValidObj( surveyDataDto.getQuestionnaireId() ) && surveyDataDto.getQuestionnaireId() > IAppConstants.ZERO ) {
                return serviceCode.NP_SERVICE_017;
            }
            if ( !validateUtil.isValidRespondentType( surveyDataDto.getRespondentType(),
                                                      language ) )
            {
                return serviceCode.NP_SERVICE_015;
            }
            if ( !validateUtil.isMobileValid( surveyDataDto.getMobileNo() ) )
            {
                return serviceCode.NP_SERVICE_008;
            }
            
            if (!StringUtils.isValidObj(surveyDataDto.getQuestionAnswerDto()) ) {
                return serviceCode.NP_SERVICE_997;
            }

            if(!StringUtils.isValidObj( surveyDataDto.getLatitude() ) && !StringUtils.isValidObj( surveyDataDto.getLongitude())) {
                return serviceCode.NP_SERVICE_997; 
            }
        }
        else
        {
            return serviceCode.NP_SERVICE_997;
        }
        return null;
    }
    // ############# validate Otp validations ##################

    public APPServiceCode validateOtp( ValidateOTPRequestModel validateOTPRequestModel )
        throws Exception
    {
    	LOGGER.info( "survey_ncf :: validateOTP::Validation::validateOtp "+validateOTPRequestModel);
        if ( StringUtils.isValidObj( validateOTPRequestModel ) )
        {
            if ( StringUtils.isEmpty( validateOTPRequestModel.getUserId() ) )
            {
                return serviceCode.NP_SERVICE_013;
            }
            if ( !validateUtil.isOtpValid( validateOTPRequestModel.getOtp() ) )
            {
                return serviceCode.NP_SERVICE_018;
            }
            if ( StringUtils.isEmpty( validateOTPRequestModel.getTsnId() ) )
            {
                return serviceCode.NP_SERVICE_016;
            }
            if ( !validateUtil.isValidRespondentType( validateOTPRequestModel.getRespondentType(),
                                                      IAppConstants.ENGLISH_LANGUAGE ) )
            {
                return serviceCode.NP_SERVICE_015;
            }
            //            if(!ValidationUtil.validateRespondentType(validateOTPRequestModel.
            //            getRespondentType(), "en")) {
            //            System.out.println("ValidateOtp Respondent type"); return
            //            serviceCode.NP_SERVICE_015; }
        }
        else
        {
            return serviceCode.NP_SERVICE_997;
        }
        return null;
    }

    public APPServiceCode sendSms( SMSRequestDto smsRequestDto )
    {
        if ( StringUtils.isValidObj( smsRequestDto ) )
        {
            for ( int i = 0; i < smsRequestDto.getMobileNo().size(); i++ )
            {
                String temp = smsRequestDto.getMobileNo().get( i );
                if ( !validateUtil.isMobileValid( temp ) )
                {
                    System.out.println( i );
                    return APPServiceCode.NP_SERVICE_008;
                }
            }
            if ( StringUtils.isEmpty( smsRequestDto.getUserId() ) )
            {
                return APPServiceCode.NP_SERVICE_013;
            }
            if ( StringUtils.isValidObj( smsRequestDto.getTemplateId() ) )
            {
                return APPServiceCode.NP_SERVICE_019;
            }
            if ( StringUtils.isEmpty( smsRequestDto.getSmsBody() ) )
            {
                return APPServiceCode.NP_SERVICE_020;
            }
        }
        else
        {
            return APPServiceCode.NP_SERVICE_997;
        }
        return null;
    }
    //// *****

    public APPServiceCode linkGeneratorValidation( String userID,
                                                   String phoneNumber,
                                                   String respondentType,
                                                   Integer stateId,
                                                   Integer surveyType) throws Exception
    {
        if ( StringUtils.isValidObj( userID ) && StringUtils.isValidObj( phoneNumber )
                && StringUtils.isValidObj( respondentType ) )
        {
            if ( !validateUtil.isMobileValid( phoneNumber ) )
            {
                return APPServiceCode.NP_SERVICE_008;
            }
            if ( StringUtils.isEmpty( userID ) )
            {
                return APPServiceCode.NP_SERVICE_013;
            }
            if ( !validateUtil.isRespondentValid( respondentType ) )
            {
                return APPServiceCode.NP_SERVICE_015;
            }
            if( !validateUtil.isValidSurveyType( surveyType )) {
            	return APPServiceCode.NP_SERVICE_022;
            }
            
        }
        else
        {
            return APPServiceCode.NP_SERVICE_997;
        }
        return null;
    }
    //### Changes by KAMAL
    public APPServiceCode appVersion(AppVersionRequestDto appVersionRequestDto, String Language) throws Exception
    {
    	if(StringUtils.isValidObj(appVersionRequestDto))
    	{
    		if(!validateUtil.isOsValid(appVersionRequestDto.getUserOs().toString())) {
        		return APPServiceCode.NP_SERVICE_023;
        	}
    		if(!validateUtil.isValidVersion(appVersionRequestDto.getInstalledVersion())) {
    			return APPServiceCode.NP_SERVICE_024;
    		}
    		
    	}
    	else
    	{
    		return APPServiceCode.NP_SERVICE_997;
    	}
    	
    	return null;
    }
    
    
    
    
    //      public static void main(String args[]) throws Exception {
    //          Validation valid = new Validation(); 
    //          ValidateOTPRequestModel validateOTPRequestModel = new ValidateOTPRequestModel(); 
    //          validateOTPRequestModel.setOtp(123654);
    //          validateOTPRequestModel.setRespondentType("3");
    //          validateOTPRequestModel.setTsnId("hakjghd");
    //          validateOTPRequestModel.setUserId("1234");
    //          System.out.println(valid.validateOtp(validateOTPRequestModel)); }
}
