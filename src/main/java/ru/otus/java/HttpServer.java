package ru.otus.java;

import com.google.gson.Gson;
import ru.otus.java.error.BadParametersException;
import ru.otus.java.error.BadRequestException;
import ru.otus.java.error.ErrorDto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;
    private volatile boolean running = true;

    private final ExecutorService service = Executors.newFixedThreadPool(3);
    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (running) {
                Socket socket = serverSocket.accept();
                service.submit(()->{
                    try(socket){
                        byte[] buffer = new byte[8192];
                        int n = socket.getInputStream().read(buffer);
                        String rawRequest="";
                        if(n!=-1){
                            rawRequest = new String(buffer, 0, n);
                        } else {
                            return;
                        }
                        try{
                            HttpRequest request = new HttpRequest(rawRequest);
                            request.info(true);
                            dispatcher.execute(request, socket.getOutputStream());
                        } catch (BadRequestException | BadParametersException | StringIndexOutOfBoundsException e) {
                            send400(socket);
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


    private void send400(Socket socket) throws IOException {
        ErrorDto errorDto = new ErrorDto("BAD_PARAMETERS", "INPUT_INCORRECT");
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
