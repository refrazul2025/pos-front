package org.palina.venta_ui.service;

import org.palina.venta_ui.dto.ApiResponseDto;
import org.palina.venta_ui.dto.OutletDto;
import org.palina.venta_ui.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OutletService {

    private RestTemplate restTemplate;

    @Autowired
    public OutletService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OutletDto getOutlet(UserDto userDto){
        String url = "http://localhost:9000/api/v1/outlet/getByUser";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);
            ResponseEntity<ApiResponseDto<OutletDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<ApiResponseDto<OutletDto>>() {}
            );

            OutletDto outlet = null;

            ApiResponseDto<OutletDto> apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.getResultado() != null) {
                outlet = apiResponse.getResultado();
            }

            return outlet;

        } catch (Exception e) {
            return null;
        }
    }
}
