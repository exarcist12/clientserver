package ru.otus.java.processors;

import com.google.gson.Gson;
import ru.otus.java.HttpRequest;
import ru.otus.java.application.ItemsRepository;
import ru.otus.java.application.ItemsServiceTemplate;
import ru.otus.java.application.dtos.Item;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetItemInfoProcessor implements RequestProcessor {
    private ItemsRepository itemsRepository;

    ItemsServiceTemplate itemsServiceTemplate = new ItemsServiceTemplate();

    public GetItemInfoProcessor(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String result;
        Gson gson = new Gson();
        if (request.containsParameter("id")) {
            Integer id = Integer.parseInt(request.getParameter("id"));
            Item item = itemsRepository.getById(id);
            result = gson.toJson(item);
        } else {
 //           List<Item> items = itemsRepository.getAll();
            List<Item> items = itemsServiceTemplate.getAllItems();
            result = gson.toJson(items);
        }
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                result;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
