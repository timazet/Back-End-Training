package com.timazet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.time.LocalDateTime;

public class MyServletContextListener implements ServletContextListener {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyServletContextListener.class);

    public static final String START_DATE = "START_DATE";

    public void contextInitialized(ServletContextEvent sce) {
        log.info("ServletContext is initialized");
        log.info("Registered filters: {}", sce.getServletContext().getFilterRegistrations().keySet());
        log.info("Registered servlets: {}", sce.getServletContext().getServletRegistrations().keySet());

        sce.getServletContext().setAttribute(START_DATE, LocalDateTime.now());
    }

    public void contextDestroyed(ServletContextEvent sce) {
        log.info("ServletContext is destroyed");
    }

}
