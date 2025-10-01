package org.palina.venta_ui.service;

import org.palina.venta_ui.dto.ApiResponseDto;
import org.palina.venta_ui.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class UserService {

    private RestTemplate restTemplate;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDto validate(String username, String password){
        String url = "http://localhost:9000/api/v1/user/validate";

        try {
            UserDto userDto = new UserDto();
            userDto.setUsername(username);
            userDto.setPassword(password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);
            ResponseEntity<ApiResponseDto<UserDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<ApiResponseDto<UserDto>>() {}
            );

            UserDto created = null;

            ApiResponseDto<UserDto> apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.getResultado() != null) {
                created = apiResponse.getResultado();
             }

            return created;

        } catch (Exception e) {
            return null;
        }
    }
}
