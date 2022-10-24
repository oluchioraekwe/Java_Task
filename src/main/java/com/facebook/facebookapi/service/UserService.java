package com.facebook.facebookapi.service;

import com.facebook.facebookapi.entity.Role;
import com.facebook.facebookapi.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String email,String roleName);
    User getUser(Long id);
    User getUser(String email);
    List<User> getAllUsers();

    User updateUser(User user, Long id);

    void deleteUser(Long id);
}
