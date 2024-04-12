package com.example.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Profile {
    @FXML
    private Label displayname;
    @FXML
    private Label username;
    @FXML
    private Label about;
    @FXML
    private Label follower;
    @FXML
    private Label following;
    @FXML
    private Label karma;
    private JSONObject user;
    private JSONObject selectedUser;
    private String perScene;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void setInfo(JSONObject selectedUser , String perScene ,JSONObject user ){       //here it set all the information about the profile
        displayname.setText(selectedUser.getString("Display Name"));
        username.setText("u/" + selectedUser.getString("Username"));
        about.setText(selectedUser.getString("About"));
        follower.setText(String.valueOf(selectedUser.getJSONArray("Followers").length()));
        following.setText(String.valueOf(selectedUser.getJSONArray("Followings").length()));
        karma.setText(String.valueOf(selectedUser.getInt("Karma")));
        this.perScene = perScene;
        this.user = user;
        this.selectedUser = selectedUser;
        validateFollow();
        if(user.getString("Username").equals(selectedUser.getString("Username"))){      //checks if the user and profile have same name or not if they do it doesn't show the follow button
            follow.setVisible(false);
        }
        posts();
    }

    public void getBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Timeline.fxml"));
        root = loader.load();
        Timeline timeline= loader.getController();
        timeline.setUser(user);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    Button follow;

    public void validateFollow(){
        ArrayList<String> follower = new ArrayList<>();
        JSONArray jsonFollower = new JSONArray(selectedUser.getJSONArray("Followers"));
        Main.jsonToList(jsonFollower , follower);
        if(follower.contains(user.getString("Username"))){
            follow.setText("Unfollow");
        }else{
            follow.setText("Follow");
        }
    }

    public void follow(){
        Files file = new Files();
        ArrayList<String> following = new ArrayList<>();
        ArrayList<String> follower = new ArrayList<>();
        Main.jsonToList(user.getJSONArray("Followings") , following);
        Main.jsonToList(selectedUser.getJSONArray("Followers") , follower);
        if(follower.contains(user.getString("Username"))){               //to follow
            following.remove(selectedUser.getString("Username"));
            user.put("Followings" , following);
            file.profileSave(user);
            //
            follower.remove(user.getString("Username"));
            selectedUser.put("Followers" , follower);
            file.profileSave(selectedUser);
        }else{                  //to unfollow
            following.add(selectedUser.getString("Username"));
            user.put("Followings" , following);
            file.profileSave(user);
            // up for user and down for user who we follow
            follower.add(user.getString("Username"));
            selectedUser.put("Followers" , follower);
            file.profileSave(selectedUser);
        }
        setInfo(selectedUser , perScene ,user);
    }

    @FXML
    VBox vboxPost;

    public void posts(){
        Files file = new Files();
        ArrayList<String> posts = new ArrayList<>();
        file.fileReader(posts , selectedUser.getString("Username") , 1);
        vboxPost.getChildren().clear();
        for(int i = posts.size() -1 ; i >= 0 ; i--) {
            try {           //shows the posts of the user who is selected
                JSONObject jsonPost = new JSONObject(posts.get(i));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PostsProfile.fxml"));
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
    public void selectedPost(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectPost.fxml"));
            root = loader.load();
            SelectPost selectPost = loader.getController();
            JSONObject jsonPost = new JSONObject(selectButton.getText());
            System.out.println(user);
            selectPost.setUser(new JSONObject(userLabel.getText()), jsonPost);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
