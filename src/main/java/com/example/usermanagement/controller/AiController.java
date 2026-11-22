package com.example.usermanagement.controller;
import com.example.usermanagement.dto.AiDtos.*;
import com.example.usermanagement.service.OllamaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/ai")
public class AiController {
 private final OllamaService ollama;public AiController(OllamaService o){ollama=o;}
 @PostMapping("/query") public AiQueryResponse query(@Valid @RequestBody AiQueryRequest r,Authentication auth){return ollama.query(r.prompt().trim());}
}
