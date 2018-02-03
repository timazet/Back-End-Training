package com.timazet.filter;

import javax.servlet.*;
import java.io.IOException;

public class AdvancedFilter implements Filter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdvancedFilter.class);

    public void init(FilterConfig filterConfig) {
        log.info("Filter is initialized");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("step in");

        chain.doFilter(request, response);

        log.info("step out");
    }

    public void destroy() {
        log.info("Filter is destroyed");
    }

}
