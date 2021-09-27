package org.example.framework.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.AnonymousProvider;
import org.example.framework.security.AuthenticationException;
import org.example.framework.security.TokenAuthentication;
import org.example.framework.util.AuthenticationHelper;

import java.io.IOException;


public class AnonymousAuthenticationFilter extends HttpFilter {
    private AnonymousProvider provider;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        provider = ((AnonymousProvider) getServletContext().getAttribute(ContextAttributes.ANON_PROVIDER_ATTR));
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!AuthenticationHelper.authenticationIsRequired(req)) {
            super.doFilter(req, res, chain);
            return;
        }

        try {
            final var authentication = provider.provide();
            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }

        super.doFilter(req, res, chain);
    }
}
