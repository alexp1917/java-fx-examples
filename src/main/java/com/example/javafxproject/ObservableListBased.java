package com.example.javafxproject;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Data;

import java.util.function.Supplier;


/**
 * based on:
 * https://camposha.info/javafx-examples/javafx-listview-crud-add-update-delete/
 */
public class ObservableListBased extends Application {
    /**
     * ObservableList. Our data source.
     */
    private final ObservableList<Teacher> spiritualTeachers =
            FXCollections.observableArrayList(
                    new Teacher("Sedata"),
                    new Teacher("Meister Eckhart"),
                    new Teacher("Confucius"),
                    new Teacher("Rumi"),
                    new Teacher("Ramana Maharshi"));

    /**
     * Main method
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * JavaFx Start Method
     */
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("JavaFX ListView CRUD - ADD UPDATE DELETE CLEAR");
        stage.setWidth(450);
        stage.setHeight(550);

        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ESCAPE)
                stage.close();
        });

        Label nameLabel = new Label("Teachers");
        nameLabel.setFont(new Font("Arial", 20));

        TextField nameTxt = new TextField();
        nameTxt.setPromptText("Name");

        ListView<Teacher> myListView = new ListView<>();
        Supplier<Integer> getSelectedIndex = () -> myListView.getSelectionModel()
                .getSelectedIndices()
                .stream().findAny().orElse(null);
        myListView.setItems(spiritualTeachers);

        myListView.setOnMouseClicked(event -> {
            Teacher currentTeacher = myListView.getSelectionModel().getSelectedItem();
            nameTxt.setText(currentTeacher.getName());
        });

        Button addButton = new Button("Add");
        addButton.setOnAction((ActionEvent e) -> {
            spiritualTeachers.add(new Teacher(nameTxt.getText()));
            nameTxt.clear();
        });
        Button updateBtn = new Button("Update");
        updateBtn.setOnAction((ActionEvent e) -> {
            Integer index = getSelectedIndex.get();
            if (index != null) {
                Dialog<?> d = new Alert(Alert.AlertType.INFORMATION,
                        "you've just edited: " + index);
                d.show();
                spiritualTeachers.get(index).setName(nameTxt.getText());
            }

            // edit approach
            // spiritualTeachers.get(selectedIndex).setName(nameTxt.getText());

            myListView.refresh();
            nameTxt.requestFocus();

            /*
            replace approach:
            spiritualTeachers.remove(selectedIndex);
            spiritualTeachers.add(selectedIndex, new Teacher(nameTxt.getText()));
            */
            nameTxt.clear();
        });
        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction((ActionEvent e) -> {
            Integer integer = getSelectedIndex.get();
            if (integer != null)
                spiritualTeachers.remove(integer.intValue());
            nameTxt.clear();
        });
        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction((ActionEvent e) -> {
            spiritualTeachers.clear();
            nameTxt.clear();
        });

        HBox myHBox = new HBox();
        myHBox.getChildren().addAll(nameTxt, addButton, updateBtn, deleteBtn, clearBtn);
        myHBox.setSpacing(3);

        VBox myVBox = new VBox();
        myVBox.setSpacing(5);
        myVBox.setPadding(new Insets(10, 0, 0, 10));
        myVBox.getChildren().addAll(nameLabel, myListView, myHBox);

        ((Group) scene.getRoot()).getChildren().addAll(myVBox);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Our data Object
     */
    @Data
    public static class Teacher {
        // private final SimpleStringProperty name;
        private String name;

        private Teacher(String name) {
            this.name = name;
        }
    }
}