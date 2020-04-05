package com.example.loginuser.model;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Login implements Serializable {
	private String email;

    private String password;

    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return this.password;
    }

}
