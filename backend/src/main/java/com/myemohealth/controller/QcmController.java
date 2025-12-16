package com.myemohealth.controller;

import com.myemohealth.entity.QcmTemplate;
import com.myemohealth.entity.User;
import com.myemohealth.repository.QcmTemplateRepository;
import com.myemohealth.repository.UserRepository;
import com.myemohealth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/qcms")
@RequiredArgsConstructor
public class QcmController {

    private final QcmTemplateRepository qcmTemplateRepository;
    private final com.myemohealth.repository.QcmQuestionRepository qcmQuestionRepository;
    private final UserRepository userRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<QcmTemplate>> getAllTemplates() {
        return ResponseEntity.ok(qcmTemplateRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<QcmTemplate> createTemplate(@RequestBody QcmTemplate template) {
        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String email = userPrincipal.getEmail();

        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        template.setCreator(creator);
        return ResponseEntity.ok(qcmTemplateRepository.save(template));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QcmTemplate> updateTemplate(
            @PathVariable Long id,
            @RequestBody QcmTemplate template) {
        return qcmTemplateRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(template.getTitle());
                    existing.setDescription(template.getDescription());
                    existing.setCategory(template.getCategory());
                    existing.setDifficultyLevel(template.getDifficultyLevel());
                    existing.setEstimatedDurationMinutes(template.getEstimatedDurationMinutes());
                    existing.setIsActive(template.getIsActive());
                    return ResponseEntity.ok(qcmTemplateRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        return qcmTemplateRepository.findById(id)
                .map(template -> {
                    qcmTemplateRepository.delete(template);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{templateId}/questions")
    public ResponseEntity<List<com.myemohealth.entity.QcmQuestion>> getQuestions(@PathVariable Long templateId) {
        return ResponseEntity.ok(qcmQuestionRepository.findByQcmTemplateIdOrderByPosition(templateId));
    }

    @PostMapping("/{templateId}/questions")
    public ResponseEntity<com.myemohealth.entity.QcmQuestion> createQuestion(
            @PathVariable Long templateId,
            @RequestBody com.myemohealth.entity.QcmQuestion question) {
        return qcmTemplateRepository.findById(templateId)
                .map(template -> {
                    question.setQcmTemplate(template);
                    return ResponseEntity.ok(qcmQuestionRepository.save(question));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<com.myemohealth.entity.QcmQuestion> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody com.myemohealth.entity.QcmQuestion question) {
        return qcmQuestionRepository.findById(questionId)
                .map(existing -> {
                    existing.setQuestionText(question.getQuestionText());
                    existing.setQuestionType(question.getQuestionType());
                    existing.setOptions(question.getOptions());
                    existing.setCorrectAnswer(question.getCorrectAnswer());
                    existing.setWeight(question.getWeight());
                    existing.setExplanation(question.getExplanation());
                    existing.setPosition(question.getPosition());
                    return ResponseEntity.ok(qcmQuestionRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        return qcmQuestionRepository.findById(questionId)
                .map(question -> {
                    qcmQuestionRepository.delete(question);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
