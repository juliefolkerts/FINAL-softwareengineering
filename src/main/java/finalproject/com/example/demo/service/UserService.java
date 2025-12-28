package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.user.AdminCreateUserRequest;
import finalproject.com.example.demo.dto.user.UpdateProfileRequest;
import finalproject.com.example.demo.dto.user.UserResponse;
import finalproject.com.example.demo.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<UserResponse> findAllUsers();

    Boolean register(String email, String password, String repeatPassword, String fullName);

    void changePassword(String oldPassword, String newPassword, String repeatNewPassword);

    User getCurrentUser();

    UserResponse getCurrentUserProfile();

    UserResponse updateProfile(UpdateProfileRequest request);

    UserResponse createUser(AdminCreateUserRequest request);

    void blockUser(Long id);

    void unblockUser(Long id);

    void deleteUser(Long id);

    User getUserByEmail(String email);

}



//package finalproject.com.example.demo.service;
//
//import finalproject.com.example.demo.entity.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//
//public interface UserService extends UserDetailsService {
//
//    Boolean register(String email, String password, String repeatPassword, String fullName);
//
//    Boolean changePassword(String oldPassword, String newPassword, String repeatNewPassword);
//
//    User getCurrentUser();
//}
