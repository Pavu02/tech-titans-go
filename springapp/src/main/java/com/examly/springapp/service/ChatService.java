package com.examly.springapp.service;

import com.examly.springapp.model.ChatMessage;
import com.examly.springapp.model.FaqEntity;
import com.examly.springapp.repository.ChatMessageRepo;
import com.examly.springapp.repository.FaqRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    @Autowired
    private FaqRepo faqRepo;

    @Autowired
    private ChatMessageRepo chatMessageRepo;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${chatbot.lexical.threshold:0.08}")
    private double lexicalThreshold;

    private final Map<String, List<Map<String, String>>> sessionMemory = new ConcurrentHashMap<>();

    @PostConstruct
    public void initFaqs() {
        try {
            if (faqRepo.count() == 0) {
                Resource resource = resourceLoader.getResource("classpath:faqs.json");
                if (resource.exists()) {
                    ObjectMapper mapper = new ObjectMapper();
                    try (InputStream is = resource.getInputStream()) {
                        List<Map<String, Object>> list = mapper.readValue(is, new TypeReference<>() {});
                        for (Map<String, Object> map : list) {
                            Long id = ((Number) map.get("id")).longValue();
                            String category = (String) map.get("category");
                            String question = (String) map.get("question");
                            String answer = (String) map.get("answer");
                            faqRepo.save(new FaqEntity(id, category, question, answer));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Could not seed faqs: " + e.getMessage());
        }
    }

    public List<FaqEntity> getAllFaqs() {
        return faqRepo.findAll();
    }

    public List<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepo.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    public void clearMemory(String sessionId) {
        sessionMemory.remove(sessionId);
    }

    public Map<String, Object> processChat(String message, String sessionId) {
        String trimmed = (message != null) ? message.trim() : "";
        String sid = (sessionId != null && !sessionId.isEmpty()) ? sessionId : "default-session";

        List<FaqEntity> faqs = faqRepo.findAll();
        FaqEntity bestFaq = null;
        double bestScore = 0.0;

        for (FaqEntity faq : faqs) {
            double qScore = jaccardSimilarity(trimmed, faq.getQuestion());
            double aScore = jaccardSimilarity(trimmed, faq.getAnswer()) * 0.7;
            double combined = Math.max(qScore, aScore);
            if (combined > bestScore) {
                bestScore = combined;
                bestFaq = faq;
            }
        }

        boolean matched = bestFaq != null && bestScore >= lexicalThreshold;
        String reply;
        String source = "lexical-fallback";
        double confidence = bestFaq != null ? Math.min(1.0, Math.round(bestScore * 100.0) / 100.0) : 0.0;

        if (matched) {
            reply = bestFaq.getAnswer();
            source = "faq-knowledge-base";
            confidence = Math.max(confidence, 0.75);
        } else {
            reply = "I'm here to help with BookHeaven! You can ask me how to rent books, check your rental request status, post feedback, or contact our support team at 123-456-7890.";
            source = "default-rule";
            confidence = 0.50;
        }

        // Store memory
        List<Map<String, String>> turns = sessionMemory.computeIfAbsent(sid, k -> new ArrayList<>());
        Map<String, String> userTurn = new HashMap<>();
        userTurn.put("role", "user");
        userTurn.put("text", trimmed);
        turns.add(userTurn);

        Map<String, String> botTurn = new HashMap<>();
        botTurn.put("role", "model");
        botTurn.put("text", reply);
        turns.add(botTurn);

        if (turns.size() > 16) {
            turns.subList(0, turns.size() - 16).clear();
        }

        ChatMessage cm = new ChatMessage(
                sid,
                trimmed,
                reply,
                matched && bestFaq != null ? bestFaq.getId() : null,
                confidence,
                LocalDateTime.now()
        );
        chatMessageRepo.save(cm);

        Map<String, Object> resp = new HashMap<>();
        resp.put("reply", reply);
        resp.put("matched", matched);
        resp.put("matchedQuestion", matched && bestFaq != null ? bestFaq.getQuestion() : null);
        resp.put("category", matched && bestFaq != null ? bestFaq.getCategory() : null);
        resp.put("confidence", confidence);
        resp.put("source", source);
        resp.put("sessionId", sid);
        resp.put("resolvedQuestion", trimmed);
        return resp;
    }

    private double jaccardSimilarity(String textA, String textB) {
        if (textA == null || textB == null) return 0.0;
        Set<String> wordsA = extractWords(textA);
        Set<String> wordsB = extractWords(textB);
        if (wordsA.isEmpty() || wordsB.isEmpty()) return 0.0;

        int intersection = 0;
        for (String w : wordsA) {
            if (wordsB.contains(w)) {
                intersection++;
            }
        }
        Set<String> union = new HashSet<>(wordsA);
        union.addAll(wordsB);
        return union.isEmpty() ? 0.0 : (double) intersection / union.size();
    }

    private Set<String> extractWords(String text) {
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] tokens = cleaned.split("\\s+");
        Set<String> words = new HashSet<>();
        for (String t : tokens) {
            if (!t.trim().isEmpty()) {
                words.add(t.trim());
            }
        }
        return words;
    }
}
