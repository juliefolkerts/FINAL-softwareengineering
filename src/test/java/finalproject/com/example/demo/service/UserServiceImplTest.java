package finalproject.com.example.demo.service;

import finalproject.com.example.demo.dto.user.UserResponse;
import finalproject.com.example.demo.entity.Role;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.mapper.UserMapper;
import finalproject.com.example.demo.repository.RoleRepository;
import finalproject.com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("encoded");
        user.setFullName("User One");
        user.setBlocked(false);
        user.setCreatedAt(LocalDateTime.now());

        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(1L, "user@example.com", "User One", false, user.getCreatedAt(), List.of("ROLE_USER"))
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loadUserByUsernameReturnsUserWhenFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        assertThat(userService.loadUserByUsername("user@example.com")).isEqualTo(user);
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("missing@example.com"));
    }

    @Test
    void getCurrentUserReturnsPrincipalWhenAuthenticated() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThat(userService.getCurrentUser()).isEqualTo(user);
    }

    @Test
    void getCurrentUserReturnsNullWhenAnonymous() {
        Authentication anonymous = new AnonymousAuthenticationToken(
                "key",
                "anon",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        when(securityContext.getAuthentication()).thenReturn(anonymous);
        SecurityContextHolder.setContext(securityContext);

        assertNull(userService.getCurrentUser());
    }

    @Test
    void registerReturnsNullWhenEmailExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        assertNull(userService.register(user.getEmail(), "pass", "pass", user.getFullName()));
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void registerReturnsFalseWhenPasswordsMismatch() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        Boolean result = userService.register(user.getEmail(), "one", "two", user.getFullName());

        assertThat(result).isFalse();
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerPersistsUserWhenValid() {
        Role role = new Role();
        role.setRole("ROLE_USER");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(roleRepository.findByRole("ROLE_USER")).thenReturn(role);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");

        Boolean result = userService.register(user.getEmail(), "secret", "secret", user.getFullName());

        assertThat(result).isTrue();
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePasswordThrowsWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.changePassword("old", "new", "new"));
        assertThat(ex.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void changePasswordThrowsWhenOldPasswordMismatch() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(passwordEncoder.matches("old", user.getPassword())).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.changePassword("old", "new", "new"));

        assertThat(ex.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(passwordEncoder).matches("old", user.getPassword());
    }

    @Test
    void changePasswordThrowsWhenNewPasswordsMismatch() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(passwordEncoder.matches("old", user.getPassword())).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.changePassword("old", "new", "different"));

        assertThat(ex.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(passwordEncoder).matches("old", user.getPassword());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordUpdatesPasswordWhenValid() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(passwordEncoder.matches("old", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("encodedNew");
        SecurityContextHolder.setContext(securityContext);

        userService.changePassword("old", "new", "new");

        verify(passwordEncoder).encode("new");
        verify(userRepository).save(user);
    }
}
