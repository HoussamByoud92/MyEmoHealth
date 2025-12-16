package com.myemohealth.config;

import com.myemohealth.entity.*;
import com.myemohealth.entity.QcmQuestion.QuestionType;
import com.myemohealth.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database initializer to seed initial data
 * DISABLED: Using SQL seed scripts instead (seed-data.sql)
 */
// @Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

        private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

        private final RoleRepository roleRepository;
        private final PhaseRepository phaseRepository;
        private final QcmTemplateRepository qcmTemplateRepository;
        private final QcmQuestionRepository qcmQuestionRepository;
        private final UserRepository userRepository;
        private final DoctorProfileRepository doctorProfileRepository;
        private final PatientProfileRepository patientProfileRepository;

        private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
                initializeRoles();
                initializeUsers();
                initializePhases();
                // initializeQcmData();
        }

        private void initializeRoles() {
                if (roleRepository.count() > 0) {
                        log.info("Roles already initialized, skipping...");
                        return;
                }

                log.info("Initializing roles...");

                Role patientRole = Role.builder()
                                .name("PATIENT")
                                .description("Patient user with access to tests and consultations")
                                .build();
                roleRepository.save(patientRole);

                Role doctorRole = Role.builder()
                                .name("DOCTOR")
                                .description("Doctor user with access to patient management")
                                .build();
                roleRepository.save(doctorRole);

                Role adminRole = Role.builder()
                                .name("ADMIN")
                                .description("Administrator with full system access")
                                .build();
                roleRepository.save(adminRole);
                log.info("Roles initialized");
        }

        private void initializePhases() {
                if (phaseRepository.count() > 0) {
                        log.info("Phases already initialized, skipping...");
                        return;
                }

                log.info("Initializing phases...");

                Phase[] phases = {
                                Phase.builder().number(1).label("Initial Assessment")
                                                .description("Initial patient evaluation and baseline assessment")
                                                .durationDays(30).build(),
                                Phase.builder().number(2).label("Early Treatment")
                                                .description("Beginning of treatment protocol")
                                                .durationDays(60).build(),
                                Phase.builder().number(3).label("Mid Treatment")
                                                .description("Ongoing treatment and progress monitoring")
                                                .durationDays(90).build(),
                                Phase.builder().number(4).label("Advanced Treatment")
                                                .description("Advanced therapeutic interventions")
                                                .durationDays(90).build(),
                                Phase.builder().number(5).label("Maintenance")
                                                .description("Long-term maintenance and relapse prevention")
                                                .durationDays(120).build()
                };

                for (Phase phase : phases) {
                        phaseRepository.save(phase);
                }
                log.info("Phases initialized");
        }

        private void initializeUsers() {
                log.info("Initializing users...");
                Role doctorRole = roleRepository.findByName("DOCTOR").orElseThrow();
                Role patientRole = roleRepository.findByName("PATIENT").orElseThrow();

                // Create Demo Doctor
                User doctorUser;
                if (userRepository.findByEmail("doctor1@example.com").isEmpty()) {
                        doctorUser = User.builder()
                                        .email("doctor1@example.com")
                                        .passwordHash(passwordEncoder.encode("password"))
                                        .firstName("John")
                                        .lastName("Doe")
                                        .role(doctorRole)
                                        .enabled(true)
                                        .emailVerified(true)
                                        .build();
                        doctorUser = userRepository.save(doctorUser);

                        DoctorProfile doctorProfile = DoctorProfile.builder()
                                        .user(doctorUser)
                                        .specialty("Psychiatrist")
                                        .phone("123-456-7890")
                                        .licenseNumber("MD12345")
                                        .build();
                        doctorProfileRepository.save(doctorProfile);

                        log.info("Created demo doctor: doctor1@example.com / password");
                }

                // Create Demo Patient
                if (userRepository.findByEmail("patient1@example.com").isEmpty()) {
                        // Re-fetch doctor to be sure
                        User doctor = userRepository.findByEmail("doctor1@example.com").orElseThrow();

                        User patient = User.builder()
                                        .email("patient1@example.com")
                                        .passwordHash(passwordEncoder.encode("password"))
                                        .firstName("Alice")
                                        .lastName("Dupont")
                                        .role(patientRole)
                                        .enabled(true)
                                        .emailVerified(true)
                                        .build();
                        patient = userRepository.save(patient);

                        // Link to Doctor
                        PatientProfile patientProfile = PatientProfile.builder()
                                        .user(patient)
                                        .dateNaissance(java.time.LocalDate.of(1990, 5, 15))
                                        .sexe("F")
                                        .doctor(doctor) // Link here
                                        .currentPhase(1)
                                        .consentVoiceRecording(true)
                                        .build();
                        patientProfileRepository.save(patientProfile);

                        log.info("Created demo patient: patient1@example.com / password with doctor assignment");
                }
        }
}
