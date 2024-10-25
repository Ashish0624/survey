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

public class SaveSurveyData {

	private static RedissonClient redisson;
	private final String url = "jdbc:postgresql://89.233.105.5:5432/ncf_meeting";
	private final String user = "ncf_meet";
	private final String password = "sdd#$hh$*&!@)BG^";

	private static final String INSERT_SURVEY_DATA = "INSERT INTO public.\"sr_survey_data\""
			+ "  (user_id,question_id,questionnaire_id,answer,state_id) VALUES " + " (?, ?, ?, ?, ?)";

	public static void main(String[] args) {

		try {
			Config config = new Config();
			// use single Redis server
			config.useSingleServer().setAddress("redis://89.233.105.5:6379");
			redisson = Redisson.create(config);

			new SaveSurveyData().looper();
//          new SaveSurveyData().test();

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

	public void saveSurveyData(SurveyData responseData) throws SQLException {
		System.out.println(INSERT_SURVEY_DATA);
		// Step 1: Establishing a Connection
		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			// Step 2:Create a statement using connection object
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SURVEY_DATA);
			preparedStatement.setString(1, responseData.getUserId());
			preparedStatement.setLong(2, responseData.getQuestionId());
			preparedStatement.setLong(3, responseData.getQuestionnaireId());
			preparedStatement.setString(4, responseData.getAnswer());
			preparedStatement.setLong(5, responseData.getState().getStateId());

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
		System.out.println(INSERT_SURVEY_DATA);
		// Step 1: Establishing a Connection
		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			// Step 2:Create a statement using connection object
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SURVEY_DATA);
			preparedStatement.setLong(1, 123);
			preparedStatement.setLong(2, 321);
			preparedStatement.setLong(3, 1);
			preparedStatement.setString(4, "A,B");
			preparedStatement.setLong(5, 36);

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

		RList<SurveyData> list = redisson.getList("surveyData");
		int n = list.size();
		System.out.println("List Size: " + n);
		while (list.size() > 0) {
			System.out.println("Size list:" + list.size());
			SurveyData responseData = list.get(0);
			System.out.println("SurveyData: " + responseData.toString());
			SaveSurveyData app = new SaveSurveyData();
			app.saveSurveyData(responseData);
			;

			System.out.println("Added: " + responseData);
			list.remove(0);
		}
		return "Successfully ADDED";
	}

}
