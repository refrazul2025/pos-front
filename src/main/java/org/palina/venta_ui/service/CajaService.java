package org.palina.venta_ui.service;

import org.palina.venta_ui.dto.ApiResponseDto;
import org.palina.venta_ui.dto.CajaDto;
import org.palina.venta_ui.dto.OutletDto;
import org.palina.venta_ui.dto.ProductoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CajaService {

    private RestTemplate restTemplate;

    @Autowired
    public CajaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CajaDto> obtenerSaldoActual() {
        if(restTemplate == null){
            restTemplate = new RestTemplate();
        }
        String url = "http://localhost:9000/api/v1/caja";

        try {
            ResponseEntity<ApiResponseDto<List<CajaDto>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponseDto<List<CajaDto>>>() {}
            );

            ApiResponseDto<List<CajaDto>> cajaResponse = response.getBody();

            return cajaResponse.getResultado();
        } catch (Exception e) {
            return null;
        }
    }
}
