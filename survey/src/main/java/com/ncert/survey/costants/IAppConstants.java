package com.ncert.survey.costants;

import java.time.format.DateTimeFormatter;

public interface IAppConstants {
    public final int active = 1;
    public final int ZERO = 0;
    public final String NCERT_UTIL_PROP_FILE_NAME = "";
    public final String SPACE = " ";
    public final String UNDERSCORE = "_";
    public final String BACK_SLASH = "/";
    public final String DOT = ".";
    public final String ENGLISH_LANGUAGE = "en";
    public final String IN_hi_LANGUAGE = "IN_hi";
    public final String API_SIGNATURE = "api.signature";
    public final String RESPONDENT_TYPE = "RespondentType";
    public final byte SURVEY_SUBMITTED = 1;
    public final byte FILL_A_SURVEY = 1;
	
    public final String ANDROID = "android";
    public final String IOS = "ios";
    
    public final String HIN_LANGUAGE = "hin";
    public final String EN_LANGUAGE = "en";
    public final String GUJ_LANGUAGE = "guj";
    public final String ASM_LANGUAGE = "asm";
    public final String KAS_LANGUAGE = "kas";
    public final String DOGRI_LANGUAGE = "doi";
    public final String PANJABI_LANGUAGE = "pan";
    public final String BODO_LANGUAGE = "bod";
    public final String TELUGU_LANGUAGE = "tel";
    public final String KANNAD_LANGUAGE = "kan";
    public final String MARATHI_LANGUAGE = "mar";
    public final String MALAYALAM_LANGUAGE = "mal";
    public final String MANIPURI_LANGUAGE = "mni";
    public final String URDU_LANGUAGE = "urd";
    
    public final String HINDI_PREFIX = "hin";
    public final String GUJRATI_PREFIX = "guj";
    public final String ASSAMI_PREFIX = "asm";
    public final String KASHMIRI_PREFIX = "kas";
    public final String DOGRI_PREFIX = "doi";
    public final String PANJABI_PREFIX = "pan";
    public final String BODO_PREFIX = "bod";
    public final String TELUGU_PREFIX = "tel";
    public final String KANNAD_PREFIX = "kan";
    public final String MARATHI_PREFIX = "mar";
    public final String MALAYALAM_PREFIX = "mal";
    public final String MANIPURI_PREFIX = "mni";
    public final String URDU_PREFIX = "urd";
    
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
}
