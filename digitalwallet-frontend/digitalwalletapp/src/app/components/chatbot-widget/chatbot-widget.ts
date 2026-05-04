import { Component, inject, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { ChatService } from '../../services/chat-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 

/*@Component({
  selector: 'app-chatbot-widget',
  standalone: true, 
  imports: [], 
  templateUrl: './chatbot-widget.html',
  styleUrl: './chatbot-widget.css',
})
export class ChatbotWidget  {

  
}
*/

// Message arayüzü (interface): Mesaj objelerinin yapısını (hangi özelliklere sahip olacağını) belirler.
// sender: 'user' veya 'bot' olabilir, text: mesajın içeriği
interface Message {
  sender: 'user' | 'bot';
  text: string;
}

// @Component dekoratörü: Bu sınıfın bir Angular bileşeni olduğunu belirtir.
// selector: HTML içinde bu bileşeni çağırmak için kullanılacak etiket adı (örn: <app-chatbot-widget>)
// imports: Bu bileşende kullanılacak diğer modüller (Standalone component yapısı gereği)
// templateUrl: Bileşenin HTML görünümü
// styleUrl: Bileşenin CSS stili
@Component({
  selector: 'app-chatbot-widget',
  standalone: true, // Angular 14+ ile gelen bağımsız bileşen özelliği
  imports: [CommonModule, FormsModule], // CommonModule: *ngIf, *ngFor gibi yapısal direktifler için. FormsModule: [(ngModel)] için.
  templateUrl: './chatbot-widget.html',
  styleUrl: './chatbot-widget.css',
})
export class ChatbotWidget implements AfterViewChecked {

  // inject(): Angular 14+ ile gelen bağımlılık enjeksiyonu yöntemi. ChatService'i kullanmamızı sağlar.
  private chatService = inject(ChatService);

  // @ViewChild: HTML şablonundaki (template) bir elemente DOM üzerinden erişmemizi sağlar.
  // '#scrollMe' id'sine sahip div elementini referans alır.
  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  // Değişkenler (State / Durum yönetimi)
  isOpen = false; // Chat panelinin açık/kapalı durumunu tutar
  userInput = ''; // Kullanıcının input alanına girdiği metni tutar (ngModel ile çift yönlü bağlıdır)
  isTyping = false; // Bot cevap verirken 'yazıyor...' animasyonunu göstermek için bayrak (flag)
  messages: Message[] = [ // Başlangıçta gösterilecek varsayılan mesaj
    { sender: 'bot', text: 'Merhaba! Size nasıl yardımcı olabilirim?' }
  ];

  // Angular yaşam döngüsü hook'u: Görünüm (ve alt görünümler) her kontrol edildiğinde çalışır.
  // Yeni bir mesaj eklendiğinde scroll'un en alta inmesini sağlamak için kullanıyoruz.
  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  // Chat panelini açıp kapatan fonksiyon
  toggleChat() {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      // Panel açıldığında biraz gecikmeli olarak en alta kaydır
      setTimeout(() => this.scrollToBottom(), 100);
    }
  }

  // Mesaj gönderme işlemini yöneten asenkron fonksiyon
  async sendMessage(event?: Event) {
    // Sayfanın yenilenmesini engeller (form submit varsayılan davranışı)
    if (event) {
      event.preventDefault();
    }

    // Girdi boşluklardan temizlenip boşsa (sadece space girilmişse) gönderme
    if (!this.userInput.trim()) return;

    const userMessage = this.userInput; // Kullanıcının mesajını geçici değişkene al
    
    // 1. Kullanıcının mesajını ekrana (messages dizisine) ekle
    this.messages.push({ sender: 'user', text: userMessage });
    this.userInput = ''; // Input alanını temizle
    this.isTyping = true; // Bot yazıyor animasyonunu başlat

    try {
      // 2. Mesajı ChatService üzerinden arka plana (veya simüle edilmiş servise) gönder ve cevabı bekle
      // await kelimesi, promise çözülene kadar beklemeyi sağlar
      const response = await this.chatService.getResponse(userMessage);
      
      // 3. Gelen cevabı bot mesajı olarak ekrana ekle
      this.messages.push({ sender: 'bot', text: response });
    } catch (error) {
      console.error('Chatbot error:', error);
      this.messages.push({ sender: 'bot', text: 'Üzgünüm, bir hata oluştu.' });
    } finally {
      // 4. İşlem bitince (başarılı ya da hatalı) yazıyor animasyonunu kapat
      this.isTyping = false;
    }
  }

  // Mesajlar div'ini her zaman en alt kaydıran (scroll) yardımcı fonksiyon
  private scrollToBottom(): void {
    try {
      if (this.myScrollContainer) {
        this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
      }
    } catch(err) { 
        // Hata olursa sessizce yut
    }
  }
}
