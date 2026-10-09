export interface ChatRequest {
  message: string;
  sessionId: string;
  userId?: number;
}

export interface ChatResponse {
  reply: string;
  matched: boolean;
  matchedQuestion?: string | null;
  category?: string | null;
  confidence: number;
  source: string;
  sessionId: string;
  resolvedQuestion?: string;
}

export interface ChatBubble {
  sender: 'user' | 'bot';
  text: string;
  time?: string;
  matchedQuestion?: string | null;
  confidence?: number;
}
