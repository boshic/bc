package barcode.http;


import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.RequestHandledEvent;

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

        System.out.println("-- RequestHandledEvent --");
        System.out.println(e);
    }
}
