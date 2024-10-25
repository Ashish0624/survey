package com.ncert.survey.Worker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.ncert.survey.model.SurveyData;
import com.ncert.survey.model.SurveyorRespondentDetails;

public class SaveSurveyLink {

	private static RedissonClient redisson;
	private final String url = "jdbc:postgresql://89.233.105.5:5432/ncf_meeting";
	private final String user = "ncf_meet";
	private final String password = "sdd#$hh$*&!@)BG^";

	private static final String INSERT_SURVEY_LINK = "INSERT INTO public.\"sr_surveyor_respondent_details\""
			+ "  (user_id, first_name, last_name, respondent_type, mobile_number, url, email_id, state_id, issurveysubmitted, status)"
			+ " VALUES " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public static void main(String[] args) {

		try {
			Config config = new Config();
			// use single Redis server
			config.useSingleServer().setAddress("redis://89.233.105.5:6379");
			redisson = Redisson.create(config);

			new SaveSurveyLink().looper();
//          new SaveSurveyLink().test();

			System.out.println("Exit");
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private synchronized void looper() {

		System.out.println("Time: " + Calendar.getInstance().getTime());
		try {
			while (true) {
				TimeUnit.SECONDS.sleep(10);
				redisGet();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!redisson.isShutdown())
				redisson.shutdown();
		}

	}

	public void saveSurveyLink(SurveyorRespondentDetails responseData) throws SQLException {
		System.out.println(INSERT_SURVEY_LINK);
		// Step 1: Establishing a Connection
		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			// Step 2:Create a statement using connection object
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SURVEY_LINK);
			preparedStatement.setString(1, responseData.getUserId());
			preparedStatement.setString(2, responseData.getFirstName());
			preparedStatement.setString(3, responseData.getLastName());
			preparedStatement.setString(4, responseData.getRespondentType());
			preparedStatement.setString(5, responseData.getRespondentMobileNumber());
			preparedStatement.setString(6, responseData.getUrl());
			preparedStatement.setString(7, responseData.getEmail_id());
			preparedStatement.setLong(8, responseData.getState().getStateId());
			preparedStatement.setLong(9, responseData.getIsSurveySubmitted());
			preparedStatement.setLong(10, responseData.getStatus());

			System.out.println("PrepareStatement: " + preparedStatement);
			// Step 3: Execute the query or update query
			preparedStatement.executeUpdate();
		} catch (SQLException e) {

			// print SQL exception information
			e.printStackTrace();
		}

		// Step 4: try-with-resource statement will auto close the connection.
	}

	public void test() throws SQLException {
		System.out.println(INSERT_SURVEY_LINK);
		// Step 1: Establishing a Connection
		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			// Step 2:Create a statement using connection object
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SURVEY_LINK);
			preparedStatement.setLong(1, 1);
			preparedStatement.setString(2, "Tarun");
			preparedStatement.setString(3, "Gariya");
			preparedStatement.setString(4, "Teacher");
			preparedStatement.setString(5, "9911327757");
			preparedStatement.setString(6, "http://abc");
			preparedStatement.setString(7, "abc@gmail.com");
			preparedStatement.setLong(8, 23);
			preparedStatement.setByte(9, (byte) 0);
			preparedStatement.setByte(10, (byte) 0);

			System.out.println("PrepareStatement: " + preparedStatement);
			// Step 3: Execute the query or update query
			preparedStatement.executeUpdate();
		} catch (SQLException e) {

			// print SQL exception information
			e.printStackTrace();
		}

		// Step 4: try-with-resource statement will auto close the connection.
	}

	public String redisGet() throws SQLException {
		System.out.println("Calling redisPut ");

		RList<SurveyorRespondentDetails> list = redisson.getList("surveyLink");
		int n = list.size();
		System.out.println("List Size: " + n);
		while (list.size() > 0) {
			System.out.println("Size list:" + list.size());
			SurveyorRespondentDetails responseData = list.get(0);
			System.out.println("SurveyorRespondentDetails: " + responseData.toString());
			SaveSurveyLink app = new SaveSurveyLink();
			app.saveSurveyLink(responseData);
			;

			System.out.println("Added: " + responseData);
			list.remove(0);
		}
		return "Successfully ADDED";
	}

}
