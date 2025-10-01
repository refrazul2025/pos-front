package org.palina.venta_ui.service;

import org.palina.venta_ui.dto.ApiResponseDto;
import org.palina.venta_ui.dto.OutletDto;
import org.palina.venta_ui.dto.ProductoDto;
import org.palina.venta_ui.dto.VentaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VentaService {

    private RestTemplate restTemplate;

    @Autowired
    public VentaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public VentaDto generarVenta(VentaDto ventaDto) {
        String url = "http://localhost:9000/api/v1/sale/cash";
        VentaDto ventaRes = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<VentaDto> request = new HttpEntity<>(ventaDto, headers);
            ResponseEntity<ApiResponseDto<VentaDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<ApiResponseDto<VentaDto>>() {}
            );


            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al crear producto: HTTP " + response.getStatusCode());
            }

            ApiResponseDto<VentaDto> ventaResponse = response.getBody();
            ventaRes = ventaResponse.getResultado();

        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar la venta: " + e.getMessage(), e);
        }

        return ventaRes;
    }
}