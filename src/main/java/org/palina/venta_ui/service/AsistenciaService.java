package org.palina.venta_ui.service;

import org.palina.venta_ui.dto.ApiResponseDto;
import org.palina.venta_ui.dto.AsistenciaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AsistenciaService {

    private RestTemplate restTemplate;

    @Autowired
    public AsistenciaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AsistenciaDto registrarAsistencia(Long idUser) {
        String url = "http://localhost:9000/api/v1/asistencias/registrar";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("idUser", String.valueOf(idUser));

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<ApiResponseDto<AsistenciaDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<ApiResponseDto<AsistenciaDto>>() {}
            );

            AsistenciaDto asistenciaDto = null;

            ApiResponseDto<AsistenciaDto> apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.getResultado() != null) {
                asistenciaDto = apiResponse.getResultado();
            }

            return asistenciaDto;
        } catch (Exception e) {
            System.err.println("Error al registrar asistencia: " + e.getMessage());
            return null;
        }
    }

}
