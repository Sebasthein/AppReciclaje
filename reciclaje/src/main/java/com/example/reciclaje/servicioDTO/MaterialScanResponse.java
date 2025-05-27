package com.example.reciclaje.servicioDTO;

import com.example.reciclaje.entidades.Material;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialScanResponse {
	private Material material;
    private int pointsEarned;
    private String message;
    private String QrData;
    private int Quantity;

}
