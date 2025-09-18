package ru.otus.java.processors;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.otus.java.HttpRequest;
import ru.otus.java.application.ItemsServiceTemplate;
import ru.otus.java.application.dtos.Item;
import ru.otus.java.error.BadParametersException;
import ru.otus.java.error.BadRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EditItemProcessor implements RequestProcessor {

    ItemsServiceTemplate itemsServiceTemplate = new ItemsServiceTemplate();

    public EditItemProcessor() {
    }

    @Override
    public void execute(HttpRequest request, OutputStream output, int maxResponseSize) throws IOException {
        Item item;
        Gson gson = new Gson();
        try {
            item = gson.fromJson(request.getBody(), Item.class);
        } catch (JsonSyntaxException e){
            throw new BadRequestException("Неправильный текст запроса", "INCORRECT_INPUT_DATA");
        }
        if (item==null) {
            throw new BadRequestException("Неправильный текст запроса", "INCORRECT_INPUT_DATA");
        }
        if (item.getTitle()==null){
            throw new BadRequestException("Отсутствует обязательное поле title", "INCORRECT_INPUT_DATA");
        }
        if (item.getPrice()==null){
            throw new BadRequestException("Отсутствует обязательное поле price", "INCORRECT_INPUT_DATA");
        }

//        item = itemsServiceTemplate.getById(Integer.valueOf(request.getParameter("id")));

        itemsServiceTemplate.updateItem(item);
        String jsonItem = gson.toJson(item);
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                jsonItem;

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

        if (responseBytes.length > maxResponseSize) {
            throw new BadParametersException("Ответ превышает maxLarge", "INCORRECT_DATA");
        }

        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
