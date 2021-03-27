package sample.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Datasource {
    public static final String DB_NAME = "householdExpenses.db";
    public static final String CONNECTION_STRING ="jdbc:sqlite:C:\\Users\\Mateusz\\IdeaProjects\\Statement of monthly food expenses\\"+DB_NAME;
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSES_DATE = "date";
    public static final String COLUMN_EXPENSES_VALUE = "value";
    public static final String COLUMN_EXPENSES_DESCRIPTION = "description";

    private Connection conn;
    private Statement statement;

    public static Datasource datasource = new Datasource();
    private Datasource(){}
    public static Datasource getInstance(){
        return datasource;
    }
    public boolean open(){
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            statement = conn.createStatement();
//            statement.execute("drop table if exists " +TABLE_EXPENSES);
            statement.execute("create table if not exists " + TABLE_EXPENSES +" ("+COLUMN_EXPENSES_DATE +" date, " +COLUMN_EXPENSES_VALUE +" double, "
                    + COLUMN_EXPENSES_DESCRIPTION +" text)");
//            statement.execute("insert into " +TABLE_EXPENSES+ " ("+COLUMN_EXPENSES_DATE+","+COLUMN_EXPENSES_VALUE+","+COLUMN_EXPENSES_DESCRIPTION+")"+
//                    " values ('2010-01-01', 34.21, 'firstGrocery')");
            return true;
        }catch (SQLException e){
            System.out.println("Can't connect to DB " +e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public void close(){
        try {
            if(statement!=null){
                statement.close();
            }
            if(conn!=null){
                conn.close();
            }
        }catch (SQLException e){
            System.out.println("Can't close " +e.getMessage());
        }
    }
}
