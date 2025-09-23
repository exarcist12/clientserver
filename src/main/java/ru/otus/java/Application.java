package ru.otus.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application {
    private static final Logger log = LogManager.getLogger(Application.class.getName());

    public static void main(String[] args) {

        Properties properties = new Properties();
        try (InputStream input = Application.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                log.error("Не удалось найти application.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            log.error("Ошибка при загрузке application.properties", ex);
            return;
        }
            int port = Integer.parseInt(properties.getProperty("server.port", "8189"));
            int maxRequestSize = Integer.parseInt(properties.getProperty("server.max-request-size", "10240"));
            int maxResponseSize = Integer.parseInt(properties.getProperty("server.max-response-size", "10240"));
            int threadPoolSize = Integer.parseInt(properties.getProperty("server.thread-pool-size", "3"));


            new HttpServer(port, maxRequestSize, maxResponseSize, threadPoolSize).start();
        }
    }
