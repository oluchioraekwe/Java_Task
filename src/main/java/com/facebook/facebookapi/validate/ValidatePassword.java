package com.facebook.facebookapi.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatePassword {
    private String password;
    static public boolean isValid(String password){
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
