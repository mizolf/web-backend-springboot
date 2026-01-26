package com.mcesnik.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

/**
 * SPA-friendly CSRF token request handler.
 * Forces token to be loaded on every request, ensuring the cookie is always set.
 * This fixes timing issues with SameSite=Strict cookies in SPA applications.
 */
public final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
    private final CsrfTokenRequestHandler delegate = new CsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        this.delegate.handle(request, response, csrfToken);
        // Force token to be loaded - ensures cookie is always set
        csrfToken.get();
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        // Read from header (X-XSRF-TOKEN) for AJAX requests, otherwise from form parameter
        String header = request.getHeader(csrfToken.getHeaderName());
        return StringUtils.hasText(header) ? header : csrfToken.getToken();
    }
}