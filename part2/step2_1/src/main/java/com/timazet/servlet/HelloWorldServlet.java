package com.timazet.servlet;

import com.timazet.listener.MyServletContextListener;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HelloWorldServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<LocalDateTime> start = Optional.ofNullable(getServletContext().getAttribute(MyServletContextListener.START_DATE))
                .map(LocalDateTime.class::cast);

        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();

        writer.append("<html><body>");
        writer.append("<p>Hello World!</p>");
        start.ifPresent(localDateTime -> writer.append(String.format("<p>Application uptime is %s</p>",
                LocalTime.ofNanoOfDay(Duration.between(localDateTime, LocalDateTime.now()).toNanos()).format(DateTimeFormatter.ISO_LOCAL_TIME))));
        writer.append("</body></html>");
    }

}
