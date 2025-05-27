package com.example.reciclaje.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OpenFoodFactsResponse {
	 private Product product;
	    
	    @Data
	    public static class Product {
	        private String code; // Código de barras
	        private String product_name;
	        private String brands;
	        private String categories;
	        private String packaging;
	        
	        @JsonProperty("ecoscore_data")
	        private EcoScoreData ecoscoreData;
	    }
	    
	    @Data
	    public static class EcoScoreData {
	        private String grade; // A, B, C, D, E
	    }
	}
