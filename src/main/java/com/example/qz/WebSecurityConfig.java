package com.example.qz;

import com.example.qz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/registration").not().fullyAuthenticated()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/home/**").hasRole("USER")
                .antMatchers("/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
                .defaultSuccessUrl("/home")
                .permitAll()
                .and()
            .httpBasic()
            .and()
            .csrf().ignoringAntMatchers("/files/**");

    }

//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("user")
//            .roles("USER")
//            .build();
//
//        UserDetails user2 = User.withDefaultPasswordEncoder()
//            .username("user2")
//            .password("user2")
//            .roles("USER")
//            .build();
//
//        UserDetails admin = User.withDefaultPasswordEncoder()
//            .username("admin")
//            .password("admin")
//            .roles("ADMIN")
//            .build();
//
//        return new InMemoryUserDetailsManager(user, user2, admin);
//    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }
}
