package com.myemohealth.controller;

import com.myemohealth.entity.DoctorProfile;
import com.myemohealth.entity.PatientProfile;
import com.myemohealth.entity.User;
import com.myemohealth.entity.TestInstance;
import com.myemohealth.repository.DoctorProfileRepository;
import com.myemohealth.repository.PatientProfileRepository;
import com.myemohealth.repository.TestInstanceRepository;
import com.myemohealth.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final TestInstanceRepository testInstanceRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        long totalPatients = userRepository.countPatients();
        long totalTests = testInstanceRepository.count();
        long criticalCases = testInstanceRepository.countCriticalCases();
        long completedTests = testInstanceRepository.countCompletedTests();

        return ResponseEntity.ok(DashboardStats.builder()
                .totalPatients(totalPatients)
                .totalTests(totalTests)
                .criticalCases(criticalCases)
                .completedTests(completedTests)
                .build());
    }

    @GetMapping("/patient/me")
    @Transactional(readOnly = true)
    public ResponseEntity<PatientDashboard> getPatientDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(user -> {
                    // Try to find patient profile
                    return patientProfileRepository.findById(user.getId())
                            .map(profile -> {
                                String doctorName = "Not Assigned";
                                if (profile.getDoctor() != null) {
                                    doctorName = profile.getDoctor().getFirstName() + " "
                                            + profile.getDoctor().getLastName();
                                }

                                return ResponseEntity.ok(PatientDashboard.builder()
                                        .userName(user.getFirstName() + " " + user.getLastName())
                                        .currentMoodScore(fetchLastMoodScore(user.getId()))
                                        .moodTrend(1.2) // This would need complex logic/query
                                        .nextTestDate(LocalDate.now().toString()) // Logic to find next scheduled test
                                        .nextTestTitle("Daily Mood Check")
                                        .doctorName(doctorName)
                                        .currentPhase(profile.getCurrentPhase())
                                        .build());
                            })
                            .orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Helper to get last mood score
    private double fetchLastMoodScore(Long patientId) {
        return testInstanceRepository.findByPatientId(patientId).stream()
                .findFirst() // Ordered by createdAt DESC in repository
                .map(TestInstance::getScore)
                .map(java.math.BigDecimal::doubleValue)
                .orElse(0.0);
    }

    @Data
    @Builder
    public static class DashboardStats {
        private long totalPatients;
        private long totalTests;
        private long criticalCases;
        private long completedTests;
    }

    @Data
    @Builder
    public static class PatientDashboard {
        private String userName;
        private double currentMoodScore;
        private double moodTrend;
        private String nextTestDate;
        private String nextTestTitle;
        private String doctorName;
        private Integer currentPhase;
    }
}
