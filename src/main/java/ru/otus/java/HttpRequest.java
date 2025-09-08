package ru.otus.java;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.error.BadParametersException;
import ru.otus.java.error.BadRequestException;
import ru.otus.java.error.ErrorDto;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final Logger log = LogManager.getLogger(HttpRequest.class.getName());

    private String rawRequest;
    private String method;
    private String uri;
    private String body;
    private Map<String, String> parameters;
    private Map<String, String> headers  = new HashMap<>();
    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
    public String getBody() {
        return body;
    }
    public String getRoutingKey() {
        return method + " " + uri;
    }
    public String getParameter(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getParameters(){
        return this.parameters;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.parameters = new HashMap<>();
        this.parse();
    }

    private void parse() {
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
        method = rawRequest.substring(0, startIndex);
        uri = rawRequest.substring(startIndex + 1, endIndex);
        body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
        String[] parts = rawRequest.split("\r\n", 2);
        String heads = parts[1];
        String[] partsHeads = heads.split("\r\n\r\n", 2);
        String onlyHeads = partsHeads[0];
        String[] pairHeads = onlyHeads.split("\r\n");
        for(String h : pairHeads){
            String[] keyValue = h.split(": ");
            headers.put(keyValue[0], keyValue[1]);
        }

        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            uri = elements[0];
            String[] keysValues = elements[1].split("&");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                try {
                    parameters.put(keyValue[0], keyValue[1]);
                } catch (ArrayIndexOutOfBoundsException e){
                    throw new BadParametersException("Ошибка параметров", "INCORRECT PARAMETERS");
                }
            }
        }
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }
}

