package com.example.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StartUp {
    @FXML
    TextField userField;
    @FXML
    PasswordField passField;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void login(ActionEvent event) throws IOException {
        userField.setStyle("-fx-background-radius: 50;-fx-background-color: #44475A;-fx-text-fill: white");
        String username = userField.getText();          //entered username
        String password = passField.getText();
        password = Integer.toString(password.hashCode());
        Files files = new Files();
        if(files.usernameFind(username) != null){
            userField.setStyle("-fx-background-radius: 50;-fx-background-color: #44475A;-fx-text-fill: white");
            JSONObject user = new JSONObject(files.usernameFind(username));
            if(user.getString("Password").equals(password)){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Timeline.fxml"));
                root = loader.load();
                Timeline timeline= loader.getController();
                timeline.setUser(user);
                stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }else{
                passField.setStyle("-fx-background-radius: 50;-fx-background-color: #44475A;-fx-text-fill: white;-fx-border-color: red; -fx-border-radius: 50");
            }
        }else{
            userField.setStyle("-fx-background-radius: 50;-fx-background-color: #44475A;-fx-text-fill: white;-fx-border-color: red; -fx-border-radius: 50");
        }

    }

    public void signUp(ActionEvent event) throws IOException{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("SignUp.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
