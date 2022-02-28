package barcode.httpUtils;

import barcode.dao.services.EntityHandlerImpl;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.RequestHandledEvent;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class RequestEventListener {

    private SessionRegistry sessionRegistry;

    public RequestEventListener(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @EventListener
    public void handleEvent (RequestHandledEvent e) {
//        SessionInformation sessionInfo = (sessionRegistry != null ? sessionRegistry
//                .getSessionInformation(event.getSession().getId()) : null);
//        final ServletContext context = getServletContext();
//        final HttpSession session = (HttpSession) context.getAttribute(sessionId);
        Date date = new Date();

        System.out.println(new Timestamp(date.getTime()));
        System.out.println(e);
//        EntityHandlerImpl.checkThisIp = ((ServletRequestHandledEvent) e).getClientAddress();
    }
}
