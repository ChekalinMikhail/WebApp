package org.example.framework.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.*;
import org.example.framework.util.AuthenticationHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class CookieAuthenticationFilter extends HttpFilter {
    private AuthenticationProvider provider;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        provider = ((AuthenticationProvider) getServletContext().getAttribute(ContextAttributes.AUTH_PROVIDER_ATTR));
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!AuthenticationHelper.authenticationIsRequired(req)) {
            super.doFilter(req, res, chain);
            return;
        }

        try {
            final var cookies = req.getCookies();
            if (cookies == null) {
                super.doFilter(req, res, chain);
                return;
            }
            final var cookie = Arrays.stream(req.getCookies())
                    .filter(c -> c.getName().equals("token"))
                    .findFirst();

            if (cookie.isPresent()) {
                final var token = cookie.get().getValue();
                final var authentication = provider.tokenAuthenticate(new TokenAuthentication(token, null));
                req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
            }
        } catch (NoSuchElementException e) {
            super.doFilter(req, res, chain);
            return;
        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }

        super.doFilter(req, res, chain);
    }
}
