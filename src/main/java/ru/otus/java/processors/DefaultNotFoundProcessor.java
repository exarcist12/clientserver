package ru.otus.java.processors;

import ru.otus.java.HttpRequest;
import ru.otus.java.error.BadParametersException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DefaultNotFoundProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output, int maxResponseSize) throws IOException {
        String response = "" +
                "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>Resource Not Found!!!</h1></body></html>";


        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

        if (responseBytes.length > maxResponseSize) {
            throw new BadParametersException("Ответ превышает maxLarge", "INCORRECT_DATA");
        }

        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
