package com.examly.springapp.controller;

import com.examly.springapp.model.ChatMessage;
import com.examly.springapp.model.FaqEntity;
import com.examly.springapp.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<?> sendChatMessage(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        String sessionId = body.get("sessionId");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Message is required"));
        }
        Map<String, Object> response = chatService.processChat(message, sessionId);
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
