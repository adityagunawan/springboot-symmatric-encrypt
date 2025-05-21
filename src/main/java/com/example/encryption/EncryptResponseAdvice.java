
package com.example.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class EncryptResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        try {
            String json = objectMapper.writeValueAsString(body);
            String encrypted = AesUtil.encrypt(json);

            // 🔥 Set content type to text/plain
            response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
            return encrypted;
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt response", e);
        }
    }
}
