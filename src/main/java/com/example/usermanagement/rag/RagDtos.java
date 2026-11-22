package com.example.usermanagement.rag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public final class RagDtos {
    private RagDtos() {}

    public record IngestRequest(
            @NotBlank @Size(max = 255) String filename,
            @NotBlank @Size(max = 200000) String text) {}

    public record IngestResponse(Long documentId, int chunksStored, String message) {}

    public record AskRequest(@NotBlank @Size(max = 4000) String question) {}

    public record Source(Long documentId, String filename, int chunkIndex, String excerpt) {}

    public record AskResponse(String answer, List<Source> sources) {}
}
