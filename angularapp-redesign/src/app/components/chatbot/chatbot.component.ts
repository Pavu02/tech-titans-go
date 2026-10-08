import { Component, OnInit } from '@angular/core';
import { ChatService } from '../../services/chat.service';
import { ChatBubble } from '../../models/chat.model';

@Component({
  selector: 'app-chatbot',
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent implements OnInit {
  isOpen: boolean = false;
  sessionId: string = '';
  userQuery: string = '';
  isLoading: boolean = false;
  messages: ChatBubble[] = [];

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    let sid = sessionStorage.getItem('chatSessionId');
    if (!sid) {
      sid = 'session-' + Math.random().toString(36).substring(2, 9);
      sessionStorage.setItem('chatSessionId', sid);
    }
    this.sessionId = sid;

    // Welcome message
    this.messages.push({
      sender: 'bot',
      text: "Hi! 👋 I'm the BookHeaven assistant. Ask me about books, renting a book, rental status, or feedback."
    });
  }

  toggleChat(): void {
    this.isOpen = !this.isOpen;
  }

  sendMessage(): void {
    if (!this.userQuery || this.userQuery.trim() === '' || this.isLoading) {
      return;
    }

    const query = this.userQuery.trim();
    this.messages.push({
      sender: 'user',
      text: query,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    });
    this.userQuery = '';
    this.isLoading = true;

    this.chatService.sendMessage({ message: query, sessionId: this.sessionId }).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.messages.push({
          sender: 'bot',
          text: res.reply,
          matchedQuestion: res.matchedQuestion,
          confidence: res.confidence,
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        });
      },
      error: () => {
        this.isLoading = false;
        this.messages.push({
          sender: 'bot',
          text: 'Sorry, I encountered an error connecting to the assistant. Please try again.',
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        });
      }
    });
  }

  clearChat(): void {
    this.chatService.clearMemory(this.sessionId).subscribe({
      next: () => {
        this.messages = [
          {
            sender: 'bot',
            text: "Conversation cleared. How can I help you today?"
          }
        ];
      },
      error: () => {
        this.messages = [];
      }
    });
  }
}
