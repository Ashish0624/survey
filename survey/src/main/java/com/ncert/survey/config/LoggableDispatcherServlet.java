package com.ncert.survey.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class LoggableDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!(request instanceof ContentCachingRequestWrapper)) {
			request = new ContentCachingRequestWrapper(request);
		}
		super.doDispatch(request, response);
	}
}