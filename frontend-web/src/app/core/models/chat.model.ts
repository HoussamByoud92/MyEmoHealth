export interface ChatMessage {
    id?: number;
    senderId: number;
    recipientId: number;
    content: string;
    type: 'TEXT' | 'IMAGE' | 'VIDEO_CALL_START';
    timestamp?: string;
    isRead?: boolean;
}

export interface VoiceSession {
    id: number;
    patientId: number;
    userTranscript: string;
    aiResponse: string;
    sentimentDetected: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE' | 'ANXIOUS' | 'DEPRESSIVE';
    riskScore: number;
    timestamp: string;
}
