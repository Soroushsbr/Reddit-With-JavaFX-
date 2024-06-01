package com.example.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUP {
    @FXML
    TextField userField;
    @FXML
    PasswordField passField;
    @FXML
    TextField emailField;
    @FXML
    Label validEmail;
    @FXML
    Label validUser;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void signUp(ActionEvent event) throws IOException {
        String username = userField.getText();
        String password = passField.getText();
        String email = emailField.getText();
        boolean flagUser = validUsername(username);
        boolean flagEmail = validEmail(email);
        if(flagEmail && flagUser){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Timeline.fxml"));
            root = loader.load();
            Account account = new Account(username , Integer.toString(password.hashCode()) , email);
            account.idMaker();
            Files files = new Files();
            files.fileWriter("Account" , (account.toJson()).toString() , 0 , true);
            Timeline timeline = loader.getController();
            timeline.setUser(account.toJson());
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void login(ActionEvent event) throws IOException{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("StartUp.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validEmail(String email){
        Files files = new Files();
        ArrayList<String> accountList = new ArrayList<>();
        files.fileReader(accountList , "Account" , 0);
        String regex = "[^\\s]*@[^\\s]*.[^\\s]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        boolean flag = true;
        boolean find = matcher.find();
        if (find) {//if email is valid we have to check if it's unique
            if (!(accountList.isEmpty())) {
                for (int i = 0; i < accountList.size(); i++) {
                    JSONObject jsonObject = new JSONObject(accountList.get(i));
                    String emailList = (jsonObject.getString("Email"));
                    validEmail.setText(" ");
                    if (emailList.equals(email)) {
                        flag = false;
                        validEmail.setText("Already Exist");
                        break;
                    }
                }
            }
        }else{
            validEmail.setText("x");
        }
        return (flag && find);
    }

    public boolean validUsername(String username){
        Files files = new Files();
        if(files.usernameFind(username) == null){
            validUser.setText(" ");
            return true;
        }else{
            validUser.setText("Already Taken");
            return false;
        }
    }

}
