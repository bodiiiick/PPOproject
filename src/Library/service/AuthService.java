package Library.service;

import Library.model.Role;
import Library.model.User;

import java.util.ArrayList;

public class AuthService {
    public User login(String username , String password){
        if (username.equals("admin") && password.equals("admin")){
            return new User("admin", "admin", Role.ADMIN, new ArrayList<>());
        }
        if (username.equals("user") && password.equals("user")) {
            return new User("user", "user", Role.USER, new ArrayList<>());
        }

        return null;
    }

//    public void logout(){
//
//    }
}
