package com.techshop.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService(){
        return new TechShopUserDetailsService();
    }


    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users/**","/settings/**","/countries/**","/state/**").hasAuthority("Admin")
                .antMatchers("/categories/**","/brands/**","/articles/**","/menus/**").hasAnyAuthority("Admin","Editor")

                .antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin","Editor")
                .antMatchers("/products/edit/**", "/products/save","/products/check_unique")
                    .hasAnyAuthority("Admin","Salesperson","Editor")
                .antMatchers("/products","/products/","/products/detail/**","/products/page/**")
                    .hasAnyAuthority("Admin","Salesperson","Editor","Shipper")
                .antMatchers("/products/**").hasAnyAuthority("Admin","Editor")

                .antMatchers("/customers/**","/shipping/**","/reports/**","/get_shipping_cost").hasAnyAuthority("Admin","Salesperson")
                .antMatchers("/orders/**").hasAnyAuthority("Admin","Salesperson","Shipper")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .permitAll()
                .and()
                .logout().permitAll()
                .and()
                .rememberMe().key("AbcDifgHijklmnopqrs_1234567890")
                .tokenValiditySeconds(7*24*60*60)
                .and()
                .headers().frameOptions().sameOrigin();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**","/webjars/**");
    }
}
