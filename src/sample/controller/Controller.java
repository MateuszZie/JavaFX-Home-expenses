package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import sample.model.Datasource;
import sample.model.Expenses;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


public class Controller {
    @FXML
    private SplitPane splitPane;
    @FXML
    private TextField valueTF;
    @FXML
    private TextArea descriptionTA;
    @FXML
    private DatePicker datePicker;
    @FXML
    private DatePicker nextDate;
    @FXML
    private DatePicker lastDate;
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
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private Label lastMonth;
    @FXML
    private Label thisMonth;
    @FXML
    private Label sum;
    private List<Expenses> expensesList;
    private List<Expenses> periodList;

    @FXML
    public void initialize(){
        valueTF.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    addRecipe();
                }
            }
        });
        contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem updateItem = new MenuItem("Edit");
        deleteItem.setOnAction((e)-> deleteRecipe());
        updateItem.setOnAction((e)-> updateRecipe());
        contextMenu.getItems().addAll(updateItem,deleteItem);
        tableView.setContextMenu(contextMenu);
        datePicker.setValue(LocalDate.now());
        expensesList = Datasource.getInstance().DataToTable();
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        period();

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
                Datasource.getInstance().insertExpenses(datePicker.getValue().toString(),Double.parseDouble(valueTF.getText()),descriptionTA.getText());
                Expenses expenses = new Expenses();
                if(!expensesList.isEmpty()){
                    expenses.set_id((Collections.max(expensesList).get_id()+1));
                }else {
                    expenses.set_id(1);
                }
                expenses.setValue(Double.parseDouble(valueTF.getText()));
                expenses.setDate(datePicker.getValue().toString());
                expenses.setDescription(descriptionTA.getText());
                expensesList.add(expenses);
                expensesList.sort((Expenses e, Expenses e2 ) -> e2.getDate().compareTo(e.getDate()));
                period();
                valueTF.clear();
                descriptionTA.clear();
                incorrectVal.setVisible(false);
                tableView.getSelectionModel().select(expenses);
        }
        public void deleteRecipe() {
            Expenses expenses = tableView.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Recipe");
            alert.setHeaderText("Are you sure??");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Datasource.getInstance().deleteRecipe(expenses.get_id());
                expensesList.remove(expenses);
                period();
            }
        }
    @FXML
    public void updateRecipe(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(splitPane.getScene().getWindow());
        dialog.setTitle("Edit Recipe");
        dialog.setHeaderText("Edit");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/update.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        }catch (IOException e){
            System.out.println("Couldn't load dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        updateController controller = loader.getController();
        Expenses expenses = tableView.getSelectionModel().getSelectedItem();
        controller.setData(expenses.getValue(),expenses.getDescription(),expenses.getDate());
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){

            int _id = expensesList.indexOf(expenses);
            Expenses update = controller.processResult(expenses.get_id());
            if(update!=null){
                expensesList.set(_id,update);
                period();
                tableView.getSelectionModel().select(update);
            }
        }
    }


    private Task<ObservableList<Expenses>> refresh(List<Expenses> expenses){
        Task<ObservableList<Expenses>> task = new Task<ObservableList<Expenses>>() {
            @Override
            protected ObservableList<Expenses> call() throws Exception {
                return FXCollections.observableArrayList(expenses);
            }
        };
        tableView.itemsProperty().bind(task.valueProperty());
        setMonthValue();
        return task;
    }
    private void setMonthValue(){
        String thisM = LocalDate.now().toString().substring(0,7);
        String lastM = LocalDate.now().minusMonths(1).toString().substring(0,7);
        double last = 0;
        double now = 0;
        for (Expenses expenses: expensesList){
            if(expenses.getDate().substring(0,7).equals(thisM)){
                now += expenses.getValue();
            }
            if(expenses.getDate().substring(0,7).equals(lastM)){
                last += expenses.getValue();
            }
        }
        lastMonth.setText(String.valueOf(last));
        thisMonth.setText(String.valueOf(now));
        if(now>last){
            thisMonth.setTextFill(Color.RED);
        }else thisMonth.setTextFill(Color.GREEN);
    }
    @FXML
    private void period(){
        double sumVal=0;
        if(lastDate.getValue()==null && nextDate.getValue()==null){
            sum.setVisible(false);
            new Thread(refresh(expensesList)).start();
            return;
        }
        periodList=new ArrayList<>();
        sum.setVisible(true);
        if(nextDate.getValue()==null){
            for (Expenses expenses: expensesList){
                if(expenses.getDate().compareTo(lastDate.getValue().toString())>=0){
                    periodList.add(expenses);
                    sumVal += expenses.getValue();
                }
            }
        }else if(lastDate.getValue()==null) {
            for (Expenses expenses : expensesList) {
                if (expenses.getDate().compareTo(nextDate.getValue().toString()) <= 0) {
                    periodList.add(expenses);
                    sumVal += expenses.getValue();
                }
            }
        }else {
            for (Expenses expenses : expensesList) {
                if (expenses.getDate().compareTo(nextDate.getValue().toString()) <= 0 && expenses.getDate().compareTo(lastDate.getValue().toString()) >= 0) {
                    periodList.add(expenses);
                    sumVal += expenses.getValue();
                }

            }
        }
            sum.setText(String.valueOf(sumVal));
            new Thread(refresh(periodList)).start();
    }
}
