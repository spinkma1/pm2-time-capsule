package cz.cvut.fel.pm2.config.security;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@UtilityClass
public class SecurityEndpoints {

    /**
     * Matcher for authenticated URLs.
     */
    final RequestMatcher AUTHENTICATED_URLS = new OrRequestMatcher(
            antMatcher(HttpMethod.GET,"/users/me")
    );

    /**
     * Matcher for admin URLs.
     */
    final RequestMatcher ADMIN_URLS = new OrRequestMatcher(
            antMatcher(HttpMethod.PUT,"/example/**")
    );

    /**
     * Matcher for member URLs.
     */
    final RequestMatcher MEMBER_URLS = new OrRequestMatcher(
            antMatcher(HttpMethod.POST, "/capsules/register"),
            antMatcher(HttpMethod.POST, "/capsules/unregister"),
            antMatcher(HttpMethod.PUT, "/capsules/lock/{id}"),
            antMatcher(HttpMethod.POST, "/content/upload/{id}"),
            antMatcher(HttpMethod.GET, "/content/{id}"),
            antMatcher(HttpMethod.PUT, "/content/update/{id}"),
            antMatcher(HttpMethod.DELETE, "/content/delete/{id}"),
            antMatcher(HttpMethod.PUT, "/capsules/{capsuleId}/addContributors/**"),
            antMatcher(HttpMethod.PUT,"/admin/**"),
            antMatcher(HttpMethod.GET,"/admin/**")
    );

    /**
     * Matcher for public URLs.
     */
    final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            antMatcher(HttpMethod.GET,"/user/token"),
            antMatcher(HttpMethod.GET,"/capsules/**"),
            antMatcher("/user/**")

    );
}
