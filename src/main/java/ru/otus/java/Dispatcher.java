package ru.otus.java;

import com.google.gson.Gson;
import ru.otus.java.error.BadRequestException;
import ru.otus.java.error.ErrorDto;
import ru.otus.java.processors.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultStaticResourcesProcessor;

    public Dispatcher() {
        this.processors = new HashMap<>();
        this.processors.put("GET /", new HelloWorldProcessor());
        this.processors.put("GET /calculator", new CalculatorProcessor());
        this.processors.put("GET /items", new GetItemInfoProcessor());
        this.processors.put("POST /items", new CreateNewItemProcessor());
        this.processors.put("PUT /items", new EditItemProcessor());
        this.processors.put("DELETE /items", new DeleteItemInfoProcessor());
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultStaticResourcesProcessor = new DefaultStaticResourcesProcessor();
    }

    public void execute(HttpRequest request, OutputStream output, int maxResponseSize) throws IOException {
        if (Files.exists(Paths.get("static/", request.getUri().substring(1)))){
            defaultStaticResourcesProcessor.execute(request, output, maxResponseSize);
            return;
        }

        if (!processors.containsKey(request.getRoutingKey())) {
            defaultNotFoundProcessor.execute(request, output, maxResponseSize);
            return;
        }

        processors.get(request.getRoutingKey()).execute(request, output, maxResponseSize);

    }
}
