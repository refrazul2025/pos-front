package org.palina.venta_ui.service;

import org.palina.venta_ui.dto.ApiResponseDto;
import org.palina.venta_ui.dto.ProductoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoService {

    private RestTemplate restTemplate;

    @Autowired
    public ProductoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ProductoDto> getProducts() {
        if(restTemplate == null){
            restTemplate = new RestTemplate();
        }
        String url = "http://192.168.100.151:9000/api/v1/inventario/l/all";

        try {
            ResponseEntity<ApiResponseDto<List<ProductoDto>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponseDto<List<ProductoDto>>>() {}
            );

            ApiResponseDto<List<ProductoDto>> inventarioResponse = response.getBody();

            return inventarioResponse != null && inventarioResponse.getResultado() != null
                    ? inventarioResponse.getResultado()
                    : new ArrayList<>();

        } catch (Exception e) {
            return null;
        }
    }

    public void createProduct(ProductoDto productoDto) {
        String url = "http://192.168.100.151:9000/api/v1/inventario";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ProductoDto> request = new HttpEntity<>(productoDto, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(
                    url,
                    request,
                    Void.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al crear producto: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el producto: " + e.getMessage(), e);
        }
    }
}
