package com.ble.eic.git.botspine;

public class StoreData {
     
	    //private variables
	    String address;
	    String password;
	    int hasPassword;
	     
	    // Empty constructor
	    public StoreData(){
	         
	    }
	    // constructor
	    public StoreData(String address, String password, int hasPassword){
	        this.address = address;
	        this.password = password;
	        this.hasPassword = hasPassword;
	    }
	     
	    // constructor
	    public StoreData(String address, String password){
	        this.address = address;
	        this.password = password;
	    }
	    // getting ID
	    public String getAddress(){
	        return this.address;
	    }
	     
	    // setting address
	    public void setAddress(String address){
	        this.address = address;
	    }
	     
	    // getting password
	    public String getPassword(){
	        return this.password;
	    }
	     
	    // setting password
	    public void setPassword(String password){
	        this.password = password;
	    }
	     
	    // getting phone number
	    public int getHasPassword(){
	        return this.hasPassword;
	    }
	     
	    // setting phone number
	    public void setHasPassword(int hasPassword){
	        this.hasPassword = hasPassword;
	    }
	
}
