package ru.otus.java;

import com.google.gson.Gson;
import ru.otus.java.error.BadRequestException;
import ru.otus.java.error.ErrorDto;
import ru.otus.java.processors.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundProcessor;

    public Dispatcher() {
        this.processors = new HashMap<>();
        this.processors.put("/", new HelloWorldProcessor());
        this.processors.put("/calculator", new CalculatorProcessor());
        this.processors.put("/items", new GetItemInfoProcessor());
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
    }

    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (!processors.containsKey(request.getUri())) {
            defaultNotFoundProcessor.execute(request, output);
            return;
        }

        try {
            processors.get(request.getUri()).execute(request, output);
        } catch (BadRequestException e){
            ErrorDto errorDto = new ErrorDto(e.getCode() , e.getMessage());
            Gson gson = new Gson();
            String response = "" +
                    "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    gson.toJson(errorDto);
            output.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
