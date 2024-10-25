package com.ncert.survey.config;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.simple.JSONObject;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.ncert.survey.exception.SignatureValidationFailed;
import com.ncert.survey.utils.SafeFieldUtils;
import com.ncert.survey.utils.SignatureUtil;
import com.ncert.survey.utils.StringUtils;

import io.netty.util.internal.StringUtil;

@Component
@Aspect
public class AppControllerAdvice {
	private static final String SIGNATURE_HEADER_KEY = "signature";
	private static final String REQUEST_TIMESTAMP_KEY = "timestamp";
	private static final Map<String, String> methodNameMap = new HashMap<String, String>();
	private static final LRUMap<String, String> LRUMAP = new LRUMap<>(10000);
	private static final String API_SIGNATURE = "api.signature";
	private static final Logger LOGGER = Logger.getLogger(AppControllerAdvice.class);

	@Pointcut("execution(* com.ncert.survey.controller.AppController.*(..))")
	public void controllerMethods() {
	}

	@Pointcut("execution(@com.ncert.survey.costants.CheckSignature * *(..))")
	public void signatureValidation() {
	}

	@Before("signatureValidation()")
	public void signatureCheckerBeforeAdvice(JoinPoint jp) throws Throwable {
		System.out.println("===============BEFORE================ From Before Advice on Check Signature : "
				+ jp.getSignature().getDeclaringTypeName() + ",Signature Name :" + jp.getSignature().getName());
		LOGGER.info("---------------------------------------------------" + jp.getSignature().getDeclaringTypeName()
				+ ",Signature Name :" + jp.getSignature().getName()
				+ "---------------------------------------------------");
		if (true) {
			final Object[] args = jp.getArgs();
			System.out.println("args Length :" + args.length);
			ContentCachingRequestWrapper request = getWrapper((ProceedingJoinPoint) jp);
			System.out.println(getRequestBody(request));
			String methodName = null;
			if (args != null && args.length > 5) {
				try {
					@SuppressWarnings("rawtypes")
					final Map httpHeaders = (Map) args[0];
					final String timeStamp = (String) httpHeaders.get(REQUEST_TIMESTAMP_KEY);
					if (!validTimeStatmp(timeStamp)) {
						LOGGER.error("Timestamp Invalid:" + timeStamp);
						throw new SignatureValidationFailed("Bad Request - Signature Validation Failed : ",
								getMethodName(jp.getSignature()));
					}
					final String objectDto = getRequestBody(request);
					final String inServiceParam = (String) args[1];
					final String inClientParam = (String) args[2];
					methodName = getMethodName(jp.getSignature());
					final String signature = (String) httpHeaders.get(SIGNATURE_HEADER_KEY);
					final String key = methodName.hashCode() + timeStamp;
					if (LRUMAP.containsKey(key)) {
						if (StringUtils.equals(signature, LRUMAP.get(key))) {
							LOGGER.error("Duplicate request with same signature:" + timeStamp);
							throw new SignatureValidationFailed(
									"Bad Request - Signature Validation Failed :" + getMethodName(jp.getSignature()));
						}
					}
					System.out.println("Given Signature:" + signature + ",Signature Param:" + methodName + ","
							+ inServiceParam + "," + inClientParam + "," + timeStamp + "," + objectDto);

					if (inClientParam.equalsIgnoreCase("FileUploaded")) {
						System.out.println("signature bypassed");
					} else if (!SignatureUtil.isSignatureValid(signature, methodName, inServiceParam, inClientParam,
							timeStamp, objectDto)) {
						throw new SignatureValidationFailed(
								"Bad Request - Signature Validation Failed : " + getMethodName(jp.getSignature()));
					} else {
						System.out.println("signature match");
					}
					LRUMAP.put(key, signature);
				} catch (Exception ex) {
					LOGGER.warn("Exception validating signature - " + ex, ex);
					throw ex;
				}
			} else {
				LOGGER.error("Expecting signatures on method not having the required number of arguments");
				throw new SignatureValidationFailed("Bad Request - Signature Validation Failed");
			}
		}
	}

	@Around("controllerMethods()")
	public Object timingAroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println(pjp.getSignature());
		String methodName = getMethodName(pjp.getSignature());
		System.out.println(methodName);
		if (StringUtils.isNotBlank(methodName)) {
			MDC.put("PARAM3", methodName);
		}
		StopWatch sw = new StopWatch();
		sw.start(methodName);
		Object[] args = pjp.getArgs();
		System.out.println("------------"+args[0]+"--------------"+args[1]+"--------------------"+args[2]+"----------"+args[3]+"---------"+args[4]+"------"+args[5]);
		if (args != null && args.length >= 3) {
			String clientIP = null;
			Object payload = args[3];
			Object api = args[2];
			MDC.put("api",api);
			if (args.length >= 5) {
				HttpServletRequest request = (HttpServletRequest) args[4];
				clientIP = request.getRemoteAddr();
			}
			String user =null;
			if((api).equals("getSurveyType") || (api).equals("getRespondentType")) 
			{
				Object lan = args[0];
				System.out.println("++++++++++++++++++++++++      "+lan);
				if(lan instanceof LinkedHashMap) {
				LinkedHashMap<String, String> header = (LinkedHashMap) lan;
					System.out.println("LANG:" + header.get(HttpHeaders.ACCEPT_LANGUAGE));
					user = header.get("accept-language");
					
				}
				
			}
			else
			{
				user = SafeFieldUtils.readField(payload, "mobileNo", true, String.class);
				if (StringUtils.isBlank(user)) {
					user = SafeFieldUtils.readField(payload, "respondentMobileNumber", true, String.class);
				}
				if (StringUtils.isBlank(user)) {
					user = SafeFieldUtils.readField(payload, "userId", true, String.class);
				}
			}
//			if(StringUtils.isBlank(user))
//			{
//				Object lan = args[1];
//				user = SafeFieldUtils.readField(lan, "language", true, String.class);
//			}
//			user = null;
			if (StringUtils.isNotBlank(user)) {
				MDC.put("PARAM", user);
			}else {
				MDC.put("PARAM", "null");
				
			}
			
//			System.out.println("############"+mobileNo);
//			String user = SafeFieldUtils.readField(payload, "mobileNo", true, String.class);
//			if (StringUtils.isBlank(user)) {
//				user = SafeFieldUtils.readField(payload, "emailId", true, String.class);
//			}
//			if (StringUtils.isNotBlank(user)) {
//				MDC.put("PARAM1", user);
//			}
//			if (StringUtils.isNotBlank(clientIP)) {
//				MDC.put("PARAM2", clientIP);
//			}
		}
		System.out.println("===============AROUND - BEFORE================ From Around Advice : "
				+ pjp.getSignature().getDeclaringTypeName() + " " + pjp.getSignature().getName());
		if (LOGGER.isDebugEnabled()) {
			int i = 1;
			for (Object o : pjp.getArgs()) {
				System.out.println("Args [ " + i++ + "]" + o.toString());
			}
		}
		Object retVal = pjp.proceed();
		sw.stop();
		System.out.println("===============AROUND - AFTER================" + sw);
		//MDC.remove("PARAM1");
		//MDC.remove("PARAM2");
		//MDC.remove("PARAM3");
		MDC.remove("PARAM");
		MDC.remove("api");
		return retVal;
	}

	private boolean validTimeStatmp(String inTimeStamp) {
		long timeStamp = StringUtils.numericValue(inTimeStamp);
		if (timeStamp <= 0 || (Calendar.getInstance().getTimeInMillis() - timeStamp > (10 * 60 * 1000))) {
			return false;
		}
		return true;
	}

	private static String getMethodName(Signature signature) {
		String methodName = methodNameMap.get(signature.getName());
		System.out.println(signature.getName() + "method Name::" + methodName);
		if (methodName == null) {
			final RequestMapping requestMappingAnnotation = AnnotationUtils
					.findAnnotation(((MethodSignature) signature).getMethod(), RequestMapping.class);
			final String[] value = requestMappingAnnotation.value();
			methodName = value[0];
			System.out.println("Method Name >>>>>" + methodName);
			if (StringUtils.startsWith(methodName, "/")) {
				methodName = StringUtils.removeStart(methodName, "/");
				if (StringUtils.contains(methodName, "/")) {
					methodName = StringUtils.split(methodName, "/")[0];
				}
			}
			if (StringUtils.isNotBlank(methodName)) {
				methodNameMap.put(signature.getName(), methodName);
			}
		}
		return methodName;
	}

	private String getRequestBody(final ContentCachingRequestWrapper wrapper) {
		String payload = null;
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				try {
					int maxLength = buf.length > 2000 ? 2000 : buf.length;
					payload = new String(buf, 0, maxLength, wrapper.getCharacterEncoding());
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("UnsupportedEncoding.", e);
				}
			}
		}
		return payload;
	}

	private ContentCachingRequestWrapper getWrapper(ProceedingJoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		ContentCachingRequestWrapper request = null;
		for (Object arg : args) {
			if (arg instanceof ContentCachingRequestWrapper) {
				request = (ContentCachingRequestWrapper) arg;
				break;
			}
		}
		return request;
	}
	/*
	 * @AfterReturning(pointcut =
	 * "execution(* com.np.sankalp.service.controller.AppController.*(..))",
	 * returning = "retVal") public void afterReturningAdvice( JoinPoint jp, Object
	 * retVal ) { HttpServletRequest request = ( (ServletRequestAttributes)
	 * RequestContextHolder.currentRequestAttributes() ) .getRequest(); String
	 * requestUrl = request.getScheme() + "://" + request.getServerName() + ":" +
	 * request.getServerPort() + request.getContextPath() + request.getRequestURI()
	 * + "?" + request.getQueryString(); Object[] agrs = jp.getArgs(); for ( Object
	 * object : agrs ) { System.out.println( " Object ::: " + object.toString() ); }
	 * System.out.println( " *******************Client IP :: " +
	 * request.getRemoteAddr() ); System.out.println(
	 * " *******************Method Signature: " + jp.getSignature() );
	 * System.out.println( " *******************Returning:" + retVal.toString() );
	 * System.out.println( " *******************Request Url : " + requestUrl ); }
	 */
}
