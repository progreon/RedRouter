/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.util;

import java.io.IOException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

/**
 * TODO: no FXML for performance?
 *
 * @author Marco Willems
 */
public class LabeledSpinner extends HBox {

    private final SpinnerValueFactory.IntegerSpinnerValueFactory svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0, 1);

//    @FXML
//    protected Label label;
    protected Label label = new Label();
//    @FXML
//    protected Spinner spinner;
    protected Spinner spinner = new Spinner(svf);

    public LabeledSpinner() {
        getStyleClass().add("labeled-spinner");
        setAlignment(Pos.CENTER_RIGHT);
        label.setPadding(new Insets(0, 3, 0, 0));
        getChildren().add(label);
        spinner.setEditable(true);
        spinner.setPrefWidth(60);
        getChildren().add(spinner);

//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LabeledSpinner.fxml"));
//        fxmlLoader.setRoot(this);
//        fxmlLoader.setController(this);
//        try {
//            fxmlLoader.load();
//        } catch (IOException exception) {
//            throw new RuntimeException(exception);
//        }
//        label.setPadding(new Insets(0, 3, 0, 0));
//        spinner.setValueFactory(svf);
    }
    
    public LabeledSpinner(String text, int min, int max, int value) {
        this();
        setText(text);
        setMin(min);
        setMax(max);
        setValue(value);
    }

    public StringProperty textProperty() {
        return label.textProperty();
    }

    public String getText() {
        return label.getText();
    }

    public final void setText(String text) {
        label.setText(text);
    }

    public ObjectProperty<Integer> valueProperty() {
        return svf.valueProperty();
    }

    public Integer getValue() {
        return svf.getValue();
    }

    public final void setValue(Integer value) {
        if (value < svf.getMin()) {
            value = svf.getMin();
        }
        if (value > svf.getMax()) {
            value = svf.getMax();
        }
        svf.setValue(value);
    }

    public IntegerProperty minProperty() {
        return svf.minProperty();
    }

    public int getMin() {
        return svf.getMin();
    }

    public final void setMin(int min) {
        svf.setMin(min);
    }

    public IntegerProperty maxProperty() {
        return svf.maxProperty();
    }

    public int getMax() {
        return svf.getMax();
    }

    public final void setMax(int max) {
        svf.setMax(max);
    }

    public void addListener(SpinnerChangedListener listener) {
        svf.valueProperty().addListener(new ChangeListener<Integer>() {

            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                listener.changed(LabeledSpinner.this, oldValue, newValue);
            }
        });
    }

    public static interface SpinnerChangedListener {

        public void changed(LabeledSpinner source, int oldValue, int newValue);

    }

}
