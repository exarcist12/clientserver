package ru.otus.java.processors;

import ru.otus.java.HttpRequest;
import ru.otus.java.error.BadParametersException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HelloWorldProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output, int maxResponseSize) throws IOException {
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>Hello World!!!</h1></body></html>";

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

        if (responseBytes.length > maxResponseSize) {
            throw new BadParametersException("Ответ превышает maxLarge", "INCORRECT_DATA");
        }

        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
