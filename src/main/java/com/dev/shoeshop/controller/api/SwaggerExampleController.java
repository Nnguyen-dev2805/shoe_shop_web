package com.dev.shoeshop.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/example")
@Tag(name = "Example API", description = "Example endpoints to demonstrate Swagger documentation")
public class SwaggerExampleController {

    @Operation(
            summary = "Get greeting message",
            description = "Returns a simple greeting message with optional name parameter"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved greeting"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    @GetMapping("/greet")
    public ResponseEntity<Map<String, String>> greet(
            @Parameter(description = "Name of the person to greet", example = "John")
            @RequestParam(value = "name", defaultValue = "Guest") String name) {
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, " + name + "!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Echo message",
            description = "Returns the same message that was sent in the request body"
    )
    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(
            @Parameter(description = "Message to echo back")
            @RequestBody Map<String, String> payload) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("original", payload);
        response.put("echo", payload.get("message"));
        response.put("length", payload.get("message") != null ? payload.get("message").length() : 0);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get API status")
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("version", "1.0.0");
        response.put("service", "Shoe Shop API");
        
        return ResponseEntity.ok(response);
    }
}
