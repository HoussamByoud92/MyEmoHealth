package com.myemohealth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Extended profile information for doctors
 */
@Entity
@Table(name = "doctor_profile")
public class DoctorProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "patientProfile", "doctorProfile", "password", "passwordHash" })
    private User user;

    @Column(length = 100)
    private String specialty;

    @Column(length = 20)
    private String phone;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> availabilities;

    @Column(name = "max_patients")
    private Integer maxPatients = 50;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DoctorProfile() {
    }

    public DoctorProfile(Long userId, User user, String specialty, String phone, String licenseNumber,
            Map<String, Object> availabilities, Integer maxPatients, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.user = user;
        this.specialty = specialty;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.availabilities = availabilities;
        this.maxPatients = maxPatients != null ? maxPatients : 50;
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

    public static DoctorProfileBuilder builder() {
        return new DoctorProfileBuilder();
    }

    public static class DoctorProfileBuilder {
        private Long userId;
        private User user;
        private String specialty;
        private String phone;
        private String licenseNumber;
        private Map<String, Object> availabilities;
        private Integer maxPatients = 50;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        DoctorProfileBuilder() {
        }

        public DoctorProfileBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public DoctorProfileBuilder user(User user) {
            this.user = user;
            return this;
        }

        public DoctorProfileBuilder specialty(String specialty) {
            this.specialty = specialty;
            return this;
        }

        public DoctorProfileBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public DoctorProfileBuilder licenseNumber(String licenseNumber) {
            this.licenseNumber = licenseNumber;
            return this;
        }

        public DoctorProfileBuilder availabilities(Map<String, Object> availabilities) {
            this.availabilities = availabilities;
            return this;
        }

        public DoctorProfileBuilder maxPatients(Integer maxPatients) {
            this.maxPatients = maxPatients;
            return this;
        }

        public DoctorProfileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DoctorProfileBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DoctorProfile build() {
            return new DoctorProfile(userId, user, specialty, phone, licenseNumber, availabilities, maxPatients,
                    createdAt, updatedAt);
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Map<String, Object> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(Map<String, Object> availabilities) {
        this.availabilities = availabilities;
    }

    public Integer getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(Integer maxPatients) {
        this.maxPatients = maxPatients;
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
