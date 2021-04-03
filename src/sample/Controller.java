package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import sample.model.Datasource;
import sample.model.Expenses;

import java.time.LocalDate;
import java.util.List;


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
    private Label incorrectVal;
    private List<Expenses> expensesList;

    @FXML
    public void initialize(){
        String test = "2011-02-31";
        System.out.println(test.matches("^\\d{4}-[0-1]\\d-[0-3]\\d$"));
        datePicker.setValue(LocalDate.now());
        expensesList = Datasource.getInstance().DataToTable();
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        new Thread(refresh()).start();

    }
    @FXML
    private void addRecipe(){
        valueTF.setText(valueTF.getText().replace(",","."));
        if(valueTF.getText().isEmpty()|| !valueTF.getText().matches("^\\d+(.\\d{1,2})?$")) {
            incorrectVal.setText("Incorrect \nValue");
            incorrectVal.setVisible(true);
            return;
        }
        if(descriptionTA.getText().isEmpty()) {
            descriptionTA.setText(" ");
        }
                String description;
                Datasource.getInstance().insertExpenses(datePicker.getValue().toString(),Double.parseDouble(valueTF.getText()),descriptionTA.getText());
                Expenses expenses = new Expenses();
                expenses.setValue(Double.parseDouble(valueTF.getText()));
                expenses.setDate(datePicker.getValue().toString());
                expenses.setDescription(descriptionTA.getText());
                expensesList.add(expenses);
                expensesList.sort((Expenses e, Expenses e2 ) -> e2.getDate().compareTo(e.getDate()));
                new Thread(refresh()).start();
                tableView.refresh();
                valueTF.clear();
                descriptionTA.clear();
                incorrectVal.setVisible(false);
        }
        public void deleteRecipe(){
            Expenses expenses = tableView.getSelectionModel().getSelectedItem();
            Datasource.getInstance().deleteRecipe(expenses.get_id());
        }
    private Task<ObservableList<Expenses>> refresh(){
        Task<ObservableList<Expenses>> task = new Task<ObservableList<Expenses>>() {
            @Override
            protected ObservableList<Expenses> call() throws Exception {
                return FXCollections.observableArrayList(expensesList);
            }
        };
        tableView.itemsProperty().bind(task.valueProperty());
        return task;
    }
}
