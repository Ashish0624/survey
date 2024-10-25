package com.ncert.survey.dto;

import java.io.Serializable;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto implements Serializable {

    private BigInteger surveyLinkSendToSurveyorCount;
    private BigInteger surveyLinkSendToRespondentCount;
    private BigInteger totalSurveyLinkSubmittedCount;
    
}
