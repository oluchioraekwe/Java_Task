package com.facebook.facebookapi.service;

import com.facebook.facebookapi.entity.Gender;
import com.facebook.facebookapi.entity.Role;
import com.facebook.facebookapi.entity.User;
import com.facebook.facebookapi.exception.ResourceNotFoundException;
import com.facebook.facebookapi.repository.RoleRepository;
import com.facebook.facebookapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserServiceImpl() {
    }

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,BCryptPasswordEncoder bCryptPasswordEncoder ) {
        super();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserServiceImpl(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    public UserServiceImpl(RoleRepository roleRepository) {
        super();
        this.roleRepository = roleRepository;
    }

    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User not found in database");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
    @Override
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        Role savedRole = roleRepository.findByName(role.getName());
        if(savedRole != null){
            throw new RuntimeException("Role exist in database");
        }
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        User savedUser = userRepository.findByEmail(email);
        Role savedRoles = roleRepository.findByName(roleName);
        savedUser.getRoles().add(savedRoles);

    }

    @Override
    public User getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User","id",id));
        return user;
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user,Long id) {
        User savedUser = userRepository.findById(id).get();


        String firstName = user.getFirstName() != null ? user.getFirstName() : savedUser.getFirstName();
        String lastName = user.getLastName() != null ? user.getLastName() : savedUser.getLastName();
        String savedEmail = user.getEmail() != null ? user.getEmail() : savedUser.getEmail();
        String password = user.getPassword() != null ? user.getPassword() : savedUser.getPassword();
        String country = user.getCountry() != null ? user.getCountry() : savedUser.getCountry();
        Date dob = user.getDob() != null ? user.getDob() : savedUser.getDob();
        Gender gender = user.getGender() != null ? user.getGender() : savedUser.getGender();

        savedUser.setFirstName(firstName);
        savedUser.setLastName(lastName);
        savedUser.setPassword(bCryptPasswordEncoder.encode(password));
        savedUser.setEmail(savedEmail);
        savedUser.setCountry(country);
        savedUser.setDob(dob);
        savedUser.setGender(gender);

        return  userRepository.save(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User","id",id));
        userRepository.delete(user);
    }


}
