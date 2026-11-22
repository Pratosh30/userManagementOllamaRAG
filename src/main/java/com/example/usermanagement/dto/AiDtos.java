package com.example.usermanagement.dto;
import jakarta.validation.constraints.*;
public final class AiDtos {
 private AiDtos(){}
 public record AiQueryRequest(@NotBlank @Size(max=8000) String prompt){}
 public record AiQueryResponse(String model,String response){}
}
