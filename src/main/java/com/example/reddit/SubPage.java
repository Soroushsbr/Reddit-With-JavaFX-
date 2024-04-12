package com.example.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SubPage {
    @FXML
    Label name;
    @FXML
    Label about;
    @FXML
    Label members;
    @FXML
    ListView<String> listView;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private JSONObject user = new JSONObject();
    private JSONObject sub = new JSONObject();
    public void setInfo(JSONObject sub , JSONObject user){
        name.setText("r/" + sub.getString("Name"));
        about.setText(sub.getString("About"));
        members.setText(String.valueOf(sub.getJSONArray("Members").length()));
        this.user = user;
        this.sub = sub;
        validateJoin();
        validateAdmin();
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
    Button join;

    public void validateJoin(){
        ArrayList<String> membersList = new ArrayList<>();
        JSONArray jsonMember = new JSONArray(sub.getJSONArray("Members"));
        Main.jsonToList(jsonMember , membersList);
        if(membersList.contains(user.getString("Username"))){
            join.setText("Joined");
        }else{
            join.setText("Join");
        }
    }

    public void join(){
        Files file = new Files();
        ArrayList<String> joinedSub = new ArrayList<>();
        ArrayList<String> member = new ArrayList<>();
        Main.jsonToList(user.getJSONArray("Joined Subreddit") , joinedSub);
        Main.jsonToList(sub.getJSONArray("Members") , member);
        if(member.contains(user.getString("Username"))){               //to join
            joinedSub.remove(sub.getString("Name"));
            user.put("Joined Subreddit" , joinedSub);
            file.profileSave(user);
            //
            member.remove(user.getString("Username"));
            sub.put("Members" , member);
            file.subredditSave(sub);
            setInfo(sub , user);
        }else{                  //to unjoin
            joinedSub.add(sub.getString("Name"));
            user.put("Joined Subreddit" , joinedSub);
            file.profileSave(user);
            // up for user and down for user who we follow
            member.add(user.getString("Username"));
            sub.put("Members" , member);
            file.subredditSave(sub);
            setInfo(sub , user);
        }
    }
    @FXML
    VBox vboxPost;
    public void posts(){
        Files file = new Files();
        ArrayList<String> posts = new ArrayList<>();
        file.fileReader(posts , sub.getString("Name") , 3);
        vboxPost.getChildren().clear();
        for(int i = posts.size() -1 ; i >= 0 ; i--) {
            try {
                JSONObject jsonPost = new JSONObject(posts.get(i));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PostsSub.fxml"));
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
    @FXML
    Rectangle backgroundAdmin;
    @FXML
    AnchorPane anchorPaneAdmin;
    @FXML
    ComboBox<String> adminsBox;
    @FXML
    ComboBox<String> membersBox;
    @FXML
    Button adminButton;

    public void validateAdmin(){
        ArrayList<String> admins = new ArrayList<>();
        Main.jsonToList(sub.getJSONArray("Admins"), admins);
        if(admins.contains(user.getString("Username"))){
            adminButton.setVisible(true);
        }
    }
    public void admin(){
        adminsBox.getItems().clear();
        membersBox.getItems().clear();
        backgroundAdmin.setVisible(true);
        anchorPaneAdmin.setVisible(true);
        ArrayList<String> admins = new ArrayList<>();
        ArrayList<String> members = new ArrayList<>();
        Main.jsonToList(sub.getJSONArray("Admins") , admins);
        adminsBox.getItems().addAll(admins);
        Main.jsonToList(sub.getJSONArray("Members") , members);
        membersBox.getItems().addAll(members);
    }
    @FXML
    Label isAdminLabel;

    public void addAdmin(){
        Files files = new Files();
        String selectedMember = membersBox.getValue();
        ArrayList<String> adminsTemp = new ArrayList<>();
        Main.jsonToList(sub.getJSONArray("Admins") , adminsTemp);
        if(!adminsTemp.contains(selectedMember)) {
            isAdminLabel.setText("");
            adminsTemp.add(selectedMember);
            sub.put("Admins", adminsTemp);
            files.subredditSave(sub);       //save changes in subreddit
            JSONObject memberJson = new JSONObject(files.usernameFind(selectedMember));
            JSONArray memberTemp = memberJson.getJSONArray("Admin Subreddit");
            memberTemp.put(sub.getString("Name"));
            memberJson.put("Admin Subreddit", memberTemp);
            files.profileSave(memberJson);  //and profile
            admin();
        }else{
            isAdminLabel.setText(selectedMember + " is Already Moderator");
        }
    }

    public void removeAdmin(){
        Files files = new Files();
        isAdminLabel.setText("");
        String selectedAdmin = adminsBox.getValue();
        ArrayList<String> adminsTemp = new ArrayList<>();
        Main.jsonToList(sub.getJSONArray("Admins") , adminsTemp);
        adminsTemp.remove(selectedAdmin);
        sub.put("Admins" , adminsTemp);
        files.subredditSave(sub);
        JSONObject adminJson = new JSONObject(files.usernameFind(selectedAdmin));
        ArrayList<String> adminTemp = new ArrayList<>();
        Main.jsonToList(adminJson.getJSONArray("Admin Subreddit") , adminTemp);
        adminTemp.remove(sub.getString("Name"));
        adminJson.put("Admin Subreddit",adminTemp);
        files.profileSave(adminJson);
        admin();
    }

    public void closeAdmin(){
        backgroundAdmin.setVisible(false);
        anchorPaneAdmin.setVisible(false);
    }
}
