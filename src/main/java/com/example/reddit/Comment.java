package com.example.reddit;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Comment extends Post {
    private String writer = "";
    private String text = "";
    private int karma = 0;
    private ArrayList<String> upvotes = new ArrayList<>();
    private ArrayList<String> downvotes = new ArrayList<>();
    private ArrayList<String> replay = new ArrayList<>();

    public JSONObject newComment(JSONObject currentUser , String text){
        this.text = text;
        writer = currentUser.getString("Username");
        JSONObject json = new JSONObject();
        json.put("Text" , this.text);
        json.put("Writer" , writer);
        json.put("Upvote" , upvotes);
        json.put("Downvote" , downvotes);
        json.put("Replay" , replay);
        json.put("karma" , karma);
        return json;
    }



    @Override
    public void saveChanges(String changedPost) {
        super.saveChanges(changedPost);
    }



    @Override
    public int validKarma(JSONObject currentUser, int value, JSONObject comment) {
        return super.validKarma(currentUser, value, comment);
    }

    public void addReplay(JSONObject selectedComment , String replay , JSONObject post , int index){
        JSONArray replayJson = selectedComment.getJSONArray("Replay");
        replayJson.put(replay);
//        selectedComment.put("Replay" , replayJson);
//        ArrayList<JSONObject> comments = new ArrayList<>();
//        jsonComment.put("Comment" , comments);
        ArrayList<JSONObject> comments = new ArrayList<>();
        jsonArraytoList(post.getJSONArray("Comment") , comments);
        comments.set(index , selectedComment);
        post.put("Comment" , comments);
        saveChanges(post.toString());
    }

    public void jsonArraytoList(JSONArray jsonArray , ArrayList<JSONObject> list){
        for(int i = 0 ; i < jsonArray.length() ; i++){
            list.add(jsonArray.getJSONObject(i));
        }
    }

    @Override
    public void profileKarma(int value, String username) {
        super.profileKarma(value, username);
    }
}
