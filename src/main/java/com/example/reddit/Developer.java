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

    /*private void reportMenu(){
        Scanner in = new Scanner(System.in);
        Files file = new Files();
        ArrayList<String> reports = new ArrayList<>();
        file.fileReader(reports , "Reports" , 2);
        for(int i = 0 ; i < reports.size() ; i++){
            System.out.println("(" + (i + 1) + ") " + reports.get(i));
        }
        int index = in.nextInt();
        try {
            String str = reports.get(index - 1);
            JSONObject selectedAccount = new JSONObject(str);
            System.out.println("(1) Delete the Post\n(2) Delete Account\n(3) Ignore\n(4) Exit");
            switch (in.next()){
                case "1":
                    file.deletePost(selectedAccount);
                    deleteFromList(str);
                    reportMenu();
                    break;
                case "2":
                    JSONObject acc = new JSONObject(file.usernameFind(selectedAccount.getString("Username")));
                    Account account = new Account(selectedAccount.getString("Username"));
                    account.deleteAccount(acc);
                    deleteFromList(str);
                    break;
                case "3":
                    deleteFromList(str);
                    break;
                default:

            }
        }catch (IndexOutOfBoundsException e){
            reportMenu();
        }
    }*/

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
