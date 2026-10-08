import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatRequest, ChatResponse } from '../models/chat.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  public apiUrl: string = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  sendMessage(request: ChatRequest): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.apiUrl}/api/chat`, request);
  }

  getHistory(sessionId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/chat/history/${sessionId}`);
  }

  clearMemory(sessionId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/chat/memory/${sessionId}`);
  }

  getFaqs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/api/faqs`);
  }
}
