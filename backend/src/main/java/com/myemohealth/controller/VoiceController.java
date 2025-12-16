package com.myemohealth.controller;

import com.myemohealth.entity.VoiceSession;
import com.myemohealth.service.SimulatedAiService;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class VoiceController {

    private final com.myemohealth.service.OpenAIService openAIService;
    private final com.myemohealth.repository.VoiceSessionRepository voiceSessionRepository;
    private final com.myemohealth.service.SimulatedAiService simulatedAiService;

    @GetMapping("/conversations/{patientId}")
    public ResponseEntity<List<VoiceSession>> getConversationHistory(@PathVariable Long patientId) {
        List<VoiceSession> sessions = voiceSessionRepository.findByPatientIdOrderByTimestampDesc(patientId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping(value = "/analyze", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VoiceSession> analyzeSession(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile audioFile,
            @RequestParam("patientId") Long patientId) {

        try {
            String transcript = openAIService.transcribe(audioFile);
            return processAnalysis(patientId, transcript);
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorSession(patientId, "Failed to process audio: " + e.getMessage());
        }
    }

    @PostMapping(value = "/analyze-text")
    public ResponseEntity<VoiceSession> analyzeText(@RequestBody VoiceAnalysisRequest request) {
        try {
            return processAnalysis(request.getPatientId(), request.getTranscript());
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorSession(request.getPatientId(), "Failed to process request: " + e.getMessage());
        }
    }

    private ResponseEntity<VoiceSession> processAnalysis(Long patientId, String transcript) {
        String aiResponse;

        try {
            // 1. Analyze with OpenAI
            aiResponse = openAIService.chat(transcript);

            // Validation
            if (aiResponse.startsWith("AI request failed:")) {
                throw new RuntimeException(aiResponse);
            }
        } catch (Exception e) {
            System.out.println("OpenAI Failed: " + e.getMessage() + ". PROCEEDING WITH FALLBACK.");
            // Fallback to Simulated
            aiResponse = simulatedAiService.analyzeSentiment(transcript);
        }

        // 3. Detect Sentiment
        String sentiment = "NEUTRAL";
        String lower = aiResponse.toLowerCase();
        if (lower.contains("sad") || lower.contains("bad") || lower.contains("anxious") || lower.contains("depressed"))
            sentiment = "DEPRESSIVE";
        else if (lower.contains("happy") || lower.contains("good") || lower.contains("great"))
            sentiment = "POSITIVE";

        // 4. Save
        VoiceSession session = VoiceSession.builder()
                .patientId(patientId)
                .userTranscript(transcript)
                .aiResponse(aiResponse)
                .sentimentDetected(sentiment)
                .riskScore(sentiment.equals("DEPRESSIVE") ? 60 : 10)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(voiceSessionRepository.save(session));
    }

    private ResponseEntity<VoiceSession> createErrorSession(Long patientId, String errorMessage) {
        VoiceSession errorSession = VoiceSession.builder()
                .patientId(patientId)
                .userTranscript("")
                .aiResponse(errorMessage)
                .sentimentDetected("NEUTRAL")
                .riskScore(0)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(voiceSessionRepository.save(errorSession));
    }
}

@Data
class VoiceAnalysisRequest {
    private Long patientId;
    private String transcript;
}
