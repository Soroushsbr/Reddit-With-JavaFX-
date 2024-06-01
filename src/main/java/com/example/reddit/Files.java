package com.example.reddit;

import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class Files {

    public void fileWriter(String username , String data, int option , boolean append){
        String address;
        if(option == 1){
            address = "C:\\Users\\Lenovo\\Desktop\\Java Projects\\Reddit\\accounts\\" + username + ".txt";
        } else if(option == 3){
            address = "C:\\Users\\Lenovo\\Desktop\\Java Projects\\Reddit\\subreddits\\" + username + ".txt";
        } else{
            address = username + ".txt";
        }
        try{
            FileWriter writer = new FileWriter(address , append);
            if(append) {
                writer.write(data + "\n");
            }
            else{
                writer.write(data); //when I don't want to append I don't need the extra end line
            }
            writer.close();
        }catch (IOException e){
        }
    }

    public void fileReader(ArrayList<String> List , String address , int option){
        if(option == 1){
            address = "C:\\Users\\Lenovo\\Desktop\\Java Projects\\Reddit\\accounts\\" + address + ".txt";
        } else if(option == 3){
            address = "C:\\Users\\Lenovo\\Desktop\\Java Projects\\Reddit\\subreddits\\" + address + ".txt";
        } else{
            address = address + ".txt";
        }
        try{
            File file = new File(address);
            Scanner reader = new Scanner(file);
            while(reader.hasNextLine()){
                List.add(reader.nextLine());
            }
            reader.close();
        }catch (FileNotFoundException e){
        }
    }

    public String usernameFind(String username) {
        ArrayList<String> accountList = new ArrayList<>();
        fileReader(accountList , "Account" , 2 );
        if(!(accountList.isEmpty())) {
            for (int i = 0; i < accountList.size(); i++) {
                JSONObject jsonObject = new JSONObject(accountList.get(i));
                if((jsonObject.getString("Username")).equals(username)){
                    return jsonObject.toString();
                }
            }
        }
        return null;
    }

    public String subredditFind(String name){
        ArrayList<String> subredditList = new ArrayList<>();
        fileReader(subredditList, "Subreddit" , 2 );
        if(!(subredditList.isEmpty())) {
            for (int i = 0; i < subredditList.size(); i++) {
                JSONObject jsonObject = new JSONObject(subredditList.get(i));
                if((jsonObject.getString("Name")).equals(name)){
                    return jsonObject.toString();
                }
            }
        }
        return null;
    }

    public void karmaSaver(String username , ArrayList<String> accountList, int value) { // check this later
        if(!(accountList.isEmpty())) {
            for (int i = 0; i < accountList.size(); i++) {
                JSONObject jsonObject = new JSONObject(accountList.get(i));
                if((jsonObject.getString("Username")).equals(username)){
                    int karma = jsonObject.getInt("Karma");
                    jsonObject.put("Karma" , karma + value);
                    save(accountList, i, jsonObject , "Account");
                    break;
                }
            }
        }
    }


    public void profileSave(JSONObject account){
        ArrayList<String> accountList = new ArrayList<>();
        fileReader(accountList , "Account" , 2);
        if(!(accountList.isEmpty())){
            for(int i = 0 ; i < accountList.size() ; i++){
                JSONObject jsonObject = new JSONObject(accountList.get(i));
                if((jsonObject.getString("Username")).equals(account.getString("Username"))){
                    save(accountList, i, account , "Account");
                }
            }
        }
    }

    public void subredditSave(JSONObject subJson){
        ArrayList<String> subList = new ArrayList<>();
        fileReader(subList , "Subreddit" , 2);
        if(!(subList.isEmpty())){
            for(int i = 0 ; i < subList.size() ; i++){
                JSONObject jsonObject = new JSONObject(subList.get(i));
                if((jsonObject.getString("Name")).equals(subJson.getString("Name"))){
                    save(subList, i, subJson , "Subreddit");
                }
            }
        }
    }

    private void save(ArrayList<String> list, int i, JSONObject jsonObject , String address) {
        list.set(i , jsonObject.toString());
        String data ="";
        for(int j = 0 ; j < list.size() ; j++){
            data += (list.get(j)) + "\n";
        }
        fileWriter(address, data, 2 , false);
    }

    public void postFinder(ArrayList<String> list , String target){
        JSONObject jsonTarget = new JSONObject(target);
        for(int i = 0 ; i < list.size(); i++){
            JSONObject json = new JSONObject(list.get(i));
            if((json.getString("ID")).equals(jsonTarget.getString("ID"))){
                list.set(i , target);
                break;
            }
        }
    }

    public void deletePost(JSONObject jsonPost){
        ArrayList<String> list = new ArrayList<>();
        fileReader(list , jsonPost.getString("Username") , 1);
        postFind2Delete(list , jsonPost.toString());
        String data ="";
        for(int j = 0 ; j < list.size() ; j++){
            data += (list.get(j)) + "\n";
        }
        fileWriter(jsonPost.getString("Username") , data , 1 , false);
        list.clear();
        fileReader(list, jsonPost.getString("Subreddit") , 3 );
        postFind2Delete(list , jsonPost.toString());
        data ="";
        for(int i = 0 ; i < list.size() ; i++){
            data += (list.get(i)) + "\n";
        }
        fileWriter(jsonPost.getString("Subreddit") , data, 3 , false);
        list.clear();
        fileReader(list, "Explore", 2 );
        postFind2Delete(list , jsonPost.toString());
        data ="";
        for(int i = 0 ; i < list.size() ; i++){
            data += (list.get(i)) + "\n";
        }
        fileWriter("Explore", data, 2 , false);
    }


    private void postFind2Delete(ArrayList<String> list, String target){
        for(int i = 0 ; i < list.size() ; i++){
            if(list.get(i).equals(target)){
                list.remove(i);
                break;
            }
        }
    }

}
