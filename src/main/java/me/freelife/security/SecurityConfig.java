package me.freelife.security;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Log
@EnableWebSecurity
/** @Secured를 작동시키기 위한 설정 */
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //데이터베이스를 이용하려면 DataSource가 필요하므로 주입
    @Autowired
    DataSource dataSource;

    @Autowired
    FreelifeUserService freelifeUserService;

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

        // freelifeUserService를 사용하도록 설정
        // http.userDetailsService(freelifeUserService);

        //remember-me 설정
        // http.rememberMe().key("freelife").userDetailsService(freelifeUserService);

        //HttpSecurity 가 데이터베이스를 이용하도록 설정
        http.rememberMe()
                .key("freelife")
                .userDetailsService(freelifeUserService)
                .tokenRepository(getJDBCRepository())
                .tokenValiditySeconds(60*60*24); // 쿠키의 유효시간을 초단위로 설정하는데 사용 24시간 유지하는 쿠키 생성
    }

    /**
     * HttpSecurity에서 JdbcTokenRepositoryImpl을
     * 이용해 데이터베이스를 이용하도록 설정
     * @return
     */
    private PersistentTokenRepository getJDBCRepository(){
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // return NoOpPasswordEncoder.getInstance();
        // BCryptPasswordEncoder 구현 클래스를 사용하도록 지정
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 매니저가 PasswordEncoder를 이용할 것이라는 것을 명시
     * @param auth
     * @throws Exception
     */
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        log.info("build Auth global.......");
        auth.userDetailsService(freelifeUserService).passwordEncoder(passwordEncoder());
    }

}
