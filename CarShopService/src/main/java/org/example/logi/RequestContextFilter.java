//package org.example.logi;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
///**
// * Фильтр для установки {@link HttpServletRequest} в {@link RequestContext}.
// */
///**
// * Фильтр для установки и очистки {@link HttpServletRequest} в {@link RequestContext}.
// *
// * Этот фильтр перехватывает HTTP-запросы и устанавливает текущий {@link HttpServletRequest} в {@link RequestContext}
// * до того, как запрос будет передан следующему элементу в цепочке фильтров или целевому сервлету.
// * После обработки запроса фильтр очищает контекст, чтобы избежать утечек и обеспечить корректность последующих запросов.
// *
// */
//@Component
//public class RequestContextFilter implements Filter {
//
//    /**
//     * Устанавливает текущий {@link HttpServletRequest} в {@link RequestContext} и продолжает обработку запроса.
//     *
//     * Этот метод вызывается для каждого запроса, проходящего через фильтр. Он устанавливает текущий запрос
//     * в контекст для использования другими частями приложения, такими как аспекты (AOP) или сервисы. После этого
//     * запрос передается следующему элементу в цепочке фильтров или целевому сервлету. В блоке {@code finally}
//     * контекст очищается, чтобы избежать утечек и обеспечить корректность последующих запросов.
//     *
//     *
//     * @param request объект {@link ServletRequest} для обработки текущего запроса.
//     * @param response объект {@link ServletResponse} для обработки текущего ответа.
//     * @param chain цепочка фильтров для продолжения обработки запроса.
//     *
//     * @throws IOException если происходит ошибка ввода-вывода во время обработки запроса.
//     * @throws ServletException если происходит ошибка при обработке запроса.
//     */
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        RequestContext.setRequest((HttpServletRequest) request);
//        try {
//            chain.doFilter(request, response);
//        } finally {
//            RequestContext.clear();
//        }
//    }
//
//    /**
//     * Инициализация фильтра.
//     *
//     * Этот метод вызывается один раз при создании фильтра. В текущей реализации он не выполняет никаких
//     * действий и может быть оставлен пустым.
//     *
//     *
//     * @param filterConfig конфигурация фильтра.
//     */
//    @Override
//    public void init(FilterConfig filterConfig) {}
//
//    /**
//     * Завершение работы фильтра.
//     *
//     * Этот метод вызывается один раз при удалении фильтра из цепочки фильтров. В текущей реализации он не
//     * выполняет никаких действий и может быть оставлен пустым.
//     *
//     */
//    @Override
//    public void destroy() {}
//}
