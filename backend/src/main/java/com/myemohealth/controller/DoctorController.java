package com.myemohealth.controller;

import com.myemohealth.entity.PatientProfile;
import com.myemohealth.entity.User;
import com.myemohealth.repository.DoctorProfileRepository;
import com.myemohealth.repository.PatientProfileRepository;
import com.myemohealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    @PostMapping("/assign/{patientId}")
    public ResponseEntity<Void> assignPatient(@PathVariable Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String doctorEmail = auth.getName();

        User doctorUser = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Allow admins and doctors
        boolean isDoctor = doctorProfileRepository.existsById(doctorUser.getId());
        boolean isAdmin = doctorUser.getRole() != null && "ADMIN".equals(doctorUser.getRole().getName());

        if (!isDoctor && !isAdmin) {
            return ResponseEntity.status(403).build(); // Not authorized
        }

        PatientProfile patientProfile = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patientProfile.setDoctor(doctorUser);
        patientProfileRepository.save(patientProfile);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/patients")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PatientProfile>> getAllPatients() {
        // Return all patients regardless of doctor assignment
        return ResponseEntity.ok(patientProfileRepository.findAll());
    }
}
