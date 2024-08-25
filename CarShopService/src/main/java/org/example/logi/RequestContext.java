package org.example.logi;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Контекст для хранения {@link HttpServletRequest} в {@link ThreadLocal}.
 * Позволяет доступ к текущему запросу из любого места в коде.
 */
public class RequestContext {
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void setRequest(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static HttpServletRequest getRequest() {
        return requestHolder.get();
    }

    public static void clear() {
        requestHolder.remove();
    }
}