package org.example.logi;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Фильтр для установки {@link HttpServletRequest} в {@link RequestContext}.
 * Этот фильтр устанавливает текущий запрос до выполнения фильтров и сервлетов
 * и очищает его после завершения обработки.
 */
public class RequestContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        RequestContext.setRequest((HttpServletRequest) request);
        try {
            chain.doFilter(request, response);
        } finally {
            RequestContext.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}