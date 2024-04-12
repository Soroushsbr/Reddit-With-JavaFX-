package com.example.reddit;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
public class SubReddit {
    private String name;
    private String about;
    private ArrayList<String> members = new ArrayList<>();

    private ArrayList<String> admins = new ArrayList<>();

    public SubReddit(){
    }

    public void newSubreddit(String name){
        Scanner in = new Scanner(System.in);
        this.name = name;
        System.out.println("Tell us about your community");
        about = in.nextLine();
        System.out.println("(1) Create\n(2) Exit");
        if("1".equals(in.next())){
            JSONObject json = new JSONObject();
            json.put("Name", this.name);
            json.put("Members" , members);
            json.put("Admins" , admins);
            json.put("About", about);
            Files file = new Files();
            file.fileWriter("Subreddit" , json.toString(), 2 , true );
        }
    }

}

