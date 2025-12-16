import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ChatMessage } from '../models/chat.model';

@Injectable({
    providedIn: 'root'
})
export class ChatService {
    private apiUrl = `${environment.apiUrl}/chat`;

    constructor(private http: HttpClient) { }

    getChatHistory(userId1: number, userId2: number): Observable<ChatMessage[]> {
        return this.http.get<ChatMessage[]>(`${this.apiUrl}/history/${userId1}/${userId2}`);
    }

    sendMessage(message: ChatMessage): Observable<ChatMessage> {
        return this.http.post<ChatMessage>(`${this.apiUrl}/send`, message);
    }
}
