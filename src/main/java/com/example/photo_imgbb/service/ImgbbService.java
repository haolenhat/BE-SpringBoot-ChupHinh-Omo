package com.example.photo_imgbb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class ImgbbService {

    @Value("${imgbb.api-key}")
    private String apiKey;

    private static final String UPLOAD_URL = "https://api.imgbb.com/1/upload";

    public Map<String, Object> upload(byte[] fileBytes, String fileName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // Tạo body dạng multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // Chuyển ảnh thành ByteArrayResource
        ByteArrayResource byteArrayResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName; // Tên file gửi lên imgbb
            }
        };
        body.add("image", byteArrayResource);
        body.add("key", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                UPLOAD_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        return response.getBody();
    }
}
