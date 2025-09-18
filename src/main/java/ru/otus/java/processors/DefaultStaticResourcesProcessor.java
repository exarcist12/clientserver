package ru.otus.java.processors;

import ru.otus.java.HttpRequest;
import ru.otus.java.error.BadParametersException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultStaticResourcesProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest httpRequest, OutputStream output, int maxResponseSize) throws IOException {
        String filename = httpRequest.getUri().substring(1);
        Path filePath = Paths.get("static/", filename);
        byte[] fileData = Files.readAllBytes(filePath);

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + fileData.length + "\r\n" +
                "\r\n";

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

        if (responseBytes.length > maxResponseSize) {
            throw new BadParametersException("Ответ превышает maxLarge", "INCORRECT_DATA");
        }

        output.write(response.getBytes());
        output.write(fileData);
    }
}
