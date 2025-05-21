
package com.example.encryption;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DemoController {

    @PostMapping("/echo")
    public Payload echo(@RequestBody Payload payload) {
        return new Payload("Server received: " + payload.getMessage());
    }
}
