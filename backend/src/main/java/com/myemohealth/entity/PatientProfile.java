package com.myemohealth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Extended profile information for patients
 */
@Entity
@Table(name = "patient_profile")
public class PatientProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "patientProfile", "doctorProfile", "password", "passwordHash" })
    private User user;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(length = 10)
    private String sexe;

    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonIgnoreProperties({ "patientProfile", "doctorProfile", "password", "passwordHash" })
    private User doctor;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "discharge_date")
    private LocalDate dischargeDate;

    @Column(name = "current_phase")
    private Integer currentPhase = 1;

    @Column(name = "consent_voice_recording")
    private Boolean consentVoiceRecording = false;

    @Column(name = "consent_data_sharing")
    private Boolean consentDataSharing = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PatientProfile() {
    }

    public PatientProfile(Long userId, User user, LocalDate dateNaissance, String sexe, String medicalNotes,
            User doctor, LocalDate admissionDate, LocalDate dischargeDate, Integer currentPhase,
            Boolean consentVoiceRecording, Boolean consentDataSharing, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.userId = userId;
        this.user = user;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.medicalNotes = medicalNotes;
        this.doctor = doctor;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.currentPhase = currentPhase != null ? currentPhase : 1;
        this.consentVoiceRecording = consentVoiceRecording != null ? consentVoiceRecording : false;
        this.consentDataSharing = consentDataSharing != null ? consentDataSharing : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getAge() {
        if (dateNaissance == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateNaissance.getYear();
    }

    public boolean hasVoiceConsent() {
        return Boolean.TRUE.equals(consentVoiceRecording);
    }

    public static PatientProfileBuilder builder() {
        return new PatientProfileBuilder();
    }

    public static class PatientProfileBuilder {
        private Long userId;
        private User user;
        private LocalDate dateNaissance;
        private String sexe;
        private String medicalNotes;
        private User doctor;
        private LocalDate admissionDate;
        private LocalDate dischargeDate;
        private Integer currentPhase = 1;
        private Boolean consentVoiceRecording = false;
        private Boolean consentDataSharing = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        PatientProfileBuilder() {
        }

        public PatientProfileBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public PatientProfileBuilder user(User user) {
            this.user = user;
            return this;
        }

        public PatientProfileBuilder dateNaissance(LocalDate dateNaissance) {
            this.dateNaissance = dateNaissance;
            return this;
        }

        public PatientProfileBuilder sexe(String sexe) {
            this.sexe = sexe;
            return this;
        }

        public PatientProfileBuilder medicalNotes(String medicalNotes) {
            this.medicalNotes = medicalNotes;
            return this;
        }

        public PatientProfileBuilder doctor(User doctor) {
            this.doctor = doctor;
            return this;
        }

        public PatientProfileBuilder admissionDate(LocalDate admissionDate) {
            this.admissionDate = admissionDate;
            return this;
        }

        public PatientProfileBuilder dischargeDate(LocalDate dischargeDate) {
            this.dischargeDate = dischargeDate;
            return this;
        }

        public PatientProfileBuilder currentPhase(Integer currentPhase) {
            this.currentPhase = currentPhase;
            return this;
        }

        public PatientProfileBuilder consentVoiceRecording(Boolean consentVoiceRecording) {
            this.consentVoiceRecording = consentVoiceRecording;
            return this;
        }

        public PatientProfileBuilder consentDataSharing(Boolean consentDataSharing) {
            this.consentDataSharing = consentDataSharing;
            return this;
        }

        public PatientProfileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PatientProfileBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PatientProfile build() {
            return new PatientProfile(userId, user, dateNaissance, sexe, medicalNotes, doctor, admissionDate,
                    dischargeDate, currentPhase, consentVoiceRecording, consentDataSharing, createdAt, updatedAt);
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDate getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDate dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Integer getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(Integer currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Boolean getConsentVoiceRecording() {
        return consentVoiceRecording;
    }

    public void setConsentVoiceRecording(Boolean consentVoiceRecording) {
        this.consentVoiceRecording = consentVoiceRecording;
    }

    public Boolean getConsentDataSharing() {
        return consentDataSharing;
    }

    public void setConsentDataSharing(Boolean consentDataSharing) {
        this.consentDataSharing = consentDataSharing;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
