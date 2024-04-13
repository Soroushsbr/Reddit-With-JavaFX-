package com.example.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.ArrayList;

public class SelectPost {
    @FXML
    Hyperlink subreddit;
    @FXML
    Hyperlink username;
    @FXML
    Label title;
    @FXML
    Label text;
    @FXML
    Label karma;
    @FXML
    Button deletePostButton;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private JSONObject user = new JSONObject();
    private JSONObject post = new JSONObject();
    public void setUser(JSONObject user , JSONObject post){
        this.user = user;
        this.post = post;
        ArrayList<String> tempSub = new ArrayList<>();
        Main.jsonToList(user.getJSONArray("Admin Subreddit") , tempSub);
        //the creator of post and the moderators of the subreddit can delete the post
        if(user.getString("Username").equals(post.getString("Username")) || tempSub.contains(post.getString("Subreddit"))){
            deletePostButton.setVisible(true);
        }
        setInfo(post);
    }

    public void selectUsername(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Profile.fxml"));
        root = loader.load();
        Profile profile = loader.getController();
        Files files = new Files();
        JSONObject selectedUser = new JSONObject(files.usernameFind(post.getString("Username")));
        profile.setInfo(selectedUser , "SelectPost.fxml" , user);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void selectSubreddit(ActionEvent event)throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SubPage.fxml"));
        root = loader.load();
        Files files = new Files();
        JSONObject selectedSub = new JSONObject(files.subredditFind(post.getString("Subreddit")));
        SubPage subPage = loader.getController();
        subPage.setInfo(selectedSub , user);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setInfo(JSONObject post){
        subreddit.setText("r/" + post.getString("Subreddit"));
        username.setText(post.getString("Username"));
        title.setText(post.getString("Title"));
        text.setText(post.getString("Post"));
        karma.setText(String.valueOf(post.getInt("Karma")));
    }

    public void timeline(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Timeline.fxml"));
        root = loader.load();
        Timeline timeline = loader.getController();
        timeline.setUser(user);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void upvote(){
        String name = post.getString("Username");
        Post selectedPost = new Post();
        int value = selectedPost.validKarma(user, 1 , post);
        saveKarma(name, selectedPost, value);
    }

    private void saveKarma(String name, Post selectedPost, int value) {
        int perKarma = post.getInt("Karma");
        post.put("Karma", perKarma + value);
        selectedPost.saveChanges(post.toString());
        if(!name.equals(user.getString("Username"))) {      //user cant give karma to himself
            selectedPost.profileKarma(value, name);
        }
        karma.setText(String.valueOf(perKarma + value));
        int userKarma = user.getInt("Karma");
        user.put("Karma" , userKarma + value);
    }

    public void downvote(){
        String name = post.getString("Username");
        Post selectedPost = new Post();
        int value = selectedPost.validKarma(user, -1 , post);
        saveKarma(name, selectedPost, value);
    }
    @FXML
    ScrollPane scrollPane;
    @FXML
    AnchorPane anchorPaneComments;
    @FXML
    TextArea textArea;
    @FXML
    VBox commentVbox;

    public void showComment(){
        anchorPaneComments.setVisible(false);
        scrollPane.setVisible(true);
        commentVbox.getChildren().clear();
        ArrayList<JSONObject> comments = new ArrayList<>();
        jsonArrayToList(post.getJSONArray("Comment") , comments);
        int i = 0;
        for(JSONObject comment: comments){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CommentView.fxml"));
                AnchorPane anchorPane = loader.load();
                ((Label) anchorPane.getChildren().get(0)).setText("u/" + comment.getString("Writer"));
                ((Label) anchorPane.getChildren().get(1)).setText(comment.getString("Text"));
                ((Label) anchorPane.getChildren().get(2)).setText(String.valueOf(i));
                ((Button) anchorPane.getChildren().get(3)).setText(comment.toString());
                ((Button) anchorPane.getChildren().get(3)).setOnAction(event -> selectComment(((Button) anchorPane.getChildren().get(3)) ,  ((Label) anchorPane.getChildren().get(2))));
                commentVbox.getChildren().add(anchorPane);
                i++;
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }
    @FXML
    AnchorPane selectedCommentPane;
    @FXML
    Label writerLabel;
    @FXML
    Label commentTextLabel;
    @FXML
    ListView<String> listViewReplay;
    JSONObject comment = new JSONObject();

    public void selectComment(Button commentButton , Label index){
        listViewReplay.getItems().clear();
        this.index = Integer.parseInt(index.getText());
        selectedCommentPane.setVisible(true);
        comment = new JSONObject(commentButton.getText());
        writerLabel.setText( "u/" + comment.getString("Writer"));
        commentTextLabel.setText(comment.getString("Text"));
        karmaCommentLabel.setText(String.valueOf(comment.getInt("karma")));
        ArrayList<String> replays = new ArrayList<>();
        Main.jsonToList(comment.getJSONArray("Replay") , replays);
        listViewReplay.getItems().addAll(replays);
        backgroundReplay.setVisible(true);
    }
    public void hideComment(){
        selectedCommentPane.setVisible(false);
        backgroundReplay.setVisible(false);
    }
    @FXML
    Button closeReplayButton;

    @FXML
    TextArea textAreaReplay;
    @FXML
    Button postReplay;
    @FXML
    Rectangle backgroundReplay;
    public void newReplayShow(){
        closeReplayButton.setVisible(true);
        textAreaReplay.setVisible(true);
        postReplay.setVisible(true);
        postReplay.setVisible(true);
    }
    int index = 0;
    public void AddReplay(){
        listViewReplay.getItems().clear();
        String replay = textAreaReplay.getText();
        replay = "u/" + user.getString("Username") + "\n" + replay;
        Comment commentClass = new Comment();
        commentClass.addReplay(comment , replay , post , index);
        ArrayList<String> replays = new ArrayList<>();
        Main.jsonToList(comment.getJSONArray("Replay") , replays);
        listViewReplay.getItems().addAll(replays);
        showComment();
    }

    public void hideNewReplay(){
        closeReplayButton.setVisible(false);
        textAreaReplay.setVisible(false);
        postReplay.setVisible(false);
        postReplay.setVisible(false);
    }
    @FXML
    Label karmaCommentLabel;

    public void upvoteComment(){
        Comment commentClass = new Comment();
        int value = commentClass.validKarma(user , 1 , comment);
        saveKarma(commentClass, value);
    }

    public void downvoteComment(){
        Comment commentClass = new Comment();
        int value = commentClass.validKarma(user , -1 , comment);
        saveKarma(commentClass, value);
    }

    private void saveKarma(Comment commentClass, int value) {
        int karma = comment.getInt("karma");
        comment.put("karma" , karma + value);
        karmaCommentLabel.setText(String.valueOf(karma + value));
        ArrayList<JSONObject> newComments = new ArrayList<>();
        commentClass.jsonArraytoList(post.getJSONArray("Comment") , newComments);
        newComments.set(index , comment);
        System.out.println(newComments);
        post.put("Comment" , newComments);
        commentClass.saveChanges(post.toString());
        commentClass.profileKarma(value , comment.getString("Writer"));
        showComment();
    }

    public void jsonArrayToList(JSONArray jsonArray , ArrayList<JSONObject> list){
        for(int i = 0 ; i < jsonArray.length() ; i++){
            JSONObject temp = jsonArray.getJSONObject(i);
            list.add(temp);
        }
    }

    public void newComment(){
        scrollPane.setVisible(false);
        anchorPaneComments.setVisible(true);
    }

    public void postComment(){
        JSONArray commentsJson = post.getJSONArray("Comment");
        String text = textArea.getText();
        JSONObject json = new JSONObject();
        ArrayList<String> upvotes = new ArrayList<>();
        ArrayList<String> downvotes = new ArrayList<>();
        ArrayList<String> replay = new ArrayList<>();
        json.put("Text" , text);
        json.put("Writer" , user.getString("Username"));
        json.put("Upvote" , upvotes);
        json.put("Downvote" , downvotes);
        json.put("Replay" , replay);
        json.put("karma" , 0);
        commentsJson.put(json);
        post.put("Comment" ,commentsJson);
        Post selectedPost = new Post();
        selectedPost.saveChanges(post.toString());
        showComment();
    }

    public void deletePost() throws IOException{
        Files files = new Files();
        files.deletePost(post);
        timeline(new ActionEvent());
    }
}
