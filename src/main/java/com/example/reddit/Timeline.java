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
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timeline {
    @FXML
    TextField searchbar;
    @FXML
    ListView<String> listView;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private JSONObject user = new JSONObject();
    private ArrayList<String> posts = new ArrayList<>();

    public void setUser(JSONObject user){
        this.user = user;
        Collections.addAll(sorting.getItems() , "Newest" , "For You" , "Followings");
        newest();
        setTopSub();
    }
    @FXML
    AnchorPane searchScroll;
    @FXML
    VBox vboxSearch;
    @FXML
    Label jsonUser;
    public void search() throws IOException {
        searchScroll.setVisible(true);
        vboxSearch.getChildren().clear();
        Button button = new Button();
        button.setMaxWidth(Double.MAX_VALUE);
        vboxSearch.getChildren().add(button);
        String regex = searchbar.getText();
        ArrayList<String> nameFind = new ArrayList<>();
        if(regex.length()> 1){
            if(regex.startsWith("u/")){     //search for username
                regex = regex.substring(2);
                finder(nameFind , regex , "Account" , "Username");
            }else if ((regex).startsWith("r/")){        //for subreddit
                regex = regex.substring(2);
                finder(nameFind, regex , "Subreddit" , "Name");
            }else{  //for both
                finder(nameFind , regex,"Subreddit" , "Name");
                finder(nameFind , regex , "Account" , "Username");
            }
        }else{
            finder(nameFind , regex,"Subreddit" , "Name");
            finder(nameFind , regex , "Account" , "Username");
            //todo show
        }
        for(String find : nameFind){        //show them as a button in vbox
            FXMLLoader loader = new FXMLLoader(getClass().getResource("searchFinds.fxml"));
            AnchorPane anchorPane = loader.load();
            ((Label) anchorPane.getChildren().get(0)).setText(user.toString());
            ((Button) anchorPane.getChildren().get(1)).setText(find);
            vboxSearch.getChildren().add(anchorPane);
        }
    }
    @FXML
    Button searchButton;
    public void selectSearch(ActionEvent event)throws IOException{
        if(searchButton.getText().startsWith("u/")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Profile.fxml"));
            root = loader.load();
            Profile profile = loader.getController();
            Files files = new Files();
            JSONObject selectedUser = new JSONObject(files.usernameFind(searchButton.getText().substring(2)));
            profile.setInfo(selectedUser, "SelectPost.fxml", new JSONObject(jsonUser.getText()));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }else{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubPage.fxml"));
            root = loader.load();
            Files files = new Files();
            JSONObject selectedSub = new JSONObject(files.subredditFind(searchButton.getText().substring(2)));
            SubPage subPage = loader.getController();
            subPage.setInfo(selectedSub , new JSONObject(jsonUser.getText()));
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void unshowSearch(){
        searchScroll.setVisible(false);
    }


    public void finder(ArrayList<String> nameFind , String regex , String address , String name){
        regex = "^" + regex + ".*";
        Pattern pattern = Pattern.compile(regex , Pattern.CASE_INSENSITIVE);
        ArrayList<String> nameList = new ArrayList<>();
        Files file = new Files();
        file.fileReader(nameList , address , 2);
        Matcher matcher;
        for(int i = 0 ; i < nameList.size() ; i++){
            JSONObject json = new JSONObject(nameList.get(i));
            matcher = pattern.matcher(json.getString(name));
            if(matcher.find()){
                if(address.equals("Subreddit")){
                    nameFind.add("r/" + json.getString(name));
                }else {
                    nameFind.add("u/" + json.getString(name));
                }
            }
        }
    }

    public void profile(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountMenu.fxml"));
        root = loader.load();
        AccountMenu accountMenu = loader.getController();
        accountMenu.setUser(user);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void newest(){
        posts.clear();
        Files file = new Files();
        file.fileReader(posts , "Explore" , 2);
        posts();
    }

    public void forYou(String nameList , String name){      //this method set the list of posts as subreddit you joined or followings
        Files file = new Files();
        posts.clear();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        Main.jsonToList(user.getJSONArray(nameList) , list);
        file.fileReader(posts , "Explore" , 2);
        for(int i = 0 ; i < posts.size() ; i++){
            JSONObject jsonObject = new JSONObject(posts.get(i));
            if(list.contains(jsonObject.getString(name))){
                temp.add(posts.get(i));
            }
        }
        posts = temp;
        posts();
    }
    @FXML
    ComboBox<String> sorting;

    public void sort(){
        if(sorting.getValue().equals("Newest")){
            newest();
        }else if(sorting.getValue().equals("For You")){
            forYou("Joined Subreddit", "Subreddit");
        }else{
            forYou("Followings", "Username");
        }
    }
    @FXML
    VBox vbox;
    public void posts(){
        vbox.getChildren().clear();
        for(int i = posts.size() -1 ; i >= 0 ; i--){
            try {           //to set posts in timline in a vbox
                JSONObject jsonPost = new JSONObject(posts.get(i));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Posts.fxml"));
                AnchorPane anchorPane = loader.load();
                ((Label) anchorPane.getChildren().get(0)).setText("r/" + jsonPost.getString("Subreddit"));
                ((Label) anchorPane.getChildren().get(1)).setText(jsonPost.getString("Title"));
                ((Label) anchorPane.getChildren().get(2)).setText(jsonPost.getString("Post"));
                ((Label) anchorPane.getChildren().get(3)).setText(user.toString());
                ((Button) anchorPane.getChildren().get(4)).setText(jsonPost.toString());
                vbox.getChildren().add(anchorPane);
            }catch (IOException e){
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
    Hyperlink firstSub;
    @FXML
    Hyperlink secondSub;
    @FXML
    Hyperlink thirdSub;
    public void setTopSub(){
        ArrayList<String> subreddits = new ArrayList<>();
        Files files = new Files();
        files.fileReader(subreddits , "Subreddit" , 2);
        try {
            JSONObject first = new JSONObject(subreddits.get(subreddits.size() - 1));
            firstSub.setText(first.getString("Name"));
            JSONObject second = new JSONObject(subreddits.get(subreddits.size() - 2));
            secondSub.setText(second.getString("Name"));
            JSONObject third = new JSONObject(subreddits.get(subreddits.size() - 3));
            thirdSub.setText(third.getString("Name"));
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public void setFirstSub(ActionEvent event) throws IOException {
        selectSubreddit(event ,firstSub.getText());
    }
    public void setSecondSub(ActionEvent event)throws IOException{
        selectSubreddit(event , secondSub.getText());
    }
    public void setThirdSub(ActionEvent event)throws IOException{
        selectSubreddit(event, thirdSub.getText());
    }
    public void selectSubreddit(ActionEvent event , String subName)throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SubPage.fxml"));
        root = loader.load();
        Files files = new Files();
        JSONObject selectedSub = new JSONObject(files.subredditFind(subName));
        SubPage subPage = loader.getController();
        subPage.setInfo(selectedSub , user);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
