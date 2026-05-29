package demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import demo.config.SecurityConfig;
import demo.model.User;
import demo.repository.UserRepository;
import demo.service.ContactRateLimiter;
import demo.service.ContactRequestService;
import demo.service.CustomUserDetailsService;
import demo.service.LoginAttemptService;
import demo.service.ProductService;

@WebMvcTest(PublicController.class)
@Import({SecurityConfig.class, CustomUserDetailsService.class})
class AuthenticationTest {

    @Autowired MockMvc mvc;

    @MockBean ProductService productService;            // used by GlobalModelAttributes (nav)
    @MockBean ContactRequestService contactRequestService;
    @MockBean ContactRateLimiter contactRateLimiter;
    @MockBean UserRepository userRepository;
    @MockBean LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        when(productService.findAll()).thenReturn(List.of());
        when(productService.getRangeGroups(org.mockito.ArgumentMatchers.any())).thenReturn(List.of());

        User admin = new User("admin", new BCryptPasswordEncoder().encode("secret"), "ADMIN");
        admin.setEnabled(true);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        // loginAttemptService.isBlocked(...) returns false by default (mock)
    }

    @Test
    void admin_whenUnauthenticated_redirectsToLogin() throws Exception {
        mvc.perform(get("/admin"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void login_withBadCredentials_redirectsToError() throws Exception {
        mvc.perform(post("/login").with(csrf())
                .param("username", "admin")
                .param("password", "wrong-password"))
           .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void login_withValidCredentials_redirectsToAdmin() throws Exception {
        mvc.perform(post("/login").with(csrf())
                .param("username", "admin")
                .param("password", "secret"))
           .andExpect(redirectedUrl("/admin"));
    }
}
