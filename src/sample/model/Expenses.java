package sample.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class Expenses implements Comparable<Expenses>{
    private SimpleIntegerProperty _id;
    private SimpleDoubleProperty value;
    private SimpleStringProperty description;
    private SimpleStringProperty date;

    public Expenses() {
        this._id= new SimpleIntegerProperty();
        this.value = new SimpleDoubleProperty();
        this.description = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
    }

    @Override
    public int compareTo(Expenses o) {
        return Integer.compare(this.get_id(),o.get_id());
    }

    public int get_id() {
        return _id.get();
    }

    public SimpleIntegerProperty _idProperty() {
        return _id;
    }

    public void set_id(int _id) {
        this._id.set(_id);
    }

    public double getValue() {
        return value.get();
    }

    public SimpleDoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }
}
