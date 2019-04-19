package barcode.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xlinux on 02.03.18.
 */
public class SecuritySuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private final Integer SESSION_TIMEOUT_IN_SECONDS = 5;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
                    throws ServletException, IOException {

        request.getSession().setMaxInactiveInterval(SESSION_TIMEOUT_IN_SECONDS);
        response.sendRedirect("/mainPage");

    }
}

