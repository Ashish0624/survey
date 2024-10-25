package com.ncert.survey.service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncert.survey.dto.AppVersionResponseDto;
import com.ncert.survey.dto.DashboardDto;
import com.ncert.survey.model.SurveyorRespondentDetails;
import com.ncert.survey.repository.SurveyorRespondentDetailsRepository;
import com.ncert.survey.utils.StringUtils;

@Service
public class SurveyorRespondentDetailsService
{
	private static final Logger              LOGGER = Logger.getLogger( SurveyorRespondentDetailsService.class);
    @Autowired
    private SurveyorRespondentDetailsRepository surveyorRespondentDetailsRepository;
    
    public DashboardDto getDashboardData( String userId )
    {
    	LOGGER.info( "getDashboardData :: request ");
        DashboardDto dashboardDto = new DashboardDto();
        List<Object[]> objectList = surveyorRespondentDetailsRepository.findDashboardData( userId );
        if ( StringUtils.isValidCollection( objectList ) )
        {
            for ( Object[] ob : objectList )
            {
                if(StringUtils.isValidObj( (BigInteger) ob[1] ))
                    dashboardDto.setSurveyLinkSendToSurveyorCount( (BigInteger) ob[1] );
                else
                    dashboardDto.setSurveyLinkSendToSurveyorCount( new BigInteger("0"));
                
                if(StringUtils.isValidObj( (BigInteger) ob[0])  && StringUtils.isValidObj( (BigInteger) ob[1] )) {
                    dashboardDto.setSurveyLinkSendToRespondentCount( ((BigInteger) ob[0]).subtract( ( (BigInteger) ob[1] ) ) );
                }else
                    dashboardDto.setSurveyLinkSendToRespondentCount(new BigInteger("0"));
             
                if(StringUtils.isValidObj( (BigInteger) ob[2] ))
                    dashboardDto.setTotalSurveyLinkSubmittedCount( (BigInteger) ob[2] );
                else
                    dashboardDto.setTotalSurveyLinkSubmittedCount( new BigInteger("0"));
                
             
                /*   if(StringUtils.isValidObj( (BigInteger) ob[1] ))
                    dashboardDto.setSurveySubmittedCount( (BigInteger) ob[1] );
                else
                    dashboardDto.setSurveySubmittedCount( new BigInteger("0"));
                if(StringUtils.isValidObj( (BigInteger) ob[0])  && StringUtils.isValidObj( (BigInteger) ob[1] )) {
                    dashboardDto.setSurveyLinkGenCount( ((BigInteger) ob[0]).subtract( ( (BigInteger) ob[1] ) ) );
                }else
                    dashboardDto.setSurveyLinkGenCount(new BigInteger("0"));
                */   
                
                
            }
        }
        else
        {
            /*       dashboardDto.setSurveyLinkGenCount( new BigInteger( "0" ) );
            dashboardDto.setSurveySubmittedCount( new BigInteger( "0" ) );*/
            dashboardDto.setSurveyLinkSendToRespondentCount( new BigInteger( "0" ) );
            dashboardDto.setSurveyLinkSendToSurveyorCount( new BigInteger( "0" ) );
            dashboardDto.setTotalSurveyLinkSubmittedCount( new BigInteger( "0" ) );
        }
        return dashboardDto;
    }

    public SurveyorRespondentDetails saveSurveyorRespondentDetails( SurveyorRespondentDetails surveyorRespondentDetails )
    {
        return surveyorRespondentDetailsRepository.save( surveyorRespondentDetails );
    }

    public SurveyorRespondentDetails findByUserIdAndRespondentTypeAndRespondentMobileNumber( String userid,
                                                                          String respondentType,
                                                                          String mobile )
    {
        return surveyorRespondentDetailsRepository.findByUserIdAndRespondentTypeAndRespondentMobileNumber( userid, respondentType,
                                                                                                 mobile );
    }

    public SurveyorRespondentDetails findByUseridAndRespondentMobile( String userid, String respondentMobile )
    {
        return surveyorRespondentDetailsRepository.findByUserIdAndRespondentMobileNumber( userid, respondentMobile );
    }

    public SurveyorRespondentDetails findByRespondentMobile( String mobile )
    {
//        return surveyorRespondentDetailsRepository.findByMobileNumber( mobile );
        return surveyorRespondentDetailsRepository.findByRespondentMobileNumber( mobile );
    }

    public void updateSurveyorRespondentDetails( byte isurveySubmitted,
                                                 String modifiedBy,
                                                 Date modifiedTime,
                                                 Long respdtDetailsId )
    {
          surveyorRespondentDetailsRepository.updateSurveyorRespondentDetails( isurveySubmitted, modifiedBy, modifiedTime, respdtDetailsId );
    }
    
    
}
