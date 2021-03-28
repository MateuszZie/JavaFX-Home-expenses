package sample.model;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {
    public static final String DB_NAME = "householdExpenses.db";
    public static final String CONNECTION_STRING ="jdbc:sqlite:C:\\Users\\Mateusz\\IdeaProjects\\Statement of monthly food expenses\\"+DB_NAME;
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSES_DATE = "date";
    public static final String COLUMN_EXPENSES_VALUE = "value";
    public static final String COLUMN_EXPENSES_DESCRIPTION = "description";

    public static final String INSERT_INTO_EXPENSES = "insert into " + TABLE_EXPENSES + " ("+COLUMN_EXPENSES_DATE+", "+COLUMN_EXPENSES_VALUE+", "+COLUMN_EXPENSES_DESCRIPTION+
            ") values (?, ? ,?)";

    public static final String QUERY_ALL = "Select * from " +TABLE_EXPENSES + " order by " +COLUMN_EXPENSES_DATE;

    private Connection conn;
    private Statement statement;
    private PreparedStatement insertIntoExpenses;

    public static Datasource datasource = new Datasource();
    private Datasource(){}
    public static Datasource getInstance(){
        return datasource;
    }
    public boolean open(){
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            statement = conn.createStatement();
            statement.execute("create table if not exists " + TABLE_EXPENSES +" ("+COLUMN_EXPENSES_DATE +" date, " +COLUMN_EXPENSES_VALUE +" double, "
                    + COLUMN_EXPENSES_DESCRIPTION +" text)");
            insertIntoExpenses = conn.prepareStatement(INSERT_INTO_EXPENSES);
            return true;
        }catch (SQLException e){
            System.out.println("Can't connect to DB " +e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public void close(){
        try {
            if(insertIntoExpenses!=null){
                insertIntoExpenses.close();
            }
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
    public List<Expenses> DataToTable(){
        try (Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(QUERY_ALL)){
        List<Expenses> expenses = new ArrayList<>();
        while (result.next()){
            Expenses exp = new Expenses();
            exp.setDate(result.getString(COLUMN_EXPENSES_DATE));
            exp.setValue(result.getDouble(COLUMN_EXPENSES_VALUE));
            exp.setDescription(result.getString(COLUMN_EXPENSES_DESCRIPTION));
            expenses.add(exp);
        }
        return expenses;

        }catch (SQLException e){
            System.out.println("Load data to table view error " +e.getMessage());
            return null;
        }

    }
    public void insertExpenses(String date, double value, String description){
        try {
            insertIntoExpenses.setString(1,date);
            insertIntoExpenses.setDouble(2,value);
            insertIntoExpenses.setString(3,description);
            insertIntoExpenses.execute();
        }catch (SQLException e){
            System.out.println("Insert Expenses exception " + e.getMessage());
        }

    }
}
