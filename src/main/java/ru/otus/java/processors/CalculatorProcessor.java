package ru.otus.java.processors;

import ru.otus.java.HttpRequest;
import ru.otus.java.error.BadParametersException;
import ru.otus.java.error.BadRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalculatorProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        int a;
        int b;
        if (!request.containsParameter("a")) {
            throw new BadRequestException("Отсутствует обязательный параметр запроса 'a'", "INCORRECT_INPUT_DATA");
        }
        if (!request.containsParameter("b")) {
            throw new BadRequestException("Отсутствует обязательный параметр запроса 'b'", "INCORRECT_INPUT_DATA");
        }
        if (request.getParameters().size()!=2) {
            throw new BadRequestException("Параметров не должно быть больше ДВУХ", "INCORRECT_INPUT_DATA");
        }

        try {
            a = Integer.parseInt(request.getParameter("a"));
        } catch (NumberFormatException e){
            throw new BadParametersException("Параметр a должен быть Integer", "INCORRECT_INPUT_DATA");
        }
        try {
            b = Integer.parseInt(request.getParameter("b"));
        } catch (NumberFormatException e){
            throw new BadParametersException("Параметр b должен быть Integer", "INCORRECT_INPUT_DATA");
        }
        String result = a + " + " + b + " = " + (a + b);

        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>" + result + "</h1></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}