package me.freelife.security;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Log
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        log.info("build Auth global...........");

        auth.inMemoryAuthentication()
                .withUser("manager")
                .password("1111")
                .roles("MANAGER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("security config.....................");

        http
            .authorizeRequests()
                .antMatchers("/guest/**").permitAll();

        http
            .authorizeRequests()
                .antMatchers("/manager/**").hasRole("MANAGER");

        http.formLogin();
    }


}
