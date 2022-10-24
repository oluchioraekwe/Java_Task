package com.facebook.facebookapi.controller;

import com.facebook.facebookapi.entity.Role;
import com.facebook.facebookapi.entity.User;
import com.facebook.facebookapi.filter.CustomAuthenticationFilter;
import com.facebook.facebookapi.service.UserService;
import com.facebook.facebookapi.service.UserServiceImpl;
import com.facebook.facebookapi.validate.ValidatePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
//@RequestMapping(path = "/facebook")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    public UserController() {
    }

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }



    @GetMapping("/facebook/users")
    public ResponseEntity<?> getUsers(){
        Map<String,Object> response = new HashMap<>();
        List<User> users = userService.getAllUsers();
        response.put("status",200);
        response.put("users", users);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("register/user")
    public ResponseEntity<?> createUser(@RequestBody User user){
        Map<String,Object> response = new HashMap<>();
        User savedUser = userService.getUser(user.getEmail());
        if(savedUser != null){
            String message = "User with email "+user.getEmail()+" already exists in database";
            response.put("error_message",message);
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }
        int date = Integer.parseInt(user.getDob().toString().substring(0,4));
        if(date < 1950){
            String message = "User should be born after 1950";
            response.put("error_message",message);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        boolean checkPassword = ValidatePassword.isValid(user.getPassword());

        if(!checkPassword){
            String message = "Invalid Password";
            response.put("error_message",message);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        User createdUser = userService.saveUser(user);
        response.put("status",201);
        response.put("userId",createdUser.getId());
        String message = "User with name"+ createdUser.getFirstName() +"created successfully";
        response.put("message",message);
        return new ResponseEntity<>(response,HttpStatus.CREATED);

    }

    @PostMapping("/facebook/role")
    public  ResponseEntity<Role> createRole(@RequestBody Role role){
        return new ResponseEntity<Role>(userService.saveRole(role),HttpStatus.CREATED);
    }

    @GetMapping("/facebook/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id){
        return new ResponseEntity<>(userService.getUser(id),HttpStatus.OK);
    }

    @PutMapping("/facebook/user/role")
    public ResponseEntity<?> addRoleToUser(@RequestBody AddRoleToUserBody body){
            userService.addRoleToUser(body.getEmail(), body.getName());
            return ResponseEntity.ok().build();
    }
    @PutMapping("facebook/user/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User user,@PathVariable("id") Long id){
        Map<String,Object> response = new HashMap<>();
        Authentication authenticationFilter = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationFilter.getPrincipal().toString();
        List<String> roles = authenticationFilter.getAuthorities().stream()
                .map(item-> item.getAuthority())
                .collect(Collectors.toList());
        User savedUser = null;
        User updatedUser = null;
        try{
            savedUser = userService.getUser(id);
        }catch (Exception exception){
            System.out.println(exception);
        }
        if(roles.contains("ADMIN") || savedUser.getEmail().equals(username)){
            updatedUser = userService.updateUser(user,id);
            response.put("status",200);
            response.put("message","User with name"+ updatedUser.getFirstName() +"updated successfully");
            return  new ResponseEntity<>(response,HttpStatus.OK);
        }else {
            response.put("status",200);
            response.put("error_message","Cannot update user");
            return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }


    }
    @DeleteMapping("/facebook/user")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserBody body){
        Map<String, String> response = new HashMap<>();
        User user;
        try {
            user = userService.getUser(body.getUserId());
        }catch (Exception exception){
            response.put("error_message", exception.getMessage());
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }

        Authentication authenticationFilter = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationFilter.getPrincipal().toString();
        List<String> roles = authenticationFilter.getAuthorities().stream()
                .map(item-> item.getAuthority())
                        .collect(Collectors.toList());
        if((username.equals(body.getEmail()) && username.equals(user.getEmail())) || roles.contains("ADMIN")){
                    userService.deleteUser(body.getUserId());
                    response.put("Status","200");
                    response.put("message","User with name "+user.getFirstName() + " deleted successfully");
                    return new ResponseEntity<>(response,HttpStatus.OK);

        }else {
            response.put("error_message","Not allowed");
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping("/facebook/signout")
    public ResponseEntity<?> logoutUser(){
        ResponseCookie cookie = CustomAuthenticationFilter.getCleanJetCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You have been signed out");
    }
}
class AddRoleToUserBody{
    private String email;
    private String name;

    public AddRoleToUserBody() {
    }

    public AddRoleToUserBody(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
 class DeleteUserBody{
    private Long userId;
    private String email;

     public DeleteUserBody(Long userId, String email) {
         this.userId = userId;
         this.email = email;
     }

     public Long getUserId() {
         return userId;
     }

     public String getEmail() {
         return email;
     }
 }

