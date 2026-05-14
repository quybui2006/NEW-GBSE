package demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import demo.model.User;
import demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
        }

        User user = userOpt.get();
            // THÊM DÒNG NÀY
    System.out.println("=== DEBUG LOGIN ===");
    System.out.println("Username: " + user.getUsername());
    System.out.println("Password hash: " + user.getPassword());
    System.out.println("Role: " + user.getRole());
    System.out.println("Enabled: " + user.isEnabled());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_ADMIN") 

                // .roles(user.getRole())
                .disabled(!user.isEnabled())
                .build();
    }
}