package me.freelife.security;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Log
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        log.info("build Auth global........");

        // auth.inMemoryAuthentication().withUser("manager").password("{noop}1111").roles("MANAGER");
        String query1 = "SELECT uid username, upw password, true enabled FROM tbl_members WHERE uid= ?";

        String query2 = "SELECT member uid, role_name role FROM tbl_member_roles WHERE member = ?";

        /* 스프링 시큐리티에서는 식별 데이터를 username 이라는 용어로 사용 */
        auth.jdbcAuthentication() // JdbcUserDetailsManagerConfigurer 객체를 반환
                .passwordEncoder(passwordEncoder())
                .dataSource(dataSource) // DataSource 주입
                .usersByUsernameQuery(query1) // username을 이용해 특정한 인증 주체(사용자) 정보를 세팅
                .rolePrefix("ROLE_")
                .authoritiesByUsernameQuery(query2); // username을 이용해서 권한에 대한 정보를 조회
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
