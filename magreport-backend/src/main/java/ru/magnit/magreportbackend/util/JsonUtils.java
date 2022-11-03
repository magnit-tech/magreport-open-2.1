package ru.magnit.magreportbackend.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

public class JsonUtils {
   private static ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();


   public static String getJsonFromObject (Object object){
          try {
              return objectMapper.writeValueAsString(object);
          } catch (JsonProcessingException e) {
              throw new RuntimeException(e);
          }
   }

    public static List<String> getJsonFromObjects (List<Object> objects){
        return objects
                .stream()
                .map(obj -> {
                    try {
                        return objectMapper.writeValueAsString(obj);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
