package me.freelife.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by freejava1191@gmail.com on 2019-05-11
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Slf4j
public class LoginCheckInterceptor extends HandlerInterceptorAdapter {

    /* 컨트롤러의 호출전에 동작 */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("preHandler.........................................");

        String dest = request.getParameter("dest");

        /* 파라미터의 이름에 dest가 존재한다면 이를 HttpSession에 저장 */
        if(dest != null){
            request.getSession().setAttribute("dest", dest);
        }

        return super.preHandle(request, response, handler);
    }
}
