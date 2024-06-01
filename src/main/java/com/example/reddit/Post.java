package com.example.reddit;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import org.json.JSONException;

public class Post {
    private Files file = new Files();
    private ArrayList<JSONObject> comments = new ArrayList<>();
    private ArrayList<String> upvotes = new ArrayList<>();
    private ArrayList<String> downvotes = new ArrayList<>();
    private String username;
    private int karma = 0;
    private String title;
    private String subreddit;
    private String postId;

    public Post(String username){
        this.username = username;
    }

    public Post(){

    }

    public void saveChanges(String changedPost){
        //for account folder
        JSONObject jsonObject = new JSONObject(changedPost);
        ArrayList<String> list = new ArrayList<>();
        file.fileReader(list, jsonObject.getString("Username") , 1 );
        file.postFinder(list , changedPost);
        String data ="";
        for(int i = 0 ; i < list.size() ; i++){
            data += (list.get(i)) + "\n";
        }
        file.fileWriter(jsonObject.getString("Username") , data, 1 , false);
        //for subreddit file
        list.clear();
        file.fileReader(list, jsonObject.getString("Subreddit") , 3 );
        file.postFinder(list , changedPost);
        data ="";
        for(int i = 0 ; i < list.size() ; i++){
            data += (list.get(i)) + "\n";
        }
        file.fileWriter(jsonObject.getString("Subreddit") , data, 3 , false);
        //at the end lets save it in explore
        list.clear();
        file.fileReader(list, "Explore", 2 );
        file.postFinder(list , changedPost);
        data ="";
        for(int i = 0 ; i < list.size() ; i++){
            data += (list.get(i)) + "\n";
        }
        file.fileWriter("Explore", data, 2 , false);
    }

    public void profileKarma(int value , String username){ // might change it
        ArrayList<String> accountList = new ArrayList<>();
        file.fileReader(accountList , "Account" , 2);
        file.karmaSaver(username , accountList , value);
    }

    public int validKarma(JSONObject currentUser , int value , JSONObject json){
        String name = currentUser.getString("Username");
        ArrayList<String> upvotes = new ArrayList<>();
        jsonToList(json.getJSONArray("Upvote") , upvotes);
        ArrayList<String> downvotes = new ArrayList<>();
        jsonToList(json.getJSONArray("Downvote") , downvotes);
        if(value == 1){
            if(upvotes.contains(name)){    //if you already upvoted it removes the upvote
                value *= -1;
                upvotes.remove(name);
                json.put("Upvote" , upvotes);
            }
            else if(downvotes.contains(name)){  //and if you downvoted it deletes it and make it an upvote
                value *= 2;
                downvotes.remove(name);
                json.put("Downvote" , downvotes);
                upvotes.add(name);
                json.put("Upvote" , upvotes);
            } else{
                upvotes.add(name);
                json.put("Upvote" , upvotes);
            }
        } else if(value == -1){
            if(upvotes.contains(name)){
                value *= 2;
                upvotes.remove(name);
                json.put("Upvote" , upvotes);
                downvotes.add(name);
                json.put("Downvote" , downvotes);
            }
            else if(downvotes.contains(name)){     //if you already downvoted
                value *= -1;
                downvotes.remove(name);
                json.put("Downvote" , downvotes);
            }else{
                downvotes.add(name);
                json.put("Downvote" , downvotes);
            }
        }
        return value;
    }

    public void jsonToList(JSONArray jsonArray , ArrayList<String> list){
        for(int i = 0 ; i < jsonArray.length() ; i++){
            list.add(jsonArray.getString(i));
        }
    }

}
