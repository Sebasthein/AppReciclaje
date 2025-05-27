package com.example.reciclaje.servicio;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenFoodFactsService {
	

    private static final String API_URL = "https://world.openfoodfacts.org/api/v0/product/";

    public JsonNode getProductByBarcode(String barcode) {
        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL + barcode + ".json";
        String response = restTemplate.getForObject(url, String.class);
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la respuesta de Open Food Facts", e);
        }
    }

}
