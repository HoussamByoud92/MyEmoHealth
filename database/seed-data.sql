-- MyEmoHealth Seed Data
-- Sample data for development and testing

-- ============================================
-- ROLES
-- ============================================

INSERT INTO role (name, description) VALUES
('PATIENT', 'Patient role - can take tests, chat with doctor, view own results'),
('DOCTOR', 'Doctor role - can manage patients, create QCMs, view results, communicate'),
('ADMIN', 'Administrator role - full system access, user management');

-- ============================================
-- PHASES
-- ============================================

INSERT INTO phase (number, label, description, duration_days) VALUES
(1, 'Phase Initiale', 'Évaluation initiale post-hospitalisation', 7),
(2, 'Phase de Stabilisation', 'Stabilisation émotionnelle et adaptation', 14),
(3, 'Phase de Renforcement', 'Renforcement des acquis et progression', 14),
(4, 'Phase d''Autonomisation', 'Développement de l''autonomie émotionnelle', 21),
(5, 'Phase de Consolidation', 'Consolidation et préparation à la sortie', 14);

-- ============================================
-- USERS (Passwords are all: password)
-- Plain text passwords (no hashing)
-- ============================================

-- Admin user
INSERT INTO "user" (email, password_hash, first_name, last_name, role_id, enabled, email_verified) VALUES
('admin@myemohealth.com', 'password', 'Admin', 'System', 1, TRUE, TRUE);

-- Doctors
INSERT INTO "user" (email, password_hash, first_name, last_name, role_id, enabled, email_verified) VALUES
('dr.martin@myemohealth.com', 'password', 'Sophie', 'Martin', 2, TRUE, TRUE),
('dr.dubois@myemohealth.com', 'password', 'Pierre', 'Dubois', 2, TRUE, TRUE);

-- Patients
INSERT INTO "user" (email, password_hash, first_name, last_name, role_id, enabled, email_verified) VALUES
('patient1@test.com', 'password', 'Marie', 'Lefebvre', 3, TRUE, TRUE),
('patient2@test.com', 'password', 'Jean', 'Moreau', 3, TRUE, TRUE),
('patient3@test.com', 'password', 'Claire', 'Bernard', 3, TRUE, TRUE),
('patient4@test.com', 'password', 'Luc', 'Petit', 3, TRUE, TRUE),
('patient5@test.com', 'password', 'Emma', 'Roux', 3, TRUE, TRUE);

-- ============================================
-- DOCTOR PROFILES
-- ============================================

INSERT INTO doctor_profile (user_id, specialty, phone, license_number, max_patients) VALUES
(2, 'Psychiatrie', '+33 6 12 34 56 78', 'PSY-2024-001', 30),
(3, 'Psychologie Clinique', '+33 6 98 76 54 32', 'PSY-2024-002', 25);

-- ============================================
-- PATIENT PROFILES
-- ============================================

INSERT INTO patient_profile (user_id, date_naissance, sexe, doctor_id, admission_date, discharge_date, current_phase, consent_voice_recording, consent_data_sharing, medical_notes) VALUES
(4, '1985-03-15', 'F', 2, '2024-11-01', '2024-11-10', 1, TRUE, TRUE, 'Patient stable, suivi post-hospitalisation pour dépression'),
(5, '1978-07-22', 'M', 2, '2024-11-05', '2024-11-12', 1, TRUE, TRUE, 'Anxiété généralisée, bon pronostic'),
(6, '1992-11-08', 'F', 3, '2024-10-20', '2024-10-28', 2, TRUE, FALSE, 'Trouble bipolaire, phase stabilisée'),
(7, '1988-05-30', 'M', 3, '2024-10-15', '2024-10-25', 2, FALSE, TRUE, 'Burnout professionnel, en amélioration'),
(8, '1995-09-12', 'F', 2, '2024-09-10', '2024-09-20', 3, TRUE, TRUE, 'Trouble panique, progression positive');

-- ============================================
-- PATIENT PHASE PROGRESS
-- ============================================

-- Patient 1 - Phase 1 in progress
INSERT INTO patient_phase_progress (patient_id, phase_id, status, started_at, tests_completed, tests_passed) VALUES
(4, 1, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '2 days', 1, 1);

-- Patient 2 - Phase 1 in progress
INSERT INTO patient_phase_progress (patient_id, phase_id, status, started_at, tests_completed, tests_passed) VALUES
(5, 1, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '1 day', 0, 0);

-- Patient 3 - Phase 1 completed, Phase 2 in progress
INSERT INTO patient_phase_progress (patient_id, phase_id, status, started_at, completed_at, tests_completed, tests_passed) VALUES
(6, 1, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '13 days', 3, 3),
(6, 2, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '13 days', NULL, 2, 2);

-- Patient 4 - Phase 1 completed, Phase 2 in progress
INSERT INTO patient_phase_progress (patient_id, phase_id, status, started_at, completed_at, tests_completed, tests_passed) VALUES
(7, 1, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '18 days', 3, 3),
(7, 2, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '18 days', NULL, 1, 1);

-- Patient 5 - Phases 1 & 2 completed, Phase 3 in progress
INSERT INTO patient_phase_progress (patient_id, phase_id, status, started_at, completed_at, tests_completed, tests_passed) VALUES
(8, 1, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '60 days', CURRENT_TIMESTAMP - INTERVAL '53 days', 3, 3),
(8, 2, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '53 days', CURRENT_TIMESTAMP - INTERVAL '39 days', 3, 3),
(8, 3, 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '39 days', NULL, 2, 2);

-- ============================================
-- QCM TEMPLATES
-- ============================================

INSERT INTO qcm_template (title, description, creator_id, category, difficulty_level, estimated_duration_minutes, is_active) VALUES
('Évaluation Émotionnelle Initiale', 'Questionnaire d''évaluation de l''état émotionnel initial du patient', 2, 'EMOTIONAL_ASSESSMENT', 2, 15, TRUE),
('Échelle d''Anxiété GAD-7', 'Questionnaire standardisé pour mesurer l''anxiété généralisée', 2, 'ANXIETY', 2, 10, TRUE),
('Inventaire de Dépression de Beck (BDI)', 'Évaluation de la sévérité des symptômes dépressifs', 3, 'DEPRESSION', 3, 20, TRUE),
('Qualité de Vie et Bien-être', 'Évaluation de la qualité de vie et du bien-être général', 3, 'WELLBEING', 2, 15, TRUE),
('Gestion du Stress', 'Questionnaire sur les stratégies de gestion du stress', 2, 'STRESS', 2, 12, TRUE);

-- ============================================
-- QCM QUESTIONS (Sample for first QCM)
-- ============================================

-- QCM 1: Évaluation Émotionnelle Initiale
INSERT INTO qcm_question (qcm_id, position, question_text, question_type, options, weight) VALUES
(1, 1, 'Comment évaluez-vous votre humeur générale aujourd''hui ?', 'SCALE', 
 '{"min": 1, "max": 10, "minLabel": "Très mauvaise", "maxLabel": "Excellente"}', 1.0),

(1, 2, 'Avez-vous ressenti de l''anxiété au cours des dernières 24 heures ?', 'SINGLE_CHOICE',
 '{"options": [
   {"value": "A", "text": "Pas du tout", "points": 10},
   {"value": "B", "text": "Un peu", "points": 7},
   {"value": "C", "text": "Modérément", "points": 5},
   {"value": "D", "text": "Beaucoup", "points": 2},
   {"value": "E", "text": "Énormément", "points": 0}
 ]}', 1.0),

(1, 3, 'Quelles émotions avez-vous ressenties aujourd''hui ? (plusieurs réponses possibles)', 'MULTIPLE_CHOICE',
 '{"options": [
   {"value": "A", "text": "Joie", "points": 2},
   {"value": "B", "text": "Tristesse", "points": 0},
   {"value": "C", "text": "Colère", "points": 0},
   {"value": "D", "text": "Peur", "points": 0},
   {"value": "E", "text": "Sérénité", "points": 2},
   {"value": "F", "text": "Confusion", "points": 0}
 ], "maxPoints": 10}', 1.0),

(1, 4, 'Avez-vous bien dormi la nuit dernière ?', 'SINGLE_CHOICE',
 '{"options": [
   {"value": "A", "text": "Très bien (7-8h)", "points": 10},
   {"value": "B", "text": "Bien (5-7h)", "points": 7},
   {"value": "C", "text": "Moyennement (3-5h)", "points": 4},
   {"value": "D", "text": "Mal (1-3h)", "points": 2},
   {"value": "E", "text": "Très mal (<1h)", "points": 0}
 ]}', 1.0),

(1, 5, 'Comment évaluez-vous votre niveau d''énergie ?', 'SCALE',
 '{"min": 1, "max": 10, "minLabel": "Épuisé", "maxLabel": "Très énergique"}', 1.0),

(1, 6, 'Avez-vous eu des pensées négatives récurrentes ?', 'SINGLE_CHOICE',
 '{"options": [
   {"value": "A", "text": "Jamais", "points": 10},
   {"value": "B", "text": "Rarement", "points": 7},
   {"value": "C", "text": "Parfois", "points": 5},
   {"value": "D", "text": "Souvent", "points": 2},
   {"value": "E", "text": "Constamment", "points": 0}
 ]}', 1.0),

(1, 7, 'Vous sentez-vous capable de gérer vos émotions actuellement ?', 'SCALE',
 '{"min": 1, "max": 10, "minLabel": "Pas du tout", "maxLabel": "Totalement"}', 1.0),

(1, 8, 'Avez-vous eu des interactions sociales positives récemment ?', 'SINGLE_CHOICE',
 '{"options": [
   {"value": "A", "text": "Oui, plusieurs", "points": 10},
   {"value": "B", "text": "Oui, quelques-unes", "points": 7},
   {"value": "C", "text": "Une ou deux", "points": 5},
   {"value": "D", "text": "Aucune", "points": 2},
   {"value": "E", "text": "J''ai évité les interactions", "points": 0}
 ]}', 1.0);

-- ============================================
-- TEST INSTANCES (Sample completed tests)
-- ============================================

-- Patient 1 - Completed test 1 (passed)
INSERT INTO test_instance (patient_id, qcm_id, phase_id, scheduled_at, started_at, finished_at, score, status, attempt_number, time_spent_seconds) VALUES
(4, 1, 1, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '12 minutes', 8.2, 'PASSED', 1, 720);

-- Patient 3 - Completed multiple tests
INSERT INTO test_instance (patient_id, qcm_id, phase_id, scheduled_at, started_at, finished_at, score, status, attempt_number, time_spent_seconds) VALUES
(6, 1, 1, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '20 days' + INTERVAL '15 minutes', 7.8, 'PASSED', 1, 900),
(6, 2, 1, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '18 days' + INTERVAL '10 minutes', 8.5, 'PASSED', 1, 600),
(6, 3, 1, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '15 days' + INTERVAL '18 minutes', 7.6, 'PASSED', 1, 1080),
(6, 4, 2, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '10 days' + INTERVAL '14 minutes', 8.1, 'PASSED', 1, 840),
(6, 5, 2, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '7 days' + INTERVAL '11 minutes', 7.9, 'PASSED', 1, 660);

-- Patient 2 - Scheduled test
INSERT INTO test_instance (patient_id, qcm_id, phase_id, scheduled_at, status) VALUES
(5, 1, 1, CURRENT_TIMESTAMP + INTERVAL '1 day', 'SCHEDULED');

-- ============================================
-- CHAT THREADS
-- ============================================

INSERT INTO chat_thread (patient_id, doctor_id, status, last_message_at) VALUES
(4, 2, 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
(5, 2, 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(6, 3, 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '5 hours'),
(7, 3, 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(8, 2, 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '6 hours');

-- ============================================
-- CHAT MESSAGES (Sample conversation)
-- ============================================

-- Conversation between Patient 1 and Dr. Martin
INSERT INTO chat_message (thread_id, sender_id, message_type, text_content, is_read, read_at, created_at) VALUES
(1, 4, 'TEXT', 'Bonjour Docteur, j''ai terminé mon premier test aujourd''hui.', TRUE, CURRENT_TIMESTAMP - INTERVAL '2 hours 55 minutes', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
(1, 2, 'TEXT', 'Bonjour Marie, c''est excellent ! J''ai vu vos résultats, vous avez très bien réussi avec un score de 8.2/10. Comment vous sentez-vous ?', TRUE, CURRENT_TIMESTAMP - INTERVAL '2 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '2 hours 50 minutes'),
(1, 4, 'TEXT', 'Je me sens mieux qu''à ma sortie de l''hôpital. Les exercices de respiration que vous m''avez recommandés m''aident beaucoup.', TRUE, CURRENT_TIMESTAMP - INTERVAL '2 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '2 hours 42 minutes'),
(1, 2, 'TEXT', 'C''est très encourageant. Continuez ainsi. N''hésitez pas à me contacter si vous avez des questions ou si vous ressentez des difficultés.', FALSE, NULL, CURRENT_TIMESTAMP - INTERVAL '2 hours 30 minutes');

-- Conversation between Patient 3 and Dr. Dubois
INSERT INTO chat_message (thread_id, sender_id, message_type, text_content, is_read, read_at, created_at) VALUES
(3, 6, 'TEXT', 'Docteur, j''ai une question sur le prochain test.', TRUE, CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP - INTERVAL '5 hours'),
(3, 3, 'TEXT', 'Bien sûr Claire, je vous écoute.', TRUE, CURRENT_TIMESTAMP - INTERVAL '4 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '4 hours 55 minutes'),
(3, 6, 'TEXT', 'Est-ce que je dois faire l''enregistrement vocal pour chaque test ?', TRUE, CURRENT_TIMESTAMP - INTERVAL '4 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '4 hours 48 minutes'),
(3, 3, 'TEXT', 'L''enregistrement vocal est optionnel mais recommandé. Il nous aide à mieux comprendre votre état émotionnel. Vous pouvez le faire quand vous vous sentez à l''aise.', FALSE, NULL, CURRENT_TIMESTAMP - INTERVAL '4 hours 40 minutes');

-- ============================================
-- NOTIFICATIONS
-- ============================================

INSERT INTO notification (user_id, notification_type, title, message, priority, is_read) VALUES
(4, 'TEST_REMINDER', 'Prochain test disponible', 'Votre prochain test "Échelle d''Anxiété GAD-7" est maintenant disponible.', 'NORMAL', FALSE),
(5, 'TEST_SCHEDULED', 'Test planifié', 'Un nouveau test a été planifié pour demain.', 'NORMAL', FALSE),
(6, 'PHASE_COMPLETED', 'Phase terminée !', 'Félicitations ! Vous avez terminé la Phase 1 avec succès.', 'HIGH', TRUE),
(8, 'MESSAGE_RECEIVED', 'Nouveau message', 'Vous avez reçu un nouveau message de Dr. Martin.', 'NORMAL', FALSE),
(2, 'PATIENT_ALERT', 'Alerte patient', 'Le patient Jean Moreau n''a pas encore commencé son test planifié.', 'HIGH', FALSE);

-- ============================================
-- SAMPLE VOICE RECORDS (metadata only, no actual files)
-- ============================================

INSERT INTO voice_record (patient_id, test_instance_id, file_path, file_size_bytes, duration_seconds, is_encrypted, processing_status, created_at, processed_at) VALUES
(4, 1, '/encrypted/voice/patient_4_test_1_20241209.enc', 245678, 45, TRUE, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '5 minutes');

INSERT INTO voice_analysis_result (voice_record_id, emotion_scores, dominant_emotion, confidence, sentiment_score, stress_level, analyzer_version) VALUES
(1, '{"joy": 0.35, "sadness": 0.15, "neutral": 0.40, "anxiety": 0.10}', 'neutral', 0.72, 6.5, 'MEDIUM', 'v1.2.0');

-- ============================================
-- AUDIT LOG SAMPLES
-- ============================================

INSERT INTO audit_log (entity_type, entity_id, action, user_id, status, created_at) VALUES
('USER', 4, 'LOGIN', 4, 'SUCCESS', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
('TEST_INSTANCE', 1, 'COMPLETE', 4, 'SUCCESS', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('QCM_TEMPLATE', 1, 'CREATE', 2, 'SUCCESS', CURRENT_TIMESTAMP - INTERVAL '30 days'),
('CHAT_MESSAGE', 1, 'SEND', 4, 'SUCCESS', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
('VOICE_RECORD', 1, 'UPLOAD', 4, 'SUCCESS', CURRENT_TIMESTAMP - INTERVAL '2 days');

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Verify data insertion
SELECT 'Users created:' as info, COUNT(*) as count FROM "user"
UNION ALL
SELECT 'Patients:', COUNT(*) FROM patient_profile
UNION ALL
SELECT 'Doctors:', COUNT(*) FROM doctor_profile
UNION ALL
SELECT 'QCM Templates:', COUNT(*) FROM qcm_template
UNION ALL
SELECT 'Test Instances:', COUNT(*) FROM test_instance
UNION ALL
SELECT 'Chat Threads:', COUNT(*) FROM chat_thread
UNION ALL
SELECT 'Messages:', COUNT(*) FROM chat_message;