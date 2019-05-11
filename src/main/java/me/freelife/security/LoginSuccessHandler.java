package me.freelife.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by freejava1191@gmail.com on 2019-05-11
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * 로그인 성공시 동작 처리
 */
@Slf4j
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {

        log.info("-----------------determineTargetUrl----------------------");

        Object dest = request.getSession().getAttribute("dest");

        String nextURL = null;

        /*
        * HttpSession에 dest 값이 존재하는 경우 redirect의 경로를 dest 값으로 지정해 준다
        * 존재하지 않으면 기존 방식으로 동작
        **/
        if(dest != null) {
            request.getSession().removeAttribute("dest");

            nextURL = (String) dest;
        } else {
            nextURL = super.determineTargetUrl(request, response);
        }
        log.info("-----------------"+nextURL+"----------------------");
        return super.determineTargetUrl(request, response);
    }
}
