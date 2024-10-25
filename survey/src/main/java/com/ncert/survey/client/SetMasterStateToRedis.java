package com.ncert.survey.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.ncert.survey.model.MstrState;

public class SetMasterStateToRedis
{
    private static RedissonClient redisson;
    private final String          url                     = "jdbc:postgresql://89.233.105.90:5434/ncert_ncf";
    private final String          user                    = "ncf_meet";
    private final String          password                = "sdd#$hh$*&!@)BG^";
    private static final String   SELECT_ALL_MASTER_STATE = "SELECT * FROM public.master_state ORDER BY state_id ASC ";
    public static void main( String[] args )
    {
        try
        {
            Config config = new Config();
            // use single Redis server
            config.useSingleServer().setAddress( "redis://89.233.105.90:7145" ).setPassword( "fh2KR*@JqLP1!~!J9)" );
            redisson = Redisson.create( config );
            System.out.println( "Time: " + Calendar.getInstance().getTime() );
            new SetMasterStateToRedis().saveSurveyData();
            //			viewall();
            redisson.shutdown();
            System.out.println( "Exit" );
        }
        catch ( Exception e )
        {
            redisson.shutdown();
            e.printStackTrace();
        }
    }

    public static void viewall()
    {
        RMap<Integer, MstrState> mStates = redisson.getMap( "mStates" );
        // mStates.getAll(mStates.keySet());
        for ( int i = 0; i < 36; i++ )
        {
            MstrState temp = mStates.get( i + 1 );
            System.out.println( temp.toString() );
        }
    }

    public void saveSurveyData()
        throws SQLException
    {
        // Step 1: Establishing a Connection
        try
        {
            Connection connection = DriverManager.getConnection( url, user, password );
            // Step 2:Create a statement using connection object
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( SELECT_ALL_MASTER_STATE );
            ArrayList<MstrState> list = new ArrayList<>();
            while ( rs.next() )
            {
                MstrState temp = new MstrState();
                temp.setStateId( rs.getInt( "state_id" ) );
                temp.setStateName( rs.getString( "state_name" ) );
                temp.setStateCode( rs.getString( "state_code" ) );
                temp.setCreatedTime( rs.getDate( "created_time" ) );
                temp.setModifiedTime( rs.getDate( "modified_time" ) );
                temp.setCreatedBy( rs.getString( "created_by" ) );
                temp.setModifiedBy( rs.getString( "modified_by" ) );
                temp.setStatus( rs.getString( "status" ) );
                temp.setStateUt( rs.getString( "state_ut" ) );
                System.out.println( temp.toString() );
                list.add( temp );
            }
            redisGet( list );
            // Step 3: Execute the query or update query
        }
        catch ( SQLException e )
        {
            // print SQL exception information
            e.printStackTrace();
        }
        // Step 4: try-with-resource statement will auto close the connection.
    }

    public String redisGet( ArrayList<MstrState> list )
        throws SQLException
    {
        System.out.println( "Calling redisPut " );
        //RList<SurveyData> list = redisson.getList("mStates");
        RMap<Integer, MstrState> mStates = redisson.getMap( "mStates" );
        int n = list.size();
        System.out.println( "List Size: " + n );
        for ( int i = 0; i < list.size(); i++ )
        {
            MstrState temp = list.get( i );
            mStates.put( temp.getStateId(), temp );
        }
        return "Successfully ADDED";
    }
}
