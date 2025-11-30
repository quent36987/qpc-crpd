package back.app.utils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import back.app.utils.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        int port = httpRequest.getServerPort();
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();

        // Filtrer les logs ici
        if (port != 9002) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                logger.info("User ID: {}, User:  {}, {} {}", userDetails.getId(), userDetails.getUsername(), method, uri);
            }
            else
                logger.info("by unknown {} {}", method, uri);
        }

        chain.doFilter(request, response);
    }
}
