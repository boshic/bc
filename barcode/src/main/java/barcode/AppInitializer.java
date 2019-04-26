package barcode;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected Class[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return null;
    }

    @Override
    protected void registerDispatcherServlet(ServletContext servletContext) {
        super.registerDispatcherServlet(servletContext);
//        servletContext.addListener(new SessionEventListener());
    }
}
