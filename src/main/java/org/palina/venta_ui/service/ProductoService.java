package org.palina.venta_ui.service;

import org.palina.venta_ui.dto.ApiResponseDto;
import org.palina.venta_ui.dto.OutletDto;
import org.palina.venta_ui.dto.ProductoDto;
import org.palina.venta_ui.dto.UserDto;
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

    public List<ProductoDto> getProducts(OutletDto tienda) {
        if(restTemplate == null){
            restTemplate = new RestTemplate();
        }
        String url = "http://localhost:9000/api/v1/product/list/"+ tienda.getId();

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
        String url = "http://localhost:9000/api/v1/product/save";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ProductoDto> request = new HttpEntity<>(productoDto, headers);
            ResponseEntity<ApiResponseDto<ProductoDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<ApiResponseDto<ProductoDto>>() {}
            );


            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al crear producto: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el producto: " + e.getMessage(), e);
        }
    }
}
