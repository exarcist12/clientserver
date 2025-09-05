package ru.otus.java;

import com.google.gson.Gson;
import ru.otus.java.application.ItemsRepository;
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
    private RequestProcessor defaultStaticResourcesProcessor;

    public Dispatcher() {
        ItemsRepository itemsRepository = new ItemsRepository();
        this.processors = new HashMap<>();
        this.processors.put("GET /", new HelloWorldProcessor());
        this.processors.put("GET /calculator", new CalculatorProcessor());
        this.processors.put("GET /items", new GetItemInfoProcessor());
        this.processors.put("POST /items", new CreateNewItemProcessor());
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultStaticResourcesProcessor = new DefaultStaticResourcesProcessor();
    }

    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (!processors.containsKey(request.getRoutingKey())) {
            defaultNotFoundProcessor.execute(request, output);
            return;
        }

        try {
            processors.get(request.getRoutingKey()).execute(request, output);
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
