package ru.otus.java.processors;

import com.google.gson.Gson;
import ru.otus.java.HttpRequest;
import ru.otus.java.application.dtos.Item;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class GetItemInfoProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        long id = Long.parseLong(request.getParameter("id"));

        Gson gson = new Gson();
        Item item = new Item(id, "Milk", BigDecimal.valueOf(90 + id % 20));
        String jsonItem = gson.toJson(item);

        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                jsonItem;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
