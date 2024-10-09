package cz.cvut.fel.pm2.carrentalservice.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;


@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    /**
     * Logs the details of an incoming HTTP request before it is handled.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param object the handler
     * @return true to continue processing the request
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        HttpServletRequest requestCacheWrapperObject = new ContentCachingRequestWrapper(request);
        log.info("===== Incoming Request =====");
        log.info("METHOD: " + requestCacheWrapperObject.getMethod());
        log.info("URI: " + requestCacheWrapperObject.getRequestURI());
        log.info("REMOTE ADDRESS: " + requestCacheWrapperObject.getRemoteAddr());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model){
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception){
    }
}

