import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  
  getResponse(userMessage: string): string {
    // Basit bir yanıt döndüren örnek bir chatbot mantığı
    if (userMessage.toLowerCase().includes('merhaba')) {  
      return 'Merhaba! Size nasıl yardımcı olabilirim?';
    } else if (userMessage.toLowerCase().includes('nasılsın')) {
      return 'İyiyim, teşekkür ederim! Siz nasılsınız?';
    } else if (userMessage.toLowerCase().includes('teşekkür')) {
      return 'Rica ederim! Başka bir sorunuz var mı?';
    } else {
      return 'Üzgünüm, bu konuda size yardımcı olamıyorum. Lütfen başka bir şey sorun.';
    } 
  }
}
