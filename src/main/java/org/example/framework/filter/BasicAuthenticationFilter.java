package org.example.framework.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.AuthenticationException;
import org.example.framework.security.AuthenticationProvider;
import org.example.framework.security.TokenAuthentication;
import org.example.framework.util.AuthenticationHelper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


public class BasicAuthenticationFilter extends HttpFilter {
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

        final var authorization = req.getHeader("Authorization");

        if (authorization == null) {
            super.doFilter(req, res, chain);
            return;
        }

        final var basicPattern = Pattern.compile("^Basic (?<basicAuth>\\S+)$");
        final var matcher = basicPattern.matcher(authorization);
        if (!matcher.matches()) {
            super.doFilter(req, res, chain);
            return;
        }

        final var basicAuth = URLDecoder.decode(matcher.group("basicAuth"), StandardCharsets.UTF_8);

        final var basicAuthSplit = basicAuth.split(":");

        if(basicAuthSplit.length != 2) {
            res.sendError(401);
            return;
        }

        try {
            final var authentication = provider.basicAuthenticate(new TokenAuthentication(basicAuth, null));
            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }

        super.doFilter(req, res, chain);
    }
}
