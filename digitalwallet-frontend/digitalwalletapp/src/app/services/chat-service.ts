import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs'; 

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  
  private chatApiUrl = 'http://localhost:8081/api/chat';
  private http = inject(HttpClient);

  getResponse(userMessage: string): Observable<string> {
    
    
    const requestBody = { message: userMessage };

    return this.http.post(this.chatApiUrl, requestBody, {
      responseType: 'text' 
    });
  }
}