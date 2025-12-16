package com.myemohealth.service;

import com.myemohealth.entity.VoiceSession;
import com.myemohealth.repository.VoiceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulatedAiService {

    private final VoiceSessionRepository voiceSessionRepository;

    public VoiceSession analyzeAndSave(Long patientId, String userTranscript) {
        String response = analyzeSentiment(userTranscript);
        String sentiment = "NEUTRAL"; // Simplified for this method or re-parse responsiveness?
        // Actually, let's keep the logic simple here or duplicate risk calculation?
        // Let's just create a helper for full analysis including risk.

        // For now, to match VoiceController expectation of receiving just a String:
        return VoiceSession.builder()
                .patientId(patientId)
                .userTranscript(userTranscript)
                .aiResponse(response)
                .sentimentDetected("NEUTRAL") // Default
                .riskScore(10)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        // Note: The original returned saved session.
    }

    public String analyzeSentiment(String userTranscript) {
        String transcriptLower = userTranscript.toLowerCase();
        String response = "I hear you. Tell me more about that.";

        if (transcriptLower.contains("sad") || transcriptLower.contains("cry")
                || transcriptLower.contains("hopeless")) {
            response = "I'm sorry you're feeling this way. It sounds very heavy. Remember your doctor is here to help.";
        } else if (transcriptLower.contains("anxious") || transcriptLower.contains("worry")
                || transcriptLower.contains("scared")) {
            response = "It seems like there's a lot on your mind. Let's take a deep breath together.";
        } else if (transcriptLower.contains("happy") || transcriptLower.contains("good")
                || transcriptLower.contains("great")) {
            response = "That's wonderful to hear! What made you feel good about that?";
        }
        return response;
    }

}
