package com.ncert.survey.utils;

import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class ResSignatureUtil {
	private static final Logger LOGGER = Logger.getLogger(ResSignatureUtil.class);
	private static String preShareKeyForSignature = "QD32VdbRuMa0iI0q9q7cH6FIHGcNWGdEZOLyK669";
	private static String signatureAlgorithm = "HmacSHA256";

	public static boolean isSignatureValid(final String signature, final String methodName, final String inServiceParam,
			final String inClientParam, final String timestamp, final Object inDetail) {
		boolean status = false;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("signature - " + signature);
			LOGGER.debug("methodName - " + methodName);
			LOGGER.debug("inServiceParam - " + inServiceParam);
			LOGGER.debug("inClientParam - " + inClientParam);
			LOGGER.debug("inDetail - " + inDetail);
			LOGGER.debug("timestamp - " + timestamp);
		}
		final String derivedSignature = getSignature(methodName, inServiceParam, inClientParam, timestamp, inDetail);
		LOGGER.info("Received Signature:" + signature);
		LOGGER.info("Derived Signature:" + derivedSignature);
		if (StringUtils.isNotBlank(signature) && StringUtils.isNotBlank(derivedSignature)
				&& signature.equals(derivedSignature)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Signature Valid for methodName %s", methodName));
			}
			status = true;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Signature InValid for methodName %s", methodName));
		}
		return status;
	}

	public static String getSignature(String methodName, String inServiceParam, String inClientParam, String timestamp,
			Object inObj) {
		StringBuffer data = new StringBuffer();
		String detail = SafeFieldUtils.readField(inObj, "param", true, String.class) + timestamp;
		Integer recordID = SafeFieldUtils.readField(inObj, "recordId", true, Integer.class);
		String clientIPAddress = SafeFieldUtils.readField(inObj, "clientIPAddress", true, String.class);
		String statusCode = SafeFieldUtils.readField(inObj, "statusCode", true, String.class);
		detail = detail + detail.hashCode();
		if (null != recordID && recordID > 0) {
			detail = detail + recordID;
		}
		if (null != clientIPAddress && !"".equals(clientIPAddress)) {
			detail = detail + clientIPAddress;
		}
		if (null != statusCode && !"".equals(statusCode)) {
			detail = detail + statusCode;
		}
		data.append(methodName).append(timestamp).append(inServiceParam).append(inClientParam).append(detail);
		String signature = calculateHMAC(data.toString());
		return signature;
	}

	public static boolean isSignatureValid(final String signature, final String url, final String timestamp,
			final Object inObj) {
		boolean status = false;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("signature - " + signature);
			LOGGER.debug("url - " + url);
			LOGGER.debug("inObj - " + inObj);
			LOGGER.debug("timestamp - " + timestamp);
		}
		final String derivedSignature = getSignature(url, timestamp, inObj);
		LOGGER.info("Received Signature:" + signature);
		LOGGER.info("Derived Signature:" + derivedSignature);
		if (StringUtils.isNotBlank(signature) && StringUtils.isNotBlank(derivedSignature)
				&& signature.equals(derivedSignature)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Signature Valid for url %s", url));
			}
			status = true;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Signature InValid for url %s", url));
		}
		return status;
	}

	/**
	 * 
	 * @param url       : /master/{serviceParam}/{clientParam}
	 * @param timestamp
	 * @param inObj
	 * @return
	 */
	private static String getSignature(String url, String timestamp, Object inObj) {
		StringBuffer data = new StringBuffer();
		String detail = SafeFieldUtils.readField(inObj, "param", true, String.class) + timestamp;
		Integer recordID = SafeFieldUtils.readField(inObj, "recordId", true, Integer.class);
		String clientIPAddress = SafeFieldUtils.readField(inObj, "clientIPAddress", true, String.class);
		String statusCode = SafeFieldUtils.readField(inObj, "statusCode", true, String.class);
		detail = detail + detail.hashCode();
		if (null != recordID && recordID > 0) {
			detail = detail + recordID;
		}
		if (null != clientIPAddress && !"".equals(clientIPAddress)) {
			detail = detail + clientIPAddress;
		}
		if (null != statusCode && !"".equals(statusCode)) {
			detail = detail + statusCode;
		}
		if (StringUtils.isNotBlank(url)) {
			if (StringUtils.startsWith(url, "/")) {
				url = StringUtils.removeStart(url, "/");
				String strArr[] = StringUtils.split(url, "/");
				data.append(strArr[0]).append(timestamp).append(strArr[1]).append(strArr[2]).append(detail);
			}
		}
		String signature = calculateHMAC(data.toString());
		return signature;
	}

	private static String calculateHMAC(String data) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(preShareKeyForSignature.getBytes(), signatureAlgorithm);
			Mac mac = Mac.getInstance(signatureAlgorithm);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			String derivedSignature = new String(Base64.encodeBase64(rawHmac));
			return derivedSignature;
		} catch (GeneralSecurityException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static void main(String[] args) {
		/*
		 * String url = "/users/{serviceParam}/{userParam}"; long timestamp =
		 * System.currentTimeMillis(); DataDto dataDto = new DataDto();
		 * System.out.println( "Timestamp : " + timestamp ); String signature =
		 * ResSignatureUtil.getSignature( url, String.valueOf( timestamp ), dataDto );
		 * System.out.println( signature );
		 */
	}
}
