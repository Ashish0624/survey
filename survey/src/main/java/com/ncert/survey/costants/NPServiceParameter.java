package com.ncert.survey.costants;

import com.ncert.survey.utils.StringUtils;

public enum NPServiceParameter {
	LIST("list"), IDLIST("idlist"), VIEW("view"), CREATE("create"), UPDATE("update"), STATUS("status"),
	DELETE("delete"), MANAGE("manage"), VALIDATE("validate"), AUTHENTICATE("authenticate"),
	FORGOT_PASSWORD_NO_LINK("forgotPasswordNoLink"), FORGOT_PASSWORD("forgotPassword"),
	CHANGE_PASSWORD("changePassword"), RESET_PASSWORD("resetPassword"), SEARCH("search"), COUNT("count"), READ("read"),
	UNREAD("Unread"), LINK("link"), SUMMARY("summary"), DETAIL("detail"), REGISTER_DEVICE("registerDevice"),
	GET_USER_DEVICES("getUserDevices"), BY_USER("byUser"), SUBMIT("submit"), BULK_INSERT("bulkInsert"),
	DOWNLOAD("download"), REPORT("report");

	private String parameter = null;

	private NPServiceParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getParameter() {
		return parameter;
	}

	public static NPServiceParameter getNPServiceParameter(String parameter) {
		{
			for (NPServiceParameter displayMsgEnum : NPServiceParameter.values())
				if (StringUtils.equals(displayMsgEnum.getParameter(), parameter)) {
					return displayMsgEnum;
				}
		}
		return null;
	}
}
