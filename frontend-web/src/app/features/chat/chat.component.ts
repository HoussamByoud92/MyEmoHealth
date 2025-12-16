import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { WebSocketService } from '../../core/services/websocket.service';
import { AuthService } from '../../core/services/auth.service';
import { ChatService } from '../../core/services/chat.service';
import { PatientService } from '../../core/services/patient.service';
import { User } from '../../core/models/user.model';
import { ChatMessage } from '../../core/models/chat.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  patients: User[] = [];
  selectedPatient: User | null = null;
  currentUser: User | null = null;
  messages: ChatMessage[] = [];
  newMessage = '';

  private chatSubscription: Subscription | null = null;

  constructor(
    private webSocketService: WebSocketService,
    private authService: AuthService,
    private chatService: ChatService,
    private patientService: PatientService
  ) { }

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    if (this.currentUser && this.authService.token) {
      // Load chat partners based on role
      if (this.currentUser.role === 'PATIENT') {
        // Patient sees Doctors
        this.patientService.getDoctors().subscribe({
          next: (data) => this.patients = data,
          error: (err) => console.error(err)
        });
      } else {
        // Doctor sees Patients (which now returns ALL patients)
        this.patientService.getPatients().subscribe({
          next: (data) => this.patients = data,
          error: (err) => console.error(err)
        });
      }

      // Initialize connection
      this.webSocketService.connect(this.authService.token);

      // Subscribe to my messages queue (for receiving)
      this.chatSubscription = this.webSocketService.watch(`/user/${this.currentUser.id}/queue/messages`)
        .subscribe((message) => {
          const chatMsg: ChatMessage = JSON.parse(message.body);

          // Only append if it belongs to current conversation
          if (this.selectedPatient &&
            (chatMsg.senderId === this.selectedPatient.id || chatMsg.recipientId === this.selectedPatient.id)) {
            this.messages.push(chatMsg);
            this.scrollToBottom();
          }
        });
    }
  }

  ngOnDestroy() {
    this.chatSubscription?.unsubscribe();
    this.webSocketService.deactivate();
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  selectPatient(patient: User) {
    this.selectedPatient = patient;
    this.loadHistory();
  }

  loadHistory() {
    if (!this.selectedPatient || !this.currentUser) return;

    this.chatService.getChatHistory(this.currentUser.id, this.selectedPatient.id)
      .subscribe(history => {
        this.messages = history;
        this.scrollToBottom();
      });
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.selectedPatient || !this.currentUser) return;

    const msg: ChatMessage = {
      senderId: this.currentUser.id,
      recipientId: this.selectedPatient.id,
      content: this.newMessage,
      type: 'TEXT'
    };

    // Send via REST API instead of WebSocket
    console.log('DEBUG PAYLOAD:', JSON.stringify(msg));
    this.chatService.sendMessage(msg).subscribe({
      next: (savedMsg) => {
        // Message saved successfully, add to UI
        this.messages.push(savedMsg);
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Failed to send message:', err);
        // Optionally show error to user
      }
    });

    this.newMessage = '';
  }

  isSentByMe(msg: ChatMessage): boolean {
    return msg.senderId === this.currentUser?.id;
  }

  scrollToBottom(): void {
    if (this.scrollContainer) {
      this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    }
  }

  startVideoCall() {
    // To be implemented in next phase
    console.log('Starting video call with', this.selectedPatient?.firstName);
  }
}
