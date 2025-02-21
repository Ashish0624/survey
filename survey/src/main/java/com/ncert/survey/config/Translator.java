package com.ncert.survey.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class Translator {

	private final ResourceBundleMessageSource messageSource;

	@Autowired
	Translator(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String toLocale(String msgCode) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(msgCode, null, locale);
	}
}
