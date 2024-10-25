package com.ncert.survey.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ncert.survey.businessMgr.RedisMgrImpl;
import com.ncert.survey.costants.IAppConstants;

@Component
public class ValidationUtil
{
    @Autowired
    private RedisMgrImpl redisMgrImpl;
    public boolean isMobileValid( String mobile )
    {
        if ( StringUtils.isValidObj( mobile ) )
        {
            if ( mobile.length() > 10 )
            {
                String orgMobile = AppEncryptionUtil.decrypt( mobile );
                Pattern pattern = Pattern.compile( "[6-9][0-9]{9}" );
                Matcher matcher = pattern.matcher( orgMobile );
                if ( matcher.matches() )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEmailValid( String email )
    {
        String orgEmail = AppEncryptionUtil.decrypt( email );
        boolean result = true;
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile( regex );
        Matcher matcher = pattern.matcher( orgEmail );
        result = matcher.matches();
        if ( !result )
            return false;
        return true;
    }
    
    public boolean isValidVersion( String version )
    {
        boolean result = true;
        String regex = "\\d{1,2}\\.\\d{1,2}\\.\\d{1,3}";
        Pattern pattern = Pattern.compile( regex );
        Matcher matcher = pattern.matcher( version );
        result = matcher.matches();
        if ( !result )
            return false;
        return true;
    }

    //
    
    public boolean isNameValid( String name )
    {
        name = name.toLowerCase();
        name = name.trim();
        char[] charArray = name.toCharArray();
        for ( int i = 0; i < charArray.length; i++ )
        {
            char ch = charArray[i];
            if ( ! ( ( ch >= 'a' && ch <= 'z' ) || ch == ' ' ) )
            {
                return false;
            }
        }
        return true;
    }
    
    public boolean isOsValid( String name )
    {
        name = name.toLowerCase();
        if(name.equals(IAppConstants.ANDROID) || name.equals(IAppConstants.IOS)) {
        	return true;
        }
        return false;
    }

    public boolean isFillSurveyValid( byte fillSurvey )
    {
        if ( fillSurvey == 1 || fillSurvey == 0 )
        {
            return true;
        }
        return false;
    }

    public boolean isRespondentValid( String respondent )
    {
        if ( StringUtils.isValidObj( respondent ) )
        {
            int r = Integer.parseInt( respondent );
            if ( -1 < r && r < 12 )
            {
                return true;
            }
        }
        return false;
    }

    public boolean isOtpValid( int otp )
    {
        return ( String.valueOf( otp ).length() == 6 );
    }

    public boolean isValidRespondentType( String respondent, String language )
        throws Exception
    {
        String[] respondentTypeList = redisMgrImpl.getRespondentList( language );
        ArrayList<String> nameList = new ArrayList<>( Arrays.asList( respondentTypeList ) );
        System.out.println( nameList.toString() );
        return nameList.contains( respondent );

    }

    public boolean isValidRespondentType( int respondent_index )
        throws Exception
    {
        String[] respondentTypeList = redisMgrImpl.getRespondentList( IAppConstants.ENGLISH_LANGUAGE );
        System.out.println( respondentTypeList );
        return ( 0 <= respondent_index && respondent_index < respondentTypeList.length);

    }
    
    public boolean isValidSurveyType( int surveyType_index ) throws Exception {
    	String[] surveyTypeList = redisMgrImpl.getSurveyTypeList( IAppConstants.ENGLISH_LANGUAGE );
    	System.out.println( surveyTypeList);
    	return ( 0 < surveyType_index && surveyType_index <= surveyTypeList.length);
    }
    
    public int compareVersions(String installedVersion, String lastCompulsoryVersion) {
        String[] versionTokensA = installedVersion.split("\\.");
        String[] versionTokensB = lastCompulsoryVersion.split("\\.");
        List<Integer> versionNumbersA = new ArrayList<>();
        List<Integer> versionNumbersB = new ArrayList<>();

        for (String versionToken : versionTokensA) {
          versionNumbersA.add(Integer.parseInt(versionToken));
        }
        System.out.println(versionNumbersA);
        for (String versionToken : versionTokensB) {
          versionNumbersB.add(Integer.parseInt(versionToken));
        }
        System.out.println(versionNumbersB);

        final int versionASize = versionNumbersA.size();
        final int versionBSize = versionNumbersB.size();
        int maxSize = Math.max(versionASize, versionBSize);

        for (int i = 0; i < maxSize; i++) {
          if ((i < versionASize ? versionNumbersA.get(i) : 0) > (i < versionBSize ? versionNumbersB.get(i) : 0)) {
            return 1;
          } else if ((i < versionASize ? versionNumbersA.get(i) : 0) < (i < versionBSize ? versionNumbersB.get(i) : 0)) {
            return -1;
          }
        }
        return 0;
      }  
    
    
    	public static void main(String arg[]) throws Exception {
    //		
    		ValidationUtil valid =  new ValidationUtil();
    		System.out.println(valid.compareVersions("1.0.9", "1.0.8"));
    		System.out.println(valid.isOsValid("ios2"));
    //		System.out.println(valid.validateRespondentType("Teacher", "en")); 
    //		
    //	}
    //	
    //	   public static boolean isValidRespondentType(String respo) throws Exception {
    //	        
    //	        System.out.println("redisss ::: "+redisMgrImpl);
    //	        String repondentTypeList[] =  redisMgrImpl.getRespondentList("en");
    //	        
    //	        for (int i = 0; i < repondentTypeList.length; i++) {
    //	            if (respo.equals(repondentTypeList[i])) {
    //	                return true;
    //	            }
    //	        }
    //	        return false;
    	    }
}
