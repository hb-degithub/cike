package com.cike.backend;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class ImageController {

    /**
     * 浏览器访问：http://127.0.0.1:8080/getImage
     * 浏览器直接渲染图片
     */
    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage() throws IOException {
        // 读取resources/static/img/demo.jpg
        ClassPathResource resource = new ClassPathResource("static/img/demo.jpg");
        try (InputStream in = resource.getInputStream()) {
            byte[] bytes = in.readAllBytes();
            return ResponseEntity
                    .ok()
                    .body(bytes);
        }
    }
}
