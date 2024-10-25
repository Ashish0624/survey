package com.ncert.survey.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.ncert.survey.model.MstrRespondent;
import com.ncert.survey.model.MstrState;

public class SetMasterRespondentToRedis {
	private static RedissonClient redisson;
	private final String url = "jdbc:postgresql://89.233.105.5:5432/ncert_ncf";
	private final String user = "ncf_meet";
	private final String password = "sdd#$hh$*&!@)BG^";
	private static final String SELECT_ALL_MASTER_RESPONDENT = "SELECT * FROM public.master_respondent";

	public static void main(String[] args) {
		try {
			Config config = new Config();
			// use single Redis server
			config.useSingleServer().setAddress("redis://89.233.105.5:7145").setPassword("fh2KR*@JqLP1!~!J9)");
			redisson = Redisson.create(config);
			System.out.println("Time: " + Calendar.getInstance().getTime());
			new SetMasterRespondentToRedis().saveRespondentData();
			// viewall();
			redisson.shutdown();
			System.out.println("Exit");
		} catch (Exception e) {
			e.printStackTrace();

			redisson.shutdown();
		}
	}

	public static void viewall() {
		RMap<Integer, MstrState> mStates = redisson.getMap("mStates");
		// mStates.getAll(mStates.keySet());
		for (int i = 0; i < 36; i++) {
			MstrState temp = mStates.get(i + 1);
			System.out.println(temp.toString());
		}
	}

	public void saveRespondentData() throws SQLException {
		// Step 1: Establishing a Connection
		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			// Step 2:Create a statement using connection object
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_ALL_MASTER_RESPONDENT);

			ArrayList<MstrRespondent> list = new ArrayList<>();

			while (rs.next()) {
				MstrRespondent temp = new MstrRespondent();
				temp.setRespondentTypeId(rs.getInt("respondent_type_id"));
				temp.setRespondentTypeEn(rs.getString("respondent_type_en"));
				temp.setRespondentTypeHin(rs.getString("respondent_type_hin"));

				temp.setCreatedTime(rs.getDate("created_time"));
				temp.setModifiedTime(rs.getDate("modified_time"));
				temp.setCreatedBy(rs.getString("created_by"));
				temp.setModifiedBy(rs.getString("modified_by"));
				temp.setStatus(rs.getByte("status"));
				System.out.println(temp.toString());

				list.add(temp);
			}
			redisGet(list);
			// Step 3: Execute the query or update query
		} catch (SQLException e) {

			// print SQL exception information
			e.printStackTrace();
		}

		// Step 4: try-with-resource statement will auto close the connection.
	}

	public String redisGet(ArrayList<MstrRespondent> list) throws SQLException {
		System.out.println("Calling redisPut ");
		// RList<SurveyData> list = redisson.getList("mStates");
		RMap<Integer, MstrRespondent> mStates = redisson.getMap("mRespondent");
		int n = list.size();
		System.out.println("List Size: " + n);
		for (int i = 0; i < list.size(); i++) {
			MstrRespondent temp = list.get(i);
			System.out.println("Respondent: " + temp);
			mStates.put(temp.getRespondentTypeId(), temp);
		}
		return "Successfully ADDED";
	}
}
