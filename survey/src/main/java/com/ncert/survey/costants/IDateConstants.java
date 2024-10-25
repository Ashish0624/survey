package com.ncert.survey.costants;

import java.text.SimpleDateFormat;

public interface IDateConstants {
	String SDF_DD_MMM_YYYY = new String("dd-MMM-yyyy");
	String FORMAT_DD_MMM_YYYY_HH_MM_SS = "dd-MMM-yyyy HH:mm:ss";
	String FORMAT_DD_MMM_YYYY = "dd-MMM-yyyy";
	String SDF_DD_MMM_YYYY_HH_MM_SS = new String(FORMAT_DD_MMM_YYYY_HH_MM_SS);
	String SDF_HH = new String("HH");
	String SDF_YYYYMMDD_HHMMSS = new String("yyyyMMdd_HHmmSS");
	String SDF_GMT = new String("EEE MMM dd HH:mm:ss zzz yyyy");
	String SDF_DD_MM_YYYY = new String("dd/MM/yyyy");
	String yyMMddHHmmSS = new String("yyMMddHHmmss");
	String SDF_DD_MM_YYYY_HH_MM_SS = new String("dd-MM-yyyy-HH-mm-ss");
	String SDF_MMMM_YYYY = new String("MMMM yyyy");
	String SDF_MMM_YYYY = new String("MMM-yyyy");
	SimpleDateFormat FORMAT_DD_MM_YYYY_HH_MM_AM_PM = new SimpleDateFormat("dd-MM-yyyy, HH:mm a");
}
