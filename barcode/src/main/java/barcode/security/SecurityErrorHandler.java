package barcode.security;

import barcode.http.SessionService;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SecurityErrorHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
//        if (exception.getClass().isAssignableFrom(SessionAuthenticationException.class)) {
//            System.out.print("SecurityErrorHandler");
//        }
        if (exception.getMessage().equals("Maximum sessions of 1 for this principal exceeded")) {
            ApplicationContext ctx = WebApplicationContextUtils
                                        .getWebApplicationContext(request.getSession()
                                        .getServletContext());
            SessionService sessionService = (SessionService) ctx.getBean("expireUserService");
            sessionService.expireUserSessions(request.getParameterValues("username")[0]);
        }
        response.sendRedirect("/home");
    }
}
