package com.techshop.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PasswordEncoderTest {
    @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Samo";
        String encodePassword = encoder.encode(rawPassword);
        System.out.println(encodePassword);
        boolean matches = encoder.matches(rawPassword,encodePassword);
        assertThat(matches).isTrue();
    }
}
