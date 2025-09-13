package ru.otus.java;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.error.BadParametersException;
import ru.otus.java.error.BadRequestException;
import ru.otus.java.error.ErrorDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private int port;
    private int maxRequestSize;
    private int maxResponseSize;
    private Dispatcher dispatcher;
    private volatile boolean running = true;
    private static final Logger log = LogManager.getLogger(HttpServer.class.getName());
    private ExecutorService service;
    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public HttpServer(int port, int maxRequestSize, int maxResponseSize, int threadPoolSize) {
        this.port = port;
        this.maxRequestSize = maxRequestSize;
        this.maxResponseSize = maxResponseSize;
        this.dispatcher = new Dispatcher();
        this.service = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Сервер запущен на порту: " + port);
            while (running) {
                Socket socket = serverSocket.accept();
                service.submit(()->{
                    try(socket){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        try {
                            HttpRequest request = HttpRequest.parseRequest(reader);
                            log.info("METHOD: " + request.getMethod());
                            log.info("URI: " + request.getUri());
                            log.info("PARAMETERS: " + request.getParameters());
                            dispatcher.execute(request, socket.getOutputStream());
                        } catch (BadRequestException e) {
                            send400(socket, e.getCode(), e.getMessage());
                        } catch (BadParametersException e) {
                            send400(socket, e.getCode(), e.getMessage());
                        } catch (StringIndexOutOfBoundsException e) {
                            send400(socket, "BAD_PARAMETERS", "INPUT_INCORRECT");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
    }


    private void send400(Socket socket, String code, String message)  throws IOException {
        ErrorDto errorDto = new ErrorDto(code, message);
        Gson gson = new Gson();
        String response = "" +
                "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                gson.toJson(errorDto);
        socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
        socket.getOutputStream().flush();
    }
}
