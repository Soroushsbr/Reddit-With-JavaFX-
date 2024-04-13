package com.example.reddit;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Developer{
    private String username = "Soroush";
    private String password = "48657";

    public void validInput(String enteredUsername , String enteredPassword){
        if(enteredUsername.equals(username) && enteredPassword.equals(password)){
        }
    }

    private void deleteFromList(String user){
        Files file = new Files();
        ArrayList<String> list = new ArrayList<>();
        file.fileReader(list , "Reports" , 2);
        list.remove(user);
        String data = "";
        for (int i = 0 ; i < list.size() ; i++){
            data += list.get(i) + "\n";
        }
        file.fileWriter("Reports" , data , 2 , false);
    }

}
