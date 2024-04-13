package com.example.reddit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

public class AccountMenu {
    @FXML
    private Label username;
    @FXML
    private Label displayname;
    @FXML
    private Label karma;
    @FXML
    AnchorPane newPostPane;
    @FXML
    ScrollPane scrollPanePosts;
    @FXML
    Label about;
    private Stage stage;
    private Scene scene;
    private Parent root;
    JSONObject user = new JSONObject();

    public void setUser(JSONObject user){
        this.user = user;
        meun();
    }
    public void meun(){
        displayname.setText(user.getString("Display Name"));
        username.setText("u/" + user.getString("Username"));
        karma.setText("Karma: " + user.getInt("Karma"));
        about.setText(user.getString("About"));
    }

    @FXML
    TextField textField;
    @FXML
    TextArea textArea;

    public void newPost(){
        System.out.println("new post panel");
        followPane.setVisible(false);
        buttonView.setVisible(false);
        scrollPanePosts.setVisible(false);
        subPane.setVisible(false);
        newPostPane.setVisible(true);
        showBox();
    }

    public void post(){
        String subreddit = comboBox.getValue();
        String title = textField.getText();
        String text = textArea.getText();
        String postId = (UUID.randomUUID()).toString();            //to make every post unique
        JSONObject json = new JSONObject();
        json.put("Post", text);
        json.put("Comment", new ArrayList<>());
        json.put("Title", title);
        json.put("Karma", 0);
        json.put("Username", user.getString("Username"));
        json.put("Subreddit", subreddit);
        json.put("ID" , postId);
        json.put("Upvote" , new ArrayList<>());
        json.put("Downvote" , new ArrayList<>());
        Files file = new Files();
        file.fileWriter(user.getString("Username"), json.toString(), 1, true);
        file.fileWriter(subreddit , json.toString(), 3 , true );
        file.fileWriter("Explore" , json.toString(), 2 , true);
        newPostPane.setVisible(false);
        System.out.println("new post has made");
    }
    @FXML
    VBox vboxPost;

    public void posts(){
        System.out.println("your posts showed");
        followPane.setVisible(false);
        buttonView.setVisible(false);
        newPostPane.setVisible(false);
        subPane.setVisible(false);
        scrollPanePosts.setVisible(true);
        Files file = new Files();
        ArrayList<String> posts = new ArrayList<>();
        file.fileReader(posts , user.getString("Username") , 1);
        vboxPost.getChildren().clear();
        for(int i = posts.size() -1 ; i >= 0 ; i--) {
            try {           //here it shows your posts
                JSONObject jsonPost = new JSONObject(posts.get(i));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PostsAccount.fxml"));
                AnchorPane anchorPane = loader.load();
                ((Label) anchorPane.getChildren().get(0)).setText("r/" + jsonPost.getString("Subreddit"));
                ((Label) anchorPane.getChildren().get(1)).setText(jsonPost.getString("Title"));
                ((Label) anchorPane.getChildren().get(2)).setText(jsonPost.getString("Post"));
                ((Label) anchorPane.getChildren().get(3)).setText(user.toString());
                ((Button) anchorPane.getChildren().get(4)).setText(jsonPost.toString());
                vboxPost.getChildren().add(anchorPane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    Button selectButton;
    @FXML
    Label userLabel;

    public void timeline(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Timeline.fxml"));
        root = loader.load();
        Timeline timeline= loader.getController();
        timeline.setUser(user);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void logout(ActionEvent event) throws IOException{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("StartUp.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    ListView<String> followList;
    @FXML
    ScrollPane followPane;
    private String selectedUserName;
    @FXML
    Button buttonView;
    public void following(){
        System.out.println("Following list showed");
        newPostPane.setVisible(false);
        subPane.setVisible(false);
        scrollPanePosts.setVisible(false);
        followPane.setVisible(true);
        buttonView.setVisible(true);
        ArrayList<String> followingList = new ArrayList<>();
        Main.jsonToList(user.getJSONArray("Followings") , followingList);
        followList.getItems().clear();
        followList.getItems().addAll(followingList);
        followList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedUserName = newValue;        //here it saves the selected User
                }
        );
    }

    public void follower(){
        System.out.println("follower panel showed");
        newPostPane.setVisible(false);
        subPane.setVisible(false);
        scrollPanePosts.setVisible(false);
        followPane.setVisible(true);
        buttonView.setVisible(true);
        ArrayList<String> followerList = new ArrayList<>();
        Main.jsonToList(user.getJSONArray("Followers") , followerList);
        followList.getItems().clear();
        followList.getItems().addAll(followerList);
        followList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                   selectedUserName = newValue;
                }
        );
    }

    public void selectUsername(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Profile.fxml"));
        root = loader.load();
        Profile profile = loader.getController();
        Files files = new Files();
        JSONObject selectedUser = new JSONObject(files.usernameFind(selectedUserName));     //that selected User is being used here
        profile.setInfo(selectedUser , "SelectPost.fxml" , user);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    ComboBox<String> comboBox;

    public void showBox(){
        comboBox.getItems().clear();
        ArrayList<String> subs =new ArrayList<>() ;
        Main.jsonToList(user.getJSONArray("Joined Subreddit") , subs);      //add joined subreddit to a combo box to choose for making a new post
        comboBox.getItems().addAll(subs);
    }

    @FXML
    TextField subName;
    @FXML
    TextArea subAbout;
    @FXML
    ListView<String> subs;
    @FXML
    AnchorPane subPane;
    @FXML
    Label notValid;
    public void subreddit(){
        System.out.println("subreddit Panel showed");
        followPane.setVisible(false);
        buttonView.setVisible(false);
        newPostPane.setVisible(false);
        scrollPanePosts.setVisible(false);
        subPane.setVisible(true);
        ArrayList<String> admin = new ArrayList<>();
        Main.jsonToList(user.getJSONArray("Admin Subreddit") , admin);
        subs.getItems().clear();
        subs.getItems().addAll(admin);
    }

    public void newSubreddit(){
        Files files = new Files();
        String name = subName.getText();
        if(files.subredditFind(name) == null){      //check if the name for subreddit is unique or not
            notValid.setVisible(false);
            String about = subAbout.getText();
            JSONObject json = new JSONObject();
            ArrayList<String> admins = new ArrayList<>();
            admins.add(user.getString("Username"));
            ArrayList<String> members = new ArrayList<>();
            members.add(user.getString("Username"));
            json.put("Name", name);
            json.put("Members" , members);
            json.put("Admins" , admins);
            json.put("About", about);
            JSONArray temp = user.getJSONArray("Admin Subreddit");
            temp.put(name);
            user.put("Admin Subreddit" , temp);
            temp = user.getJSONArray("Joined Subreddit");
            temp.put(name);
            user.put("Joined Subreddit" , temp);
            files.profileSave(user);
            files.fileWriter("Subreddit" , json.toString(), 2 , true );
            System.out.println("new Subreddit made");
            subPane.setVisible(false);
        }else{
            notValid.setVisible(true);
        }
    }

    @FXML
    TextField newNameField;
    @FXML
    TextField newPassField;
    @FXML
    AnchorPane settingPane;
    @FXML
    Rectangle backgroundSetting;
    public void showSetting(){
        System.out.println("Setting Panel showed");
        backgroundSetting.setVisible(true);
        settingPane.setVisible(true);
    }

    public void  hideSetting(){
        System.out.println("Setting Panel hide");
        settingPane.setVisible(false);
        backgroundSetting.setVisible(false);
    }

    public void savePass(){
        String newPass = newPassField.getText();
        newPass = String.valueOf(newPass.hashCode());
        user.put("Password" , newPass);
        Files files = new Files();
        files.profileSave(user);
    }
    public void saveName(){
        String newName = newNameField.getText();
        user.put("Display Name" , newName);
        Files files = new Files();
        files.profileSave(user);
        setUser(user);
    }
    @FXML
    TextArea aboutField;
    public void saveAbout(){
        String newAbout = aboutField.getText();
        user.put("About" , newAbout);
        Files files = new Files();
        files.profileSave(user);
        setUser(user);
    }

    public void deleteAccount(ActionEvent event) throws IOException {
        Account account = new Account();
        account.deleteAccount(user);
        logout(event);
    }


}
