package ru.otus.java.processors;

import com.google.gson.Gson;
import ru.otus.java.HttpRequest;
import ru.otus.java.application.ItemsServiceTemplate;
import ru.otus.java.application.dtos.Item;
import ru.otus.java.error.BadParametersException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DeleteItemInfoProcessor implements RequestProcessor {


    ItemsServiceTemplate itemsServiceTemplate = new ItemsServiceTemplate();

    public DeleteItemInfoProcessor() {
    }

    @Override
    public void execute(HttpRequest request, OutputStream output, int maxResponseSize) throws IOException {

        List<Item> items = itemsServiceTemplate.getAllItems();

        Integer id = 0;
        if (request.containsParameter("id")) {

            try {
                id = Integer.valueOf(request.getParameter("id"));
            } catch (NumberFormatException e){
                throw new BadParametersException("Параметр id должен быть Integer", "INCORRECT_DATA");
            }
            Integer finalId = id;
            items.stream()
                    .filter(i -> i.getId() == finalId)
                    .findFirst()
                    .orElseThrow(() -> new BadParametersException("Пользователя с данным id не существует", "INCORRECT_DATA"));

        } else {
            try {
                id = Integer.valueOf(request.getAddResource().split("/")[1]);
            } catch (NumberFormatException e){
                throw new BadParametersException("Параметр id должен быть Integer", "INCORRECT_DATA");
            }

            Integer finalId = id;
            items.stream()
                    .filter(i -> i.getId() == finalId)
                    .findFirst()
                    .orElseThrow(() -> new BadParametersException("Пользователя с данным id не существует", "INCORRECT_DATA"));

           itemsServiceTemplate.deleteItem(id);
            String response = "" +
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    "Пользователь удален";

            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

            if (responseBytes.length > maxResponseSize) {
                throw new BadParametersException("Ответ превышает maxLarge", "INCORRECT_DATA");
            }

            output.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
