package com.ncert.survey.businessMgr;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ncert.survey.config.Translator;
import com.ncert.survey.costants.APPServiceCode;
import com.ncert.survey.costants.IAppConstants;
import com.ncert.survey.dto.ErrorModel;
import com.ncert.survey.dto.QuestionairRequestDto;
import com.ncert.survey.dto.QuestionairResponseDto;
import com.ncert.survey.dto.RespondentTypeDto;
import com.ncert.survey.dto.ResponseModel;
import com.ncert.survey.dto.SurveyTypeDto;
import com.ncert.survey.model.MstrRespondent;
import com.ncert.survey.model.MasterSurveyType;
import com.ncert.survey.model.MstrState;
import com.ncert.survey.model.SurveyData;
import com.ncert.survey.model.SurveyorRespondentDetails;
import com.ncert.survey.service.SurveyorRespondentDetailsService;
import com.ncert.survey.utils.StringUtils;

public class RedisMgrImpl {
	private static final Logger LOGGER = Logger.getLogger(RedisMgrImpl.class);
	@Autowired
	private SurveyorRespondentDetailsService surveyorRespondentDetailsService;
	private RedissonClient redissonClient;
	private RMap<String, String> mQuestionnaires;
	private RMap<String, String> mShortUrl;
	private RList<SurveyorRespondentDetails> rlSurveyLink;
	private RList<SurveyData> rlSurverData;
	private RMap<Integer, MstrState> mStates;
	private RMap<Integer, MstrRespondent> mRespondent;
	private RMap<Integer, MasterSurveyType> mSurveyType;

	public RedisMgrImpl(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
		this.mQuestionnaires = redissonClient.getMap("questionnaire");
		this.rlSurveyLink = redissonClient.getList("surveyLink");
		this.rlSurverData = redissonClient.getList("surveyData");
		this.mStates = redissonClient.getMap("mStates");
		this.mShortUrl = redissonClient.getMap("shortUrl");
		this.mRespondent = redissonClient.getMap("mRespondent");
		this.mSurveyType = redissonClient.getMap("mSurveyType");
	}

	public RedisMgrImpl() {
		super();
	}

	public ResponseModel<QuestionairResponseDto> processQuestionnaire(QuestionairRequestDto inDto,
			Translator translator, String language) throws Exception {
		ResponseModel<QuestionairResponseDto> responseModel = new ResponseModel<>();
		QuestionairResponseDto response = new QuestionairResponseDto();
		List<ErrorModel> errorDtos = new ArrayList<>();
		APPServiceCode serviceCode = APPServiceCode.NP_SERVICE_999;
		LOGGER.info("processQuestionnaire :: request :: " + inDto.toString());
		try {
			if (StringUtils.isValidObj(inDto)) {
				SurveyorRespondentDetails surveyorRespondentDetails = surveyorRespondentDetailsService
						.findByUserIdAndRespondentTypeAndRespondentMobileNumber(inDto.getUserId(),
								inDto.getRespondentType(), inDto.getMobileNo());
				if (StringUtils.isValidObj(surveyorRespondentDetails)) {
					// validate respondent type
					boolean validRespondent = Arrays.stream(getRespondentList(language))
							.anyMatch(surveyorRespondentDetails::equals);
					if (validRespondent) {
						if (StringUtils.isValidObj(surveyorRespondentDetails.getIsSurveySubmitted())
								&& surveyorRespondentDetails.getIsSurveySubmitted() == IAppConstants.SURVEY_SUBMITTED) {
							serviceCode = APPServiceCode.NP_SERVICE_003;
							errorDtos = setErrorData(serviceCode, errorDtos, translator);
						} else {
							String data = getQuestionnaire(inDto.getRespondentType(), language);
							if (StringUtils.isValidObj(data)) {
								serviceCode = APPServiceCode.NP_SERVICE_001;
								response.setQuestionair(data);
								response.setMobileNo(inDto.getMobileNo());
								response.setStateId(inDto.getStateId());
								response.setRespondentType(inDto.getRespondentType());
								response.setUserId(inDto.getUserId());
								responseModel.setData(response);
							} else {
								serviceCode = APPServiceCode.NP_SERVICE_996;
								errorDtos = setErrorData(serviceCode, errorDtos, translator);
							}
						}
					} else {
						serviceCode = APPServiceCode.NP_SERVICE_996;
						setErrorData(serviceCode, errorDtos, translator);
					}
				} else {
					serviceCode = APPServiceCode.NP_SERVICE_004;
					errorDtos = setErrorData(serviceCode, errorDtos, translator);
				}
			} else {
				serviceCode = APPServiceCode.NP_SERVICE_997;
				errorDtos = setErrorData(serviceCode, errorDtos, translator);
			}
		} catch (RuntimeException ex) {
			LOGGER.error("processQuestionnaire :: Exception :: " + ex );
			ex.printStackTrace();
		}
		LOGGER.info("getQuestionnaire :- Response : Status Code" + serviceCode.getStatusCode() + "Status Desc : "
				+ serviceCode.getStatusDesc());
		if (StringUtils.isValidCollection(errorDtos))
			responseModel.setErrors(errorDtos);
		LOGGER.error("processQuestionnaire :: response :: " + responseModel );
		return responseModel;
	}

	public APPServiceCode putSurveyData(List<SurveyData> surveyDataList) {
		APPServiceCode serviceCode = APPServiceCode.NP_SERVICE_999;
		LOGGER.info("------------------------------- RedisMgrImpl : PutSurveyData--------------------------");
		try {
			if (StringUtils.isValidObj(surveyDataList) && surveyDataList.size() > 0) {
				if (rlSurverData.addAll(surveyDataList)) {
					serviceCode = APPServiceCode.NP_SERVICE_001;
					LOGGER.info("putSurveyData :: response :: " + surveyDataList.toString() + " :: "
							+ serviceCode + " :: " + IAppConstants.dtf.format(LocalDateTime.now()));
				} else {
					LOGGER.error("putSurveyData :- APPException : Data Didn't save at Redis");
					System.err.println("putSurveyData :- APPException : Data Didn't save at Redis");
				}
			} else {
				serviceCode = APPServiceCode.NP_SERVICE_996;
				// LOGGER.info( "putSurveyData :- Response : Status Code" +
				// serviceCode.getStatusCode() + " "
				// + "Status Desc : " + serviceCode.getStatusDesc() );
				// System.out.println( "putSurveyData ELSE :- Response : Status Code" +
				// serviceCode.getStatusCode() + " "
				// + "Status Desc : " + serviceCode.getStatusDesc() );
				LOGGER.error("putSurveyData :: request :: " + surveyDataList.toString() + " :: "
						+ serviceCode);
			}
		} catch (Exception ex) {
			// LOGGER.info( "Exception putSurveyData - " + ex );
			// System.err.println( "Exception putSurveyData - " + ex );
			LOGGER.info("putSurveyData :: response :: " + ex + " :: " + serviceCode );
			ex.printStackTrace();
		}
		LOGGER.info("putSurveyData :- Response : Status Code" + serviceCode.getStatusCode() + "Status Desc : "
				+ serviceCode.getStatusDesc());
		System.out.println("putSurveyData 184:- Response : Status Code" + serviceCode.getStatusCode() + "  "
				+ "Status Desc : " + serviceCode.getStatusDesc());
		return serviceCode;
	}

	public ResponseModel<ArrayList<RespondentTypeDto>> getRespondentTypes(Translator translator, String language)
			throws Exception {
		ResponseModel<ArrayList<RespondentTypeDto>> responseModel = new ResponseModel<>();
		List<ErrorModel> errorDtos = new ArrayList<>();
		APPServiceCode serviceCode = null;
		ArrayList<RespondentTypeDto> respondentTypeList = null;
		// LOGGER.info("------------------------------- Redis Mgr Impl
		// :getRespondentTypes --------------------------");
		LOGGER.info("getRespondentTypes :: request :: " + "RespondentType");
		try {
			String[] respondentList = getRespondentList(language);
			for (int i = 0; i < respondentList.length; i++) {
				System.out.println("#@123@# RespondentList: " + respondentList[i]);
			}
			if (StringUtils.isValidObj(respondentList)) {
				respondentTypeList = new ArrayList<>();
				if (respondentList.length > 0) {
					for (int i = 0; i < respondentList.length; i++) {
						RespondentTypeDto respondentTypeDto = new RespondentTypeDto(i, respondentList[i]);
						respondentTypeList.add(respondentTypeDto);
					}
					System.out.println("@123@# RespondentList: " + respondentList);
				}
			}
			if (StringUtils.isValidObj(respondentTypeList)) {
				responseModel.setData(respondentTypeList);
				serviceCode = APPServiceCode.NP_SERVICE_001;
				LOGGER.info("getRespondentTypes :: " + serviceCode.getStatusCode() + " :: "
						 + serviceCode.getStatusDesc());
			} else {
				serviceCode = APPServiceCode.NP_SERVICE_996;
				errorDtos = setErrorData(serviceCode, errorDtos, translator);
				LOGGER.error("getRespondentTypes :: " +serviceCode.getStatusCode() + " :: "
						+ serviceCode.getStatusDesc());
			}
		} catch (RuntimeException ex) {
			serviceCode = APPServiceCode.NP_SERVICE_999;
			LOGGER.error("getRespondentTypes :: "+ " :: "+ex );
			errorDtos = setErrorData(serviceCode, errorDtos, translator);
		}
		if (StringUtils.isValidCollection(errorDtos))
			responseModel.setErrors(errorDtos);
		LOGGER.info("getRespondentTypes :: response :: "+ " :: "+responseModel );
		return responseModel;
	}

//***    

	public ResponseModel<ArrayList<SurveyTypeDto>> getSurveyTypes(Translator translator, String language)
			throws Exception {
		LOGGER.info("getSurveyTypes :: request :: "+language );

		ResponseModel<ArrayList<SurveyTypeDto>> responseModel = new ResponseModel<>();
		List<ErrorModel> errorDtos = new ArrayList<>();
		APPServiceCode serviceCode = null;
		ArrayList<SurveyTypeDto> surveyTypeLists = null;

		try {

			String[] surveyTypeList = getSurveyTypeList(language);
			for (int i = 0; i < surveyTypeList.length; i++) {
				System.out.println("#@123@# SurveyTypeList: " + surveyTypeList[i]);
			}
			if (StringUtils.isValidObj(surveyTypeList)) {
				surveyTypeLists = new ArrayList<>();
				if (surveyTypeList.length > 0) {
					for (int i = 0; i < surveyTypeList.length; i++) {
						SurveyTypeDto surveyTypeDto = new SurveyTypeDto(i + 1, surveyTypeList[i]);
						surveyTypeLists.add(surveyTypeDto);
					}
					System.out.println("#@123@# SurveyTypeList: " + surveyTypeList);
				}
			}
			if (StringUtils.isValidObj(surveyTypeLists)) {
				responseModel.setData(surveyTypeLists);
				serviceCode = APPServiceCode.NP_SERVICE_001;
				LOGGER.info("RedisMgrImpl::getSurveyTypes :: " + serviceCode.getStatusCode() + " :: "
						+ serviceCode.getStatusDesc());
			} else {
				serviceCode = APPServiceCode.NP_SERVICE_996;
				errorDtos = setErrorData(serviceCode, errorDtos, translator);
				LOGGER.error("getSurveyTypes :: " + serviceCode.getStatusCode() + " :: "
						 + serviceCode.getStatusDesc());
			}
		} catch (Exception ex) {
			serviceCode = APPServiceCode.NP_SERVICE_999;
			LOGGER.error("getSurveyTypes :: Exception :: " + ex + " :: "
					+ serviceCode.getStatusCode());
			ex.printStackTrace();
			errorDtos = setErrorData(serviceCode, errorDtos, translator);
		}
		if (StringUtils.isValidCollection(errorDtos))
			responseModel.setErrors(errorDtos);
		LOGGER.info("getSurveyTypes :: response "  + " :: "
				+ responseModel);
		return responseModel;

	}

//******    

	public APPServiceCode putSurveyLink(SurveyorRespondentDetails respondentDetails) {
		APPServiceCode serviceCode = null;
		LOGGER.info( "putSurveyLink ");
		LOGGER.info("------------------------------- RedisMgrImpl : PutSurveyLink--------------------------");
		// System.out.println( "------------------------------- RedisMgrImpl :
		// PutSurveyLink--------------------------" );
		try {
			if (StringUtils.isValidObj(respondentDetails)) {
				if (rlSurveyLink.add(respondentDetails)) {
					// mUrl.put(id, respondentDetails.getUrl());
//					System.out.println("ACTUAL URL: " + respondentDetails.getUrl());
					// System.out.println("URL ID: "+id);
					LOGGER.info( "putSurveyLink :: "+"putSurveyLink :- Response : survey link details copied succesfully");
					// System.out.println( "putSurveyLink :- Response : survey link details copied
					// succesfully" );
				} else {
					serviceCode = APPServiceCode.NP_SERVICE_007;
					LOGGER.error( "putSurveyLink :: "+"putSurveyLink :- APPException : Data Didn't save at Redis");
				}
			} else {
				serviceCode = APPServiceCode.NP_SERVICE_996;
				LOGGER.info( "putSurveyLink :: "+"putSurveyLink :- Response : Status Code" + serviceCode.getStatusCode() + "  "
						+ "Status Desc : " + serviceCode.getStatusDesc());
			}
		} catch (RuntimeException ex) {
			LOGGER.error( "putSurveyLink :: "+ex);
			ex.printStackTrace();
			serviceCode = APPServiceCode.NP_SERVICE_999;
			LOGGER.info("putSurveyLink :- Response : Status Code" + serviceCode.getStatusCode() + "Status Desc : "
					+ serviceCode.getStatusDesc());
		}
		return serviceCode;
	}
	// public MstrState getState( Integer id )
	// throws Exception
	// {
	// return mStates.get( id );
	// }

	public MstrRespondent getRespondent(Integer respondentId) throws Exception {
		return mRespondent.get(respondentId);
	}

	public String[] getRespondentList(String language) {
		String[] respondentList = null;
		try {
			LOGGER.info( "getRespondentList"+"language"+language);
			Object[] temp = mRespondent.keySet().toArray();
			ArrayList<Integer> keys = new ArrayList<Integer>();
			for (int i = 0; i < temp.length; i++)
				keys.add((Integer) temp[i]);
			Collections.sort(keys);
			respondentList = new String[keys.size()];
			int n = 0;
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				Integer key = (Integer) iterator.next();
				MstrRespondent mstrRespondent = mRespondent.get(key);
				if (StringUtils.equalsIgnoreCase(language, IAppConstants.IN_hi_LANGUAGE)
						|| StringUtils.equalsIgnoreCase(language, IAppConstants.HIN_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeHin();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.GUJ_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeGuj();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.ASM_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeAsm();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KAS_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeKas();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.DOGRI_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeDoi();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.PANJABI_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypePan();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.BODO_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeBod();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.TELUGU_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeTel();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KANNAD_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeKan();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MARATHI_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeMar();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MALAYALAM_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeMal();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MANIPURI_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeMni();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.URDU_LANGUAGE)) {
					respondentList[n] = mstrRespondent.getRespondentTypeUrd();
				} else {
					respondentList[n] = mstrRespondent.getRespondentTypeEn();
				}
				n++;
			}
		} catch (Exception ex) {
			LOGGER.error( "getRespondentList :: "+"Exception ::"+ex.getStackTrace());
			System.err.println("#@123@# Exception - " + ex);
			ex.printStackTrace();
		}
		return respondentList;
	}

// ***

	public String[] getSurveyTypeList(String language) {
		
		LOGGER.info("getSurveyTypeList :: "+language);
		String[] surveyTypeList = null;
		try {

			Object[] temp = mSurveyType.keySet().toArray();
			ArrayList<Integer> keys = new ArrayList<Integer>();
			for (int i = 0; i < temp.length; i++)
				keys.add((Integer) temp[i]);
			Collections.sort(keys);
			surveyTypeList = new String[keys.size()];
			int n = 0;
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				Integer key = (Integer) iterator.next();
				MasterSurveyType mstrSurveyType = mSurveyType.get(key);
				if (StringUtils.equalsIgnoreCase(language, IAppConstants.IN_hi_LANGUAGE)
						|| StringUtils.equalsIgnoreCase(language, IAppConstants.HIN_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeHin();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.GUJ_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeGuj();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.ASM_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeAsm();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KAS_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeKas();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.DOGRI_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeDoi();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.PANJABI_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypePan();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.BODO_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeBod();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.TELUGU_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeTel();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KANNAD_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeKan();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MARATHI_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeMar();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MALAYALAM_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeMal();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MANIPURI_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeMni();
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.URDU_LANGUAGE)) {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeUrd();
				} else {
					surveyTypeList[n] = mstrSurveyType.getSurveyTypeEn();
				}
				n++;

			}

		} catch (Exception ex) {
			LOGGER.error("getSurveyTypeList :: Exception :: " + ex);
			ex.printStackTrace();
		}
		return surveyTypeList;
	}

// ******
	public ArrayList<MstrRespondent> getMasterRespondentList() {
		ArrayList<MstrRespondent> mstrRespondentList = null;
		try {
			Object[] temp = mRespondent.keySet().toArray();
			ArrayList<Integer> keys = new ArrayList<Integer>();
			for (int i = 0; i < temp.length; i++)
				keys.add((Integer) temp[i]);
			Collections.sort(keys);
			// Set<Integer> keys = mRespondent.keySet();
			mstrRespondentList = new ArrayList<MstrRespondent>();
			for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
				Integer key = (Integer) iterator.next();
				mstrRespondentList.add(mRespondent.get(key));
			}
		} catch (Exception ex) {
			LOGGER.error("Exception - " + ex);
			System.err.println("Exception - " + ex);
			ex.printStackTrace();
		}
		return mstrRespondentList;
	}

	public boolean putUrl(String shortURL, String longUrl) {
		if (StringUtils.isNotEmpty(shortURL) && StringUtils.isNotEmpty(longUrl)) {
			mShortUrl.put(shortURL, longUrl);
			return true;
		}
		return false;
	}

	public String getLongUrl(String shortLink) {
		if (mShortUrl.containsKey(shortLink))
			return mShortUrl.get(shortLink);
		else
			return null;
	}

	public String getQuestionnaire(String respondentType, String language) {
		LOGGER.info("survey_ncf" + " :: processQuestionnaire::RedisMgrImpl::getQuestionnaire" );
		String data = "";
		if (StringUtils.equalsIgnoreCase(language, IAppConstants.IN_hi_LANGUAGE)
				|| StringUtils.equalsIgnoreCase(language, IAppConstants.HIN_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.HINDI_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.GUJ_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.GUJRATI_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.ASM_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.ASSAMI_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KAS_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.KASHMIRI_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.DOGRI_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.DOGRI_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.PANJABI_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.PANJABI_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.BODO_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.BODO_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.TELUGU_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.TELUGU_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KANNAD_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.KANNAD_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MARATHI_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.MARATHI_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MALAYALAM_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.MALAYALAM_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MANIPURI_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.MANIPURI_PREFIX + respondentType);
		} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.URDU_LANGUAGE)) {
			data = mQuestionnaires.get(IAppConstants.URDU_PREFIX + respondentType);
		} else {
			data = mQuestionnaires.get(respondentType);
		}
		return data;
	}
	// public ArrayList<String> getQuestionnaireList( String respondentType, String
	// language )
	// {
	// ArrayList<String> respondentList = new ArrayList<>( Arrays.asList(
	// getRespondentList( IAppConstants.ENGLISH_LANGUAGE ) ) );
	// ArrayList<String> questionnaireList = new ArrayList<>();
	// if ( StringUtils.isValidObj( respondentList ) )
	// {
	// int respondentTypeIndex = respondentList.indexOf( respondentType );
	// if ( respondentTypeIndex >= IAppConstants.ZERO )
	// {
	// questionnaireList.add( ( StringUtils.equalsIgnoreCase( language,
	// IAppConstants.IN_hi_LANGUAGE )
	// || StringUtils.equalsIgnoreCase( language, IAppConstants.HIN_LANGUAGE ) )
	// ? mQuestionnaires
	// .get( IAppConstants.HINDI_PREFIX + respondentType )
	// : mQuestionnaires.get( respondentType ) );
	// respondentList.remove( respondentTypeIndex );
	// }
	// else
	// {
	// return null;
	// }
	//
	// while(respondentList.size() > IAppConstants.ZERO) {
	//
	// questionnaireList.add( ( StringUtils.equalsIgnoreCase( language,
	// IAppConstants.IN_hi_LANGUAGE )
	// || StringUtils.equalsIgnoreCase( language, IAppConstants.HIN_LANGUAGE ) )
	// ? mQuestionnaires
	// .get( IAppConstants.HINDI_PREFIX + respondentList.get( IAppConstants.ZERO ) )
	// : mQuestionnaires.get( respondentList.get( IAppConstants.ZERO ) ) );
	// respondentList.remove( IAppConstants.ZERO );
	//
	// }
	//
	//
	// }
	// return questionnaireList;
	// }

	public ArrayList<String> getQuestionnaireList(String respondentType, String language) {
		ArrayList<MstrRespondent> respondentList = getMasterRespondentList();
		ArrayList<String> questionnaireList = new ArrayList<>();
		int respondentTypeIndex = -1;
		if (StringUtils.isValidObj(respondentList)) {
			for (int i = 0; i < respondentList.size(); i++) {
				System.out.println("RespondentType" + respondentList.get(i).getRespondentTypeEn());
				System.out.println(respondentList.get(i));
			}
			for (int i = 0; i < respondentList.size(); i++) {
				if (StringUtils.equalsIgnoreCase(respondentList.get(i).getRespondentTypeEn(), respondentType)) {
					respondentTypeIndex = i;
					break;
				}
			}
			if (respondentTypeIndex >= IAppConstants.ZERO) {
				// questionnaireList.add( ( StringUtils.equalsIgnoreCase( language,
				// IAppConstants.IN_hi_LANGUAGE )
				// || StringUtils.equalsIgnoreCase( language, IAppConstants.HIN_LANGUAGE ) ) ?
				// mQuestionnaires
				// .get( IAppConstants.HINDI_PREFIX + respondentList.get( respondentTypeIndex )
				// .getQuestionnaireId() ) : mQuestionnaires.get( ""
				// + respondentList.get( respondentTypeIndex ).getQuestionnaireId() ) );
				// ###########################################################################
				if (StringUtils.equalsIgnoreCase(language, IAppConstants.IN_hi_LANGUAGE)
						|| StringUtils.equalsIgnoreCase(language, IAppConstants.HIN_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.HINDI_PREFIX + respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.GUJ_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.GUJRATI_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.ASM_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.ASSAMI_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KAS_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.KASHMIRI_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.DOGRI_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.DOGRI_PREFIX + respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.PANJABI_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.PANJABI_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.BODO_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.BODO_PREFIX + respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.TELUGU_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.TELUGU_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KANNAD_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.KANNAD_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MARATHI_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.MARATHI_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MALAYALAM_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.MALAYALAM_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MANIPURI_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.MANIPURI_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.URDU_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.URDU_PREFIX
							+ respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				} else {
					questionnaireList.add(
							mQuestionnaires.get("" + respondentList.get(respondentTypeIndex).getQuestionnaireId()));
				}
				// ###########################################################################
				// System.out.println( mQuestionnaires
				// .get( "" + respondentList.get( respondentTypeIndex ).getQuestionnaireId() )
				// );
				// System.out.println( mQuestionnaires.get( IAppConstants.HINDI_PREFIX
				// + respondentList.get( respondentTypeIndex ).getQuestionnaireId() ) );
				respondentList.remove(respondentTypeIndex);
			} else {
				return null;
			}
			while (respondentList.size() > IAppConstants.ZERO) {
				// questionnaireList.add( ( StringUtils.equalsIgnoreCase( language,
				// IAppConstants.IN_hi_LANGUAGE )
				// || StringUtils.equalsIgnoreCase( language, IAppConstants.HIN_LANGUAGE ) ) ?
				// mQuestionnaires
				// .get( IAppConstants.HINDI_PREFIX + respondentList.get( IAppConstants.ZERO )
				// .getQuestionnaireId() ) : mQuestionnaires.get( ""
				// + respondentList.get( IAppConstants.ZERO ).getQuestionnaireId() ) );
				if (StringUtils.equalsIgnoreCase(language, IAppConstants.IN_hi_LANGUAGE)
						|| StringUtils.equalsIgnoreCase(language, IAppConstants.HIN_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.HINDI_PREFIX + respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.GUJ_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.GUJRATI_PREFIX
							+ respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.ASM_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.ASSAMI_PREFIX + respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KAS_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.KASHMIRI_PREFIX
							+ respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.DOGRI_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.DOGRI_PREFIX + respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.PANJABI_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.PANJABI_PREFIX
							+ respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.BODO_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.BODO_PREFIX + respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.TELUGU_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.TELUGU_PREFIX + respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.KANNAD_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(
							IAppConstants.KANNAD_PREFIX + respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MARATHI_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.MARATHI_PREFIX
							+ respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MALAYALAM_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.MALAYALAM_PREFIX
							+ respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.MANIPURI_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.MANIPURI_PREFIX
							+ respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else if (StringUtils.equalsIgnoreCase(language, IAppConstants.URDU_LANGUAGE)) {
					questionnaireList.add(mQuestionnaires.get(IAppConstants.URDU_PREFIX
							+ respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				} else {
					questionnaireList
							.add(mQuestionnaires.get("" + respondentList.get(IAppConstants.ZERO).getQuestionnaireId()));
				}
				respondentList.remove(IAppConstants.ZERO);
			}
		}
		return questionnaireList;
	}

	private List<ErrorModel> setErrorData(APPServiceCode serviceCode, List<ErrorModel> errorModels,
			Translator translator) {
		ErrorModel errorDto = new ErrorModel();
		errorDto.setCode(serviceCode.getStatusCode());
		errorDto.setMessage(translator.toLocale(serviceCode.getStatusCode()));
		errorModels.add(errorDto);
		return errorModels;
	}
}
