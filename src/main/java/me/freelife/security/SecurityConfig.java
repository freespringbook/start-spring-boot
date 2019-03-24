package me.freelife.security;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        log.info("build Auth global........");

        auth.inMemoryAuthentication().withUser("manager").password("{noop}1111").roles("MANAGER");
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

        http
            .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN");

        //로그인 페이지
        http.formLogin().loginPage("/login");

        //접근 권한 없음 페이지 처리
        http.exceptionHandling().accessDeniedPage("/accessDenied");

        //세션 무효화
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);
    }


}
