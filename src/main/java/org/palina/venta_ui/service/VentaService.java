package org.palina.venta_ui.service;

import org.palina.venta_ui.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    private RestTemplate restTemplate;

    @Autowired
    public VentaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public VentaDto generarVenta(VentaDto ventaDto) {
        String url = "http://localhost:9000/api/v1/sale/";

        if(ventaDto.getSaleType().equalsIgnoreCase("Apartado")){
            url += "layaway";
        }else if(ventaDto.getSaleType().equalsIgnoreCase("Contado")){
            url += "cash";
        }

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

    public List<VentaDto> getApartados() {
        if(restTemplate == null){
            restTemplate = new RestTemplate();
        }
        String url = "http://localhost:9000/api/v1/sale/layaway";

        try {
            ResponseEntity<ApiResponseDto<List<VentaDto>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponseDto<List<VentaDto>>>() {}
            );

            ApiResponseDto<List<VentaDto>> ventasResponse = response.getBody();

            return ventasResponse != null && ventasResponse.getResultado() != null
                    ? ventasResponse.getResultado()
                    : new ArrayList<>();

        } catch (Exception e) {
            return null;
        }
    }

    public VentaDto agregarPago(Long idVenta, PagoDto pagoDto) {
        String url = "http://localhost:9000/api/v1/sale/addPayment/" + idVenta;


        VentaDto ventaRes = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PagoDto> request = new HttpEntity<>(pagoDto, headers);
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