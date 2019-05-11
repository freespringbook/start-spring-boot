package me.freelife;

import me.freelife.interceptor.LoginCheckInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by freejava1191@gmail.com on 2019-05-11
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
public class InterceptorConfig implements WebMvcConfigurer {

    /* 어떠한 URI에 동작하는지에 대한 설정이 필요하므로 addInterceptors를 오버라이드 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /* LoginCheckInterceptor를 '/login' 경로 호출 시 동작하도록 설정 */
        registry.addInterceptor(new LoginCheckInterceptor()).addPathPatterns("/login");

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
