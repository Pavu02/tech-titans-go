package com.examly.springapp.controller;

import com.examly.springapp.dto.ChatRequestDTO;
import com.examly.springapp.dto.ChatResponseDTO;
import com.examly.springapp.model.ChatMessage;
import com.examly.springapp.model.FaqEntity;
import com.examly.springapp.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    // Constructor Injection
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> sendChatMessage(@Valid @RequestBody ChatRequestDTO requestDTO) {
        ChatResponseDTO response = chatService.processChat(requestDTO.message(), requestDTO.sessionId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/chat/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        List<ChatMessage> history = chatService.getChatHistory(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(history);
    }

    @DeleteMapping("/chat/memory/{sessionId}")
    public ResponseEntity<Void> clearChatMemory(@PathVariable String sessionId) {
        chatService.clearMemory(sessionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<FaqEntity>> listFaqs() {
        List<FaqEntity> faqs = chatService.getAllFaqs();
        return ResponseEntity.status(HttpStatus.OK).body(faqs);
    }
}
