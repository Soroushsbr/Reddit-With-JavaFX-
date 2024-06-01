package com.example.reddit;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;
import java.util.UUID;
public class Account {
    private String displayName;
    private String username;
    private String password;
    private int karma = 0;
    private String email;
    private String about = "";
    private ArrayList<String> joinedSubreddits = new ArrayList<>();
    private ArrayList<String> adminSubreddits = new ArrayList<>();
    private ArrayList<String> followers = new ArrayList<>();
    private ArrayList<String> followings = new ArrayList<>();
    private String id;

    public void idMaker(){
        id = (UUID.randomUUID()).toString();
    }


    public void setAbout(String about) {
        this.about = about;
    }

    public Account(String username , String password , String email){
        displayName = username;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Account(String username){
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public boolean validatePassword(String enteredPassword){
        String hashedPass = Integer.toString(enteredPassword.hashCode());
        return (hashedPass).equals(this.password);
    }

    public Account(){

    }


    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("Username" , this.username);
        json.put("Password", this.password);
        json.put("Email", this.email);
        json.put("Karma", this.karma);
        json.put("Joined Subreddit" ,this.joinedSubreddits);
        json.put("Admin Subreddit", adminSubreddits);
        json.put("ID" , this.id);
        json.put("Display Name",this.displayName);
        json.put("About" , this.about);
        json.put("Followers" , followers);
        json.put("Followings" , followings);
        return json;
    }

    public void deleteAccount(JSONObject user){
        //delete all posts
        Files file = new Files();
        ArrayList<String> posts = new ArrayList<>();
        file.fileReader(posts , user.getString("Username") , 1);
        for(int i = 0 ; i < posts.size() ;i++){
            JSONObject temp = new JSONObject(posts.get(i));
            file.deletePost(temp);
        }
        //delete the account
        ArrayList<String> accounts = new ArrayList<>();
        file.fileReader(accounts , "Account" , 2);
        accounts.remove(user.toString());
        String data ="";
        for(int i = 0 ; i < accounts.size(); i++){
            data += accounts.get(i) + "\n";
        }
        file.fileWriter("Account" , data , 2 , false);
        //delete joined subreddit from them
        ArrayList<String> subreddit = new ArrayList<>();
        file.fileReader(subreddit , "Subreddit" , 2);
        ArrayList<String> joinedSub = new ArrayList<>();
        Main.jsonToList(user.getJSONArray("Joined Subreddit") , joinedSub);
        ArrayList<String> member = new ArrayList<>();
        for(String sub : subreddit){
            if(joinedSub.contains(sub)){
                JSONObject temp = new JSONObject(sub);
                member.clear();
                Main.jsonToList(temp.getJSONArray("Member") , member);
                member.remove(user.getString("Username"));
                temp.put("Members" , member);
                file.subredditSave(temp);
            }
        }
        //delete follower
        ArrayList<String> followers = new ArrayList<>();
        ArrayList<String> followings = new ArrayList<>();
        Main.jsonToList(user.getJSONArray("Followers") , followers);
        for(String follower: followers){
            JSONObject followerJson = new JSONObject(file.usernameFind(follower));
            followings.clear();
            Main.jsonToList(followerJson.getJSONArray("Followings"), followings);
            followings.remove(user.getString("Username"));
            followerJson.put("Followers" , follower);
            file.profileSave(followerJson);
        }
    }

    public void addAdmin(String subreddit) {
        adminSubreddits.add(subreddit);
        Files file = new Files();
        file.profileSave(toJson());
    }

}
