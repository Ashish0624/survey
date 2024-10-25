package com.ncert.survey.costants;

import com.ncert.survey.utils.StringUtils;

public enum APPServiceCode {
    NP_SERVICE_001("NP_SERVICE_001", "Your request has been processed successfully"),
    NP_SERVICE_002("NP_SERVICE_002", "Oops! It seems your credentials did not match with our records"),
    NP_SERVICE_003("NP_SERVICE_003", "Survey is already submitted for this user."),
    NP_SERVICE_004("NP_SERVICE_004", "Survey link is not generated for this respondent"),
    NP_SERVICE_005("NP_SERVICE_005", "Survey link is already generated using this mobile no."),
    NP_SERVICE_006("NP_SERVICE_006", "Failed to send survey link mail."),
    NP_SERVICE_007("NP_SERVICE_007", "Failed to generate survey link."),
    NP_SERVICE_008("NP_SERVICE_008", "Invalid mobile no."), 
    NP_SERVICE_009("NP_SERVICE_009", "Invalid email-id."),
    NP_SERVICE_010("NP_SERVICE_010", "Invalid first name."), 
    NP_SERVICE_011("NP_SERVICE_011", "Invalid last name."),
    NP_SERVICE_012("NP_SERVICE_012", "Invalid State ID."), 
    NP_SERVICE_013("NP_SERVICE_013", "Invalid user ID."),
    NP_SERVICE_014("NP_SERVICE_014", "Invalid isFillSurvey."),
    NP_SERVICE_015("NP_SERVICE_015", "Invalid Respondent Type."), 
    NP_SERVICE_016("NP_SERVICE_016", "Invalid transaction ID."),
    NP_SERVICE_017("NP_SERVICE_017", "Invalid Questionnaire ID."),
    NP_SERVICE_018("NP_SERVICE_018", "Invalid otp."),
    NP_SERVICE_019("NP_SERVICE_019", "Invalid templateId."), 
    NP_SERVICE_020("NP_SERVICE_020", "Empty Sms Body."),
    NP_SERVICE_021("NP_SERVICE_021", "Survey link is already generated for this user."),
    NP_SERVICE_022("NP_SERVICE_022", "Invalid Survey Type."),
    NP_SERVICE_023("NP_SERVICE_023", "Invalid User OS."),
    NP_SERVICE_024("NP_SERVICE_024", "Invalid App Version."),
   
    NP_SERVICE_994("NP_SERVICE_994", "Failed to send Notification"),
    NP_SERVICE_995("NP_SERVICE_995", "Survey Link not generated"), 
    NP_SERVICE_996("NP_SERVICE_996", "No record found"),
    NP_SERVICE_997("NP_SERVICE_997", "Invalid Request, Validation Error"),
    NP_SERVICE_998("NP_SERVICE_998", "Sorry! Security measures are not being followed"),
    NP_SERVICE_999("NP_SERVICE_999", "Unable to process your request, please try later"),;
    String statusCode = null;
    String statusDesc = null;
    private APPServiceCode( String statusCode, String statusDesc )
    {
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
    }

    public String getStatusCode()
    {
        return statusCode;
    }

    public String getStatusDesc()
    {
        return statusDesc;
    }

    public static APPServiceCode getServiceByErrorKey( String errorKey )
    {
        for ( APPServiceCode serviceCode : APPServiceCode.values() )
        {
            if ( StringUtils.equals( serviceCode.getStatusCode(), errorKey ) )
            {
                return serviceCode;
            }
        }
        return APPServiceCode.NP_SERVICE_997;
    }
}
