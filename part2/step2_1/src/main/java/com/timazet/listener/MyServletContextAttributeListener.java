package com.timazet.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

public class MyServletContextAttributeListener implements ServletContextAttributeListener {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyServletContextAttributeListener.class);

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        log.info("Attribute is added: {} = {}", event.getName(), event.getValue());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        log.info("Attribute is removed: {}", event.getName());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        log.info("Attribute is replaced: {} = {}", event.getName(), event.getValue());
    }

}
