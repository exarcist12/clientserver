package ru.otus.java;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.error.BadParametersException;
import ru.otus.java.error.BadRequestException;
import ru.otus.java.error.ErrorDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final Logger log = LogManager.getLogger(HttpRequest.class.getName());

    private String rawRequest;
    private String method;
    private String uri;
    private String addResource;
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
    public String getAddResource() {
        return addResource;
    }
    public String getParameter(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getParameters(){
        return this.parameters;
    }



    public HttpRequest(String method, String uri, Map<String, String> headers, Map<String, String> parameters,String body){
        this.method = method;
        this.uri = uri;
        if(parameters==null){
            this.headers = new HashMap<>();
        } else  this.headers = headers;
        this.body = body;
        if(parameters==null){
            this.parameters = new HashMap<>();
        } else  this.parameters = parameters;

    }

    public HttpRequest(String method, String uri, String addResource, Map<String, String> headers, Map<String, String> parameters,String body){
        this.method = method;
        this.uri = uri;
        this.addResource = addResource;
        if(parameters==null){
            this.headers = new HashMap<>();
        } else  this.headers = headers;
        this.body = body;
        if(parameters==null){
            this.parameters = new HashMap<>();
        } else  this.parameters = parameters;

    }


    public static HttpRequest parseRequest(BufferedReader reader, int maxRequestSize) throws IOException {
        StringBuilder rawRequest = new StringBuilder();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        String line = reader.readLine();
        if (line == null) {
            throw new BadRequestException("Некорректный запрос: пустая строка или конец потока", "EMPTY_REQUEST");
        }
        if (line.isEmpty()) {
            throw new BadRequestException("Некорректный запрос: пустая строка", "EMPTY_REQUEST");
        }
        int contentLength = -1;
        int indexOfSpace = line.indexOf(" ");
        final String method = line.substring(0, indexOfSpace);
        int firstSpaceIndex = line.indexOf(" ");
        int secondSpaceIndex = line.indexOf(" ", firstSpaceIndex + 1);
        String uri = line.substring(firstSpaceIndex + 1, secondSpaceIndex);
        String addResource = null;
        String[] split = uri.split("/");
        if(split.length>0){
            uri = "/" + split[1];
        }
        if(split.length>2) {
            addResource = "/" + split[2];
        }

        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            uri = elements[0];
            String[] keysValues;
            try{
                keysValues = elements[1].split("&");
            } catch (ArrayIndexOutOfBoundsException e){
                throw new BadParametersException("Ошибка параметров", "INCORRECT PARAMETERS");
            }
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                try {
                    parameters.put(keyValue[0], keyValue[1]);
                } catch (ArrayIndexOutOfBoundsException e){
                    throw new BadParametersException("Ошибка параметров", "INCORRECT PARAMETERS");
                }
            }
        }

        while ((line = reader.readLine()) != null && !line.isEmpty()) {


            String[] keyValue = line.split(": ");
            headers.put(keyValue[0], keyValue[1]);
            rawRequest.append(line).append("\r\n");
            if (line.startsWith("Content-Length:")) {
                try {
                    contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                } catch (NumberFormatException e) {
                    log.error("Неверный формат Content-Length", e);
                    contentLength = -1;
                }
            }
            if (contentLength > maxRequestSize) {
                throw new BadRequestException("REQUEST_TOO_LARGE", "Request size exceeds maximum allowed size");
            }
        }
        rawRequest.append("\r\n");

        StringBuilder body = new StringBuilder();
        while (reader.ready()) {
            int c = reader.read();
            if (c != -1) {
                body.append((char) c);
            } else {
                break;
            }
        }
        rawRequest.append(body);

        String finalRawRequest = rawRequest.toString();
        HttpRequest httpRequest;
        if(addResource!=null){
            httpRequest = new HttpRequest(method, uri, addResource, headers, parameters, body.toString());
        } else  httpRequest = new HttpRequest(method, uri, headers, parameters, body.toString());
        return httpRequest;
    }
    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }

    public void info(boolean debug) {
        if (debug) {
            log.info(rawRequest);
        }
        log.info("METHOD: " + method);
        log.info("URI: " + uri);
        log.info("PARAMETERS: " + parameters);
    }
}

