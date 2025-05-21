
package com.example.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class DecryptRequestAdvice extends RequestBodyAdviceAdapter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        try {
            InputStream encryptedStream = inputMessage.getBody();
            String encryptedJson = new String(encryptedStream.readAllBytes(), StandardCharsets.UTF_8);
            if (encryptedJson.startsWith("\"") && encryptedJson.endsWith("\"")) {
                encryptedJson = encryptedJson.substring(1, encryptedJson.length() - 1); // remove quotes
                encryptedJson = encryptedJson.replace("\\\"", "\""); // unescape inner quotes if needed
            }
            String decryptedJson = AesUtil.decrypt(encryptedJson);

            return new HttpInputMessage() {
                @Override
                public InputStream getBody() {
                    return new ByteArrayInputStream(decryptedJson.getBytes(StandardCharsets.UTF_8));
                }

                @Override
                public org.springframework.http.HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt request", e);
        }
    }
}
