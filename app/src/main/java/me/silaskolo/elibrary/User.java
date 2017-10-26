package me.silaskolo.elibrary;

/**
 * Created by silaskolo on 26/10/2017.
 */

public class User {

    private String email, first_name,last_name, mobile,matric_no,level;
    private int user_id;

    public User(int user_id, String email, String first_name, String last_name, String mobile, String matric_no, String level) {
        this.user_id = user_id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.mobile = mobile;
        this.matric_no = matric_no;
        this.level = level;
    }

    public int getId() {
        return user_id;
    }


    public String getEmail() {
        return email;
    }


    public String getFirstName() {
        return first_name;
    }


    public String getLastName() {
        return last_name;
    }


    public String getMobile() {
        return mobile;
    }


    public String getMatricNumber() {
        return matric_no;
    }


    public String getLevel() {
        return level;
    }

}