package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.model.Datasource;
import sample.model.Expenses;

import java.time.LocalDate;
import java.util.Optional;

public class updateController {
    @FXML
    private TextField text;
    @FXML
    private TextArea area;
    @FXML
    private DatePicker date;


    @FXML
    public Expenses processResult(int id){
        text.setText(text.getText().replace(",","."));
        if(text.getText().isEmpty() || !text.getText().matches("^\\d+(.\\d{1,2})?$")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Incorrect Value");
            alert.setHeaderText("Edit Fail");
            alert.setContentText("Entered value is incorrect pls try again");
            alert.show();
            return null;
        }
        if(area.getText().isEmpty()) {
            area.setText(" ");
        }
        Datasource.getInstance().uploadExpenses(date.getValue().toString(),Double.parseDouble(text.getText()),area.getText(),id);
        Expenses expenses = new Expenses();
        expenses.set_id(id);
        expenses.setValue(Double.parseDouble(text.getText()));
        expenses.setDate(date.getValue().toString());
        expenses.setDescription(area.getText());
        return expenses;
    }
    @FXML void setData(double val, String description, String data){
        text.setText(String.valueOf(val));
        area.setText(description);
        date.setValue(LocalDate.parse(data));
    }
}
