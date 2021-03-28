package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.model.Datasource;
import sample.model.Expenses;

import java.util.Date;


public class Controller {
    @FXML
    private TextField valueTF;
    @FXML
    private TextArea descriptionTA;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<Expenses> tableView;
    @FXML
    private TableColumn<Expenses, String> columnDate;
    @FXML
    private TableColumn<Expenses, Double> columnValue;
    @FXML
    private TableColumn<Expenses, String> columnDescription;

    @FXML
    public void initialize(){
        Task<ObservableList<Expenses>> task = new Task<ObservableList<Expenses>>() {
            @Override
            protected ObservableList<Expenses> call() throws Exception {
                return FXCollections.observableArrayList(Datasource.getInstance().DataToTable());
            }
        };
        tableView.itemsProperty().bind(task.valueProperty());
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        new Thread(task).start();

    }
    @FXML
    private void addRecipe(){
        String description;
//        if(valueTF==null || datePicker==null){
//            return;
//        }
//        if(descriptionTA==null){
//            description="";
//        }else {
//            description = descriptionTA.getText();
//        }
        Datasource.getInstance().insertExpenses(datePicker.getValue().toString(),Double.parseDouble(valueTF.getText()),descriptionTA.getText());
        valueTF.clear();
        descriptionTA.clear();
        System.out.println(new Date());
    }
}
