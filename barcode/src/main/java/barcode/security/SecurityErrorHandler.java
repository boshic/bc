package barcode.security;

//import barcode.httpUtils.SessionService;
import barcode.httpUtils.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

        if (exception.getMessage().equals("Maximum sessions of 1 for this principal exceeded")) {
            ApplicationContext ctx = WebApplicationContextUtils
                                        .getWebApplicationContext(request.getSession()
                                        .getServletContext());
            SessionService sessionService = (SessionService) ctx.getBean("expUserService");
            sessionService.expireUserSessions(request.getParameterValues("username")[0]);
//            SessionRegistry sessionRegistry = (SessionRegistry) ctx.getBean("sessionRegistry");
//            String username = request.getParameterValues("username")[0];
//            for (Object principal : sessionRegistry.getAllPrincipals()) {
//                if (principal instanceof User) {
//                    UserDetails userDetails = (UserDetails) principal;
//                    if (userDetails.getUsername().equals(username)) {
//                        for (SessionInformation information : sessionRegistry
//                                .getAllSessions(userDetails, true)) {
//
//                            information.expireNow();
//                        }
//                    }
//                }
//            }
//
        }
        response.sendRedirect("/home");
    }
}
