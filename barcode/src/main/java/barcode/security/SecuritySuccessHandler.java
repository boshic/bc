package barcode.security;

import barcode.dao.entities.User;
import barcode.dao.services.UserHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecuritySuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private UserHandler userHandler;

        SecuritySuccessHandler() {}
        SecuritySuccessHandler(UserHandler userHandler) {

            this.userHandler = userHandler;
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

//        final int SESSION_TIMEOUT_IN_SECONDS = 5;

        User user = userHandler.getUserByName(request.getParameterValues("username")[0]);
//        request.getSession().setMaxInactiveInterval(SESSION_TIMEOUT_IN_SECONDS);

//        if(user.getName().equals("Админ"))
//            authentication.setAuthenticated(false);
        response.sendRedirect("/mainPage");

    }
}

