package com.example.usermanagement.rag;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/documents/text")
    @ResponseStatus(HttpStatus.CREATED)
    public RagDtos.IngestResponse ingest(@Valid @RequestBody RagDtos.IngestRequest request,
                                         Authentication authentication) {
        return ragService.ingest(authentication.getName(), request);
    }

    @PostMapping("/ask")
    public RagDtos.AskResponse ask(@Valid @RequestBody RagDtos.AskRequest request,
                                   Authentication authentication) {
        return ragService.ask(authentication.getName(), request.question().trim());
    }
}
