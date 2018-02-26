package com.timazet.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BlockingQueueServlet.class);

    private final BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        try {
            String element = blockingQueue.take();
            resp.setStatus(HttpServletResponse.SC_OK);

            int remainingCapacity = blockingQueue.remainingCapacity();
            int currentCapacity = blockingQueue.size();

            PrintWriter writer = resp.getWriter();
            writer.append("<html><body>");
            writer.append(String.format("<p>Element from queue: '%s' (%s of %s free places left)</p>",
                    element, remainingCapacity, currentCapacity + remainingCapacity));
            writer.append("</body></html>");
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred during GET processing", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        try {
            String payload = req.getReader().readLine();
            if (payload != null) {
                blockingQueue.put(payload);
                resp.setStatus(HttpServletResponse.SC_ACCEPTED);

                int remainingCapacity = blockingQueue.remainingCapacity();
                int currentCapacity = blockingQueue.size();

                PrintWriter writer = resp.getWriter();
                writer.append("<html><body>");
                writer.append(String.format("<p>%s of %s free places left</p>", remainingCapacity, currentCapacity + remainingCapacity));
                writer.append("</body></html>");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Payload should be specified");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred during GET processing", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
