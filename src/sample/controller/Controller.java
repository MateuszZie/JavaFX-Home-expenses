package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.model.Datasource;
import sample.model.Expenses;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


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
    private List<Expenses> expensesList;

    @FXML
    public void initialize(){
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
                Datasource.getInstance().insertExpenses(datePicker.getValue().toString(),Double.parseDouble(valueTF.getText()),descriptionTA.getText());
                Expenses expenses = new Expenses();
                expenses.setValue(Double.parseDouble(valueTF.getText()));
                expenses.setDate(datePicker.getValue().toString());
                expenses.setDescription(descriptionTA.getText());
                expensesList.add(expenses);
                expensesList.sort((Expenses e, Expenses e2 ) -> e2.getDate().compareTo(e.getDate()));
                new Thread(refresh()).start();
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
                new Thread(refresh()).start();
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
            Expenses update = controller.processResult(_id);
            if(update!=null){
                expensesList.set(_id,update);
                new Thread(refresh()).start();
                tableView.getSelectionModel().select(update);
            }
        }
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
