package com.example.reddit;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin extends SubReddit{
    private String name;

    private JSONObject subInfo;
    private ArrayList<String> members = new ArrayList<>();

    private ArrayList<String> admins = new ArrayList<>();

    public Admin(String name){
        this.name = name;
        Files file = new Files();
        subInfo = new JSONObject(file.subredditFind(name));
        setMembers();
        setAdmins();
    }

    public void setMembers() {
        JSONArray memberJson = new JSONArray(subInfo.getJSONArray("Members"));
        Main.jsonToList(memberJson , members);
    }

    public void setAdmins() {
        JSONArray adminJson = new JSONArray(subInfo.getJSONArray("Admins"));
        Main.jsonToList(adminJson , admins);
    }


}
