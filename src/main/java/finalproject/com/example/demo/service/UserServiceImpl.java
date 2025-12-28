package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.user.AdminCreateUserRequest;
import finalproject.com.example.demo.dto.user.UpdateProfileRequest;
import finalproject.com.example.demo.dto.user.UserResponse;
import finalproject.com.example.demo.entity.Role;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.mapper.UserMapper;
import finalproject.com.example.demo.repository.RoleRepository;
import finalproject.com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserResponse> findAllUsers() {
        return userMapper.toResponse(userRepository.findAll());
    }

    //*f
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (user.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is blocked");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole())) // ROLE_ADMIN
                        .toList()
        );
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
                return userRepository.findByEmail(springUser.getUsername());
            }
        }
        return null;
    }

    @Override
    public UserResponse getCurrentUserProfile() {
        User current = getCurrentUser();
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userMapper.toResponse(current);
    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User current = getCurrentUser();
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User byEmail = userRepository.findByEmail(request.getEmail());
        if (byEmail != null && !byEmail.getId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is occupied!");
        }

        current.setEmail(request.getEmail());
        current.setFullName(request.getFullName());
        userRepository.save(current);

        return userMapper.toResponse(current);
    }

    @Override
    public Boolean register(String email, String password, String repeatPassword, String fullName) {
        if (userRepository.findByEmail(email) != null) {
            return null;
        }

        if (!password.equals(repeatPassword)) {
            return false;
        }

        Role userRole = roleRepository.findByRole("ROLE_USER");
        if (userRole == null) {
            throw new IllegalStateException("ROLE_USER not found.");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setFullName(fullName);
        newUser.setRoles(List.of(userRole));
        newUser.setBlocked(false);
        newUser.setCreatedAt(LocalDateTime.now());

        userRepository.save(newUser);
        return true;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String repeatNewPassword) {
        User current = getCurrentUser();
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        if (!passwordEncoder.matches(oldPassword, current.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password mismatch");
        }

        if (!newPassword.equals(repeatNewPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords mismatch");
        }

        current.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(current);
    }

    @Override
    public UserResponse createUser(AdminCreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email occupied");
        }

        Role role = roleRepository.findByRole(request.getRole());
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of(role));

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public void blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setBlocked(true);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setBlocked(false);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    @PostConstruct
    public void printHash() {
        System.out.println(
                new BCryptPasswordEncoder().encode("Admin123!")
        );
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



}







//package finalproject.com.example.demo.service;
//
//import finalproject.com.example.demo.entity.Role;
//import finalproject.com.example.demo.entity.User;
//import finalproject.com.example.demo.repository.RoleRepository;
//import finalproject.com.example.demo.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Objects;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(username);
//        if (Objects.nonNull(user)) {
//            return user;
//        }
//        throw new UsernameNotFoundException("User Not Found");
//    }
//
//    @Override
//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof User) {
//                return (User) principal;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public Boolean register(String email, String password, String repeatPassword, String fullName) {
//        User existing = userRepository.findByEmail(email);
//
//        // lecture-style return values:
//        // null  -> email exists
//        // false -> password mismatch
//        // true  -> success
//        if (existing != null) {
//            return null;
//        }
//
//        if (!password.equals(repeatPassword)) {
//            return false;
//        }
//
//        Role userRole = roleRepository.findByRole("ROLE_USER");
//        if (userRole == null) {
//            throw new IllegalStateException("ROLE_USER not found. Roles must be seeded first.");
//        }
//
//        User newUser = new User();
//        newUser.setEmail(email);
//        newUser.setPassword(passwordEncoder.encode(password));
//        newUser.setFullName(fullName);
//        newUser.setRoles(List.of(userRole));
//        newUser.setBlocked(false);
//        newUser.setCreatedAt(LocalDateTime.now());
//
//        userRepository.save(newUser);
//        return true;
//    }
//
//    @Override
//    public Boolean changePassword(String oldPassword, String newPassword, String repeatNewPassword) {
//        User currentUser = getCurrentUser();
//
//        // lecture-style:
//        // null -> unauthorized OR old password mismatch
//        // false -> new passwords mismatch
//        // true -> success
//        if (currentUser == null) {
//            return null;
//        }
//
//        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
//            return null;
//        }
//
//        if (!newPassword.equals(repeatNewPassword)) {
//            return false;
//        }
//
//        currentUser.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(currentUser);
//
//        return true;
//    }
//}
