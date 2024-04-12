package com.example.reddit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("StartUP.fxml")));
        stage.setScene(new Scene(root));
        Image icon = new Image(Main.class.getResourceAsStream("icon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Reddit");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void jsonToList(JSONArray jsonArray , ArrayList<String> list){
        for(int i = 0 ; i < jsonArray.length() ; i++){
            list.add(jsonArray.getString(i));
        }
    }
}