package ru.otus.java;

import ru.otus.java.processors.*;
import java.io.IOException;
import java.io.OutputStream;
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
        processors.get(request.getUri()).execute(request, output);
    }
}
