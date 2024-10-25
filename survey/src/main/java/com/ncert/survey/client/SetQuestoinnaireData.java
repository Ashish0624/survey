package com.ncert.survey.client;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ncert.survey.utils.StringUtils;

public class SetQuestoinnaireData {
    
    private final String url = "jdbc:postgresql://89.233.105.5:5432/ncert_ncf";
    private final String user = "ncf_meet";
    private final String password = "sdd#$hh$*&!@)BG^";
    
    
    private static final String SELECT_ALL_QUESTIONNAIRE_ID="SELECT DISTINCT questionnaire_id FROM public.master_questoinnaire where language=?";
    private static final String SELECT_ALL_OPTIONS_FOR_COUNT = "SELECT  option_1, option_2, option_3, option_4,option_5, option_6, option_7, option_8, option_9, option_10, option_11, option_12, option_13,option_14,option_15 FROM public.master_questoinnaire where questionnaire_id= ? and question_id= ? and language= ?";
    private static final String SELECT_ALL_OPTIONS= "SELECT  option_1, option_2, option_3, option_4,option_5, option_6, option_7, option_8, option_9, option_10, option_11, option_12, option_13,option_14,option_15 FROM public.master_questoinnaire where questionnaire_id = ? and question_id= ? and language= ? ";
    private static final String SELECT_ALL_QUESTION = "SELECT  question_name FROM public.master_questoinnaire where  questionnaire_id= ? and question_id = ? and language= ? ";
    private static final String SELECT_QUES_COUNT="SELECT  question_id FROM public.master_questoinnaire where  questionnaire_id= ?  and language= ?";
    private static final String SELECT_ALL_USER_TYPE="SELECT  respondent_type FROM public.master_questoinnaire where questionnaire_id= ?  and language= ?";
    private static final String SELECT_ALL_QUES_TYPE="SELECT  question_type FROM public.master_questoinnaire where questionnaire_id=? and question_id=? and language= ?";
    private static final String SELECT_SPECIFIC_DATA="SELECT  question_type, max_selectable_options, option_1, option_2, option_3, option_4, option_5, option_6, option_7, option_8, option_9, option_10, option_11, option_12, option_13, option_14, option_15, question_name, respondent_type FROM public.master_questoinnaire where questionnaire_id=? and question_id=? and language= ?"; 
    
    
    String questionName=null;
    String questionType=null;
    Integer maxSelectableOptions =1;
    ArrayList<String> options = new ArrayList<String>();
    
    //GET SPECIFIC DATA FOR JSON
    public void getData(int quesnrId, int quesId, String language) throws SQLException {
        Connection connection=null;
        
        
        try {
             connection = DriverManager.getConnection(url, user, password);
             PreparedStatement prst = connection.prepareStatement(SELECT_SPECIFIC_DATA);
                prst.setInt(1, quesnrId);
                prst.setInt(2, quesId);
                prst.setString(3,language);
                ResultSet rs = prst.executeQuery();
            while(rs.next()) {  
                
                if(StringUtils.isValidObj(rs.getString("question_name"))) {
                    questionName = rs.getString("question_name");
                }
                if(StringUtils.isValidObj(rs.getString("max_selectable_options"))) {
                    maxSelectableOptions = rs.getInt("max_selectable_options");
                }
                
                if(StringUtils.isValidObj(rs.getString("question_type"))) {
                    questionType = rs.getString("question_type");
                }
                
                
                if(StringUtils.isValidObj(rs.getString("option_1"))) {
                    options.add(rs.getString("option_1"));
                }   
                if(StringUtils.isValidObj(rs.getString("option_2"))) {
                    options.add(rs.getString("option_2"));
                }
                if(StringUtils.isValidObj(rs.getString("option_3"))) {
                    options.add(rs.getString("option_3"));
                }
                if(StringUtils.isValidObj(rs.getString("option_4"))) {
                    options.add(rs.getString("option_4"));
                }
                if(StringUtils.isValidObj(rs.getString("option_5"))) {
                    options.add(rs.getString("option_5"));
                }
                if(StringUtils.isValidObj(rs.getString("option_6"))) {
                    options.add(rs.getString("option_6"));
                }
                if(StringUtils.isValidObj(rs.getString("option_7"))) {
                    options.add(rs.getString("option_7"));
                }
                if(StringUtils.isValidObj(rs.getString("option_8"))) {
                    options.add(rs.getString("option_8"));
                }
                if(StringUtils.isValidObj(rs.getString("option_9"))) {
                    options.add(rs.getString("option_9"));
                }
                if(StringUtils.isValidObj(rs.getString("option_10"))) {
                    options.add(rs.getString("option_10"));
                }
                if(StringUtils.isValidObj(rs.getString("option_11"))) {
                    options.add(rs.getString("option_11"));
                }
                if(StringUtils.isValidObj(rs.getString("option_12"))) {
                    options.add(rs.getString("option_12"));
                }
                if(StringUtils.isValidObj(rs.getString("option_13"))) {
                    options.add(rs.getString("option_13"));
                }
                if(StringUtils.isValidObj(rs.getString("option_14"))) {
                    options.add(rs.getString("option_14"));
                }
                if(StringUtils.isValidObj(rs.getString("option_15"))) {
                    options.add(rs.getString("option_15"));
                }   
                
            }   
            
        
            connection.close();
        }
        catch(Exception e)
        {
            if(null!=connection)
            {
                connection.close();
            }
            System.out.println(e);
        }
    }
    
    //get questionnaire_id in list
    public  ArrayList<Integer> viewAllQuestionnaire(String language) throws SQLException{
        Connection connection=null;
        ArrayList<Integer> list = new ArrayList<>();
        try {
             connection = DriverManager.getConnection(url, user, password);
             PreparedStatement prst = connection.prepareStatement(SELECT_ALL_QUESTIONNAIRE_ID);
                prst.setString(1,language);
                ResultSet rs = prst.executeQuery();
            while(rs.next()) {              
                list.add(rs.getInt("questionnaire_id"));                
            }   
        
            connection.close();
        }
        catch(Exception e)
        {
            if(null!=connection)
            {
                connection.close();
            }
            System.out.println(e);
        }
        return list;
    }

    //GET OPTOINS FOR EACH QUESTION
    public ArrayList<String> getOps(int quesnrId, int quesId, String language) throws SQLException{
        
        ArrayList<String> options = new ArrayList<String>();
        Connection connection=null;
        try {
             connection = DriverManager.getConnection(url, user, password);

            PreparedStatement prst = connection.prepareStatement(SELECT_ALL_OPTIONS);
            prst.setInt(1, quesnrId);
            prst.setInt(2, quesId);
            prst.setString(3,language);
            ResultSet rs = prst.executeQuery();
            while(rs.next()) {          
                if(StringUtils.isValidObj(rs.getString("option_1"))) {
                    options.add(rs.getString("option_1"));
                }   
                if(StringUtils.isValidObj(rs.getString("option_2"))) {
                    options.add(rs.getString("option_2"));
                }
                if(StringUtils.isValidObj(rs.getString("option_3"))) {
                    options.add(rs.getString("option_3"));
                }
                if(StringUtils.isValidObj(rs.getString("option_4"))) {
                    options.add(rs.getString("option_4"));
                }
                if(StringUtils.isValidObj(rs.getString("option_5"))) {
                    options.add(rs.getString("option_5"));
                }
                if(StringUtils.isValidObj(rs.getString("option_6"))) {
                    options.add(rs.getString("option_6"));
                }
                if(StringUtils.isValidObj(rs.getString("option_7"))) {
                    options.add(rs.getString("option_7"));
                }
                if(StringUtils.isValidObj(rs.getString("option_8"))) {
                    options.add(rs.getString("option_8"));
                }
                if(StringUtils.isValidObj(rs.getString("option_9"))) {
                    options.add(rs.getString("option_9"));
                }
                if(StringUtils.isValidObj(rs.getString("option_10"))) {
                    options.add(rs.getString("option_10"));
                }
                if(StringUtils.isValidObj(rs.getString("option_11"))) {
                    options.add(rs.getString("option_11"));
                }
                if(StringUtils.isValidObj(rs.getString("option_12"))) {
                    options.add(rs.getString("option_12"));
                }
                if(StringUtils.isValidObj(rs.getString("option_13"))) {
                    options.add(rs.getString("option_13"));
                }
                if(StringUtils.isValidObj(rs.getString("option_14"))) {
                    options.add(rs.getString("option_14"));
                }
                if(StringUtils.isValidObj(rs.getString("option_15"))) {
                    options.add(rs.getString("option_15"));
                }
            }
            
            connection.close();
        }
        catch(Exception e)
        {
            if(null!=connection)
            {
                connection.close();
            }
            System.out.println(e);
        }
        
        return options;
    }
    
    //GET USER TYPE FOR QUESTIONNAIRE
        public ArrayList<String> getQuestionType(int quesnrId, int quesId, String language) throws SQLException {
                
                ArrayList<String> questionType = new ArrayList<String>();
                
                Connection connection=null;
                try {
                     connection = DriverManager.getConnection(url, user, password);
                    PreparedStatement prst = connection.prepareStatement(SELECT_ALL_QUES_TYPE);
                    prst.setInt(1, quesnrId);
                    prst.setInt(2, quesId);
                    prst.setString(3,language);
                    ResultSet rs = prst.executeQuery();
                    while(rs.next()) {          
                        if(StringUtils.isValidObj(rs.getString("question_type"))) {
                            questionType.add(rs.getString("question_type"));
                        }
                    }
                    connection.close();
                }
                catch(Exception e)
                {
                    if(null!=connection)
                    {
                        connection.close();
                    }
                    System.out.println(e);
                }
                return questionType;
        }
    
    //GET USER TYPE FOR QUESTIONNAIRE
    public ArrayList<String> getUserType(int quesnrId,  String language) throws SQLException {
            
            ArrayList<String> userType = new ArrayList<String>();
            
            Connection connection=null;
            try {
                 connection = DriverManager.getConnection(url, user, password);
                PreparedStatement prst = connection.prepareStatement(SELECT_ALL_USER_TYPE);
                prst.setInt(1, quesnrId);
                prst.setString(2,language);
                ResultSet rs = prst.executeQuery();
                while(rs.next()) {          
                    if(StringUtils.isValidObj(rs.getString("respondent_type"))) {
                        userType.add(rs.getString("respondent_type"));
                    }
                }
                connection.close();
            }
            catch(Exception e)
            {
                if(null!=connection)
                {
                    connection.close();
                }
                System.out.println(e);
            }
            return userType;
    }
    
    //COUNT OPTIONS FOR EACH QUESTION
    public int countOptionsForQues(int quesnrId, int quesId, String language) throws SQLException {
        
        ArrayList<String> options = new ArrayList<String>();
        
        int countOptoins =0;
        Connection connection=null;
        try {
             connection = DriverManager.getConnection(url, user, password);

            PreparedStatement prst = connection.prepareStatement(SELECT_ALL_OPTIONS_FOR_COUNT);
//          System.out.println(prst);
            prst.setInt(1, quesnrId);
            prst.setInt(2, quesId);
            prst.setString(3,language);
            ResultSet rs = prst.executeQuery();
            while(rs.next()) {          
                if(StringUtils.isValidObj(rs.getString("option_1"))) {
                    options.add(rs.getString("option_1"));
                }   
                if(StringUtils.isValidObj(rs.getString("option_2"))) {
                    options.add(rs.getString("option_2"));
                }
                if(StringUtils.isValidObj(rs.getString("option_3"))) {
                    options.add(rs.getString("option_3"));
                }
                if(StringUtils.isValidObj(rs.getString("option_4"))) {
                    options.add(rs.getString("option_4"));
                }
                if(StringUtils.isValidObj(rs.getString("option_5"))) {
                    options.add(rs.getString("option_5"));
                }
                if(StringUtils.isValidObj(rs.getString("option_6"))) {
                    options.add(rs.getString("option_6"));
                }
                if(StringUtils.isValidObj(rs.getString("option_7"))) {
                    options.add(rs.getString("option_7"));
                }
                if(StringUtils.isValidObj(rs.getString("option_8"))) {
                    options.add(rs.getString("option_8"));
                }
                if(StringUtils.isValidObj(rs.getString("option_9"))) {
                    options.add(rs.getString("option_9"));
                }
                if(StringUtils.isValidObj(rs.getString("option_10"))) {
                    options.add(rs.getString("option_10"));
                }
                if(StringUtils.isValidObj(rs.getString("option_11"))) {
                    options.add(rs.getString("option_11"));
                }
                if(StringUtils.isValidObj(rs.getString("option_12"))) {
                    options.add(rs.getString("option_12"));
                }
                if(StringUtils.isValidObj(rs.getString("option_13"))) {
                    options.add(rs.getString("option_13"));
                }
                if(StringUtils.isValidObj(rs.getString("option_14"))) {
                    options.add(rs.getString("option_14"));
                }
                if(StringUtils.isValidObj(rs.getString("option_15"))) {
                    options.add(rs.getString("option_15"));
                }
                
                for (int j=0; j<options.size(); j++) {
                    countOptoins++;
                }
            }
            connection.close();
        }
        catch(Exception e)
        {
            if(null!=connection)
            {
                connection.close();
            }
            System.out.println(e);
        }
        return countOptoins;
    }
    
    
    //GET QUESTION NAMES
    public ArrayList<String> getQues(int quesnrId, int quesId, String language) throws SQLException {
        Connection connection=null;
        ArrayList<String> ques = new ArrayList<String>();
        try {
             connection = DriverManager.getConnection(url, user, password);

            PreparedStatement prst = connection.prepareStatement(SELECT_ALL_QUESTION);
//          System.out.println(prst);
            prst.setInt(1, quesnrId);
            prst.setInt(2, quesId);
            prst.setString(3,language);
            ResultSet rs = prst.executeQuery();
            while(rs.next()) {          
                if(StringUtils.isValidObj(rs.getString("question_name"))) {
                    ques.add(rs.getString("question_name"));
                }   
                
            }
        
            connection.close();
        }
        catch(Exception e)
        {
            if(null!=connection)
            {
                connection.close();
            }
            System.out.println(e);
        }
        
        
        return ques;
    }
    
    //GET QUESTION COUNT
    public int getQuesCount(int quesnrId,  String language) throws SQLException {
            
            Connection connection = null;
            int countQuestion =0;
            try {
                 connection = DriverManager.getConnection(url, user, password);

                PreparedStatement prst = connection.prepareStatement(SELECT_QUES_COUNT);
//              System.out.println(prst);
                
                prst.setInt(1, quesnrId);
                prst.setString(2, language);
                ResultSet rs = prst.executeQuery();
                while(rs.next()) {          
//                  if(StringUtils.isValidObj(rs.getString("question_id"))) {
//                      ques.add(rs.getString("question_id"));
//                  }   
//                  
//                  for(int k=0;k<ques.size();k++) {
//                      countQuestion++;
//                  }   
//                  System.out.println(rs.getString("question_id"));
                    countQuestion++;
                }
                
                connection.close();
            }
            catch(Exception e)
            {
                if(null!=connection)
                {
                    connection.close();
                }
                System.out.println(e);
            }
            
            
            return countQuestion;
        }

    
    
    
    public Dictionary generateJSON(String language) throws SQLException {
        
        
        
        
        ArrayList<String> FinalJson = new ArrayList<String>();
        Dictionary dict = new Hashtable();
        
        JSONObject mainOBJ = new JSONObject();
        String json=null;
        ArrayList<Integer> questionnaireId = new SetQuestoinnaireData().viewAllQuestionnaire(language);
        
        for(int qsnr=0; qsnr<questionnaireId.size(); qsnr++) {
            
            
            System.out.println(questionnaireId.get(qsnr));
            JSONObject Questoinnaire = new JSONObject();
            
            int quesCount = new SetQuestoinnaireData().getQuesCount(questionnaireId.get(qsnr), language);
            ArrayList<String> userType= new SetQuestoinnaireData().getUserType(questionnaireId.get(qsnr), language);
            int qsn=1;
            JSONArray questoinsArr = new JSONArray();
            if(quesCount>0)
            {
                System.out.println(quesCount);
            while(qsn<=quesCount) 
            {
                getData(questionnaireId.get(qsnr), qsn, language);
                
                JSONObject questions = new JSONObject();
                int countOP=options.size();
                
//              int countOP = new SetQuestoinnaireData().countOptionsForQues(questionnaireId.get(qsnr),qsn, "en");
//              ArrayList<String> ops= new SetQuestoinnaireData().getOps(questionnaireId.get(qsnr), qsn, "en");
//              String[] quesName = new SetQuestoinnaireData().getQues(questionnaireId.get(qsnr), qsn, "en").toArray(new String[0]);
//              String[] questionType= new SetQuestoinnaireData().getQuestionType(questionnaireId.get(qsnr), qsn, "en").toArray(new String[0]);
                
                
                
                char key='A';
                JSONArray optionsArr= new JSONArray();
                if(countOP>0)
                {
                    
                    for(int op=0; op<countOP; op++) 
                    {
                        
                        JSONObject questionOptions = new JSONObject();
                        questionOptions.put("optionId", op);
                        questionOptions.put("Key", ""+key+"");
                        questionOptions.put("value", String.valueOf(options.get(op)));
                        optionsArr.add(questionOptions);
//                      System.out.println(questionOptions);
                        key++;
                    }
                }
                questions.put("questionOptions", optionsArr);
                questions.put("questionId", qsn);
                questions.put("questoinName", questionName);
                questions.put("questionType", questionType);
                questions.put("maxSelectableOptions",maxSelectableOptions);
                questoinsArr.add(questions);
                    
                qsn++;
            }
            }
            
            Questoinnaire.put("questions", questoinsArr);
            Questoinnaire.put("questionnaireId", questionnaireId.get(qsnr));
            Questoinnaire.put("userType", userType.get(qsnr));
        
            mainOBJ.put("Questionnaire", Questoinnaire);
            json = mainOBJ.toJSONString();
            
            FinalJson.add(json);
            String respondent = null;
            
            if(StringUtils.equals(language, "hin"))
            {
                respondent = "h"+userType.get(qsnr);
            }
            else {
                respondent = userType.get(qsnr);
            }
            dict.put(respondent, json);
            
            
        }
        System.out.println(FinalJson);
        
        return dict;
    }
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws SQLException {
        
        JSONObject mainOBJ = new JSONObject();
        JSONObject OBJ1 = new JSONObject();
        JSONObject OBJ2 = new JSONObject();
        JSONObject OBJ3 = new JSONObject();
        JSONArray arr1 = new JSONArray();
        JSONArray arr2 = new JSONArray();
        
        OBJ3.put("optionId", 1);
        OBJ3.put("Key", "A");
        OBJ3.put("value","Reading Book");
        
        arr2.add(OBJ3);
        
        OBJ2.put("questionId", 1);
        OBJ2.put("questionName", "How are you?");
        OBJ2.put("questionId", "Multi");
        OBJ2.put("questionId", arr2);
        
        
//      for(int i=1; i<=2;i++) {
//          
//          
//      }
        
        arr1.add(OBJ2);
        OBJ1.put("questionnaireID", 218);
        OBJ1.put("userType", "student");
        OBJ1.put("questoins", arr1);
        mainOBJ.put("questionnaire", OBJ1);
//      System.out.println(mainOBJ);
//      System.out.println(OBJ3);
//      System.out.println(OBJ2);
//      System.out.println(OBJ1);
        
//      System.out.println(new SetQuestoinnaireData().countOptionsForQues( 2, "en"));
//      System.out.println(new SetQuestoinnaireData().getQues(218, 2, "en"));
//      System.out.println(new SetQuestoinnaireData().getQuesCount(218, "en"));
        
        System.out.println(new SetQuestoinnaireData().generateJSON("en"));
        
//      SetQuestoinnaireData qsn = new SetQuestoinnaireData();
//      qsn.getData(218, 1, "en");
//      System.out.println(qsn.questionName);
//      System.out.println(qsn.questionType);
//      System.out.println(qsn.options.size());
//      
//      
    }

}
