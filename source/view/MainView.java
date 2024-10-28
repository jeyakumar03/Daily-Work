package source.view;
import java.util.Scanner;

import java.sql.SQLException;

import source.view.SuperAdmin;
import source.view.UserInfo;
import source.view.AdminHelper;

import source.controller.AdminDb;
public class MainView{
   private Scanner ob=new Scanner(System.in);
   public void show(){
     try{
        boolean s = true;
        while (s){
            System.out.println("-------------------------------");
            System.out.println("1. SuperAdmin\n2. Admin\n3. User");
            System.out.println("-------------------------------");
            System.out.print("Enter your choice: ");
            int choice = ob.nextInt();
            ob.nextLine(); 
            switch (choice) {
                case 1:
                    SuperAdmin.show();
                    break;
                case 2:
                    AdminDb adminDb = new AdminDb();
                    new AdminHelper(adminDb).signin();
                    break;
                case 3:
                    UserInfo.show();
                    break;
                default:
                    System.out.println("Please enter a valid choice:");
                    break;
            }
            System.out.println("-------------------------------");
        }
    }
    catch(SQLException e){
           e.printStackTrace();
    	System.out.println("Exception caught in Main");
    }
  }
}
