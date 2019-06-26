package barcode.httpUtils;

import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;

@Component
public class SessionEventListener extends HttpSessionEventPublisher {
    // ... Прочие методы
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        super.sessionCreated(event);
        event.getSession().setMaxInactiveInterval(60*60*8);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
//        ApplicationContext ctx = WebApplicationContextUtils
//                                .getWebApplicationContext(event.getSession().getServletContext());
//        SessionRegistry sessionRegistry = (SessionRegistry) ctx.getBean("sessionRegistry");
//        SessionInformation sessionInfo = (sessionRegistry != null ?
//                sessionRegistry.getSessionInformation(event.getSession().getId()) : null);
        super.sessionDestroyed(event);
    }
}
