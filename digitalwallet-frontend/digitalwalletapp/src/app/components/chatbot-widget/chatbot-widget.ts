import { Component, inject, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { ChatService } from '../../services/chat-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Message {
  sender: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-chatbot-widget',
  standalone: true, 
  imports: [CommonModule, FormsModule], 
  templateUrl: './chatbot-widget.html',
  styleUrl: './chatbot-widget.css',
})
export class ChatbotWidget implements AfterViewChecked {

  private chatService = inject(ChatService);

  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  isOpen = false;
  userInput = ''; 
  isTyping = false; 
  messages: Message[] = [
    {sender: 'bot', text: 'Merhaba, size nasıl yardımcı olabilirim?'}
  ];

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  toggleChat(){
    this.isOpen = !this.isOpen;
    if(this.isOpen){
      setTimeout(() => this.scrollToBottom(), 100);
    }
  }

  sendMessage(event?: Event){

      if(event){
        event.preventDefault(); 
      }

      if(!this.userInput.trim()) return; 

      const userMessage = this.userInput;

      this.messages.push({sender: 'user', text: userMessage});
      this.userInput = ''; 
      this.isTyping = true;
      setTimeout(() => this.scrollToBottom(), 50);

    
      this.chatService.getResponse(userMessage).subscribe({
        
        next: (botResponse) => {
          this.messages.push({sender: 'bot', text: botResponse});
          this.isTyping = false; 
          setTimeout(() => this.scrollToBottom(), 50); 
        },

        
        error: (error) => {
          console.error('Chatbot error: ', error);
          this.messages.push({sender: 'bot', text: 'Üzgünüm, şuanda size yanıt veremiyorum.'});
          this.isTyping = false; 
          setTimeout(() => this.scrollToBottom(), 50);
        }
        
      });
  }

  private scrollToBottom(): void {
    try {
      if(this.myScrollContainer){
        this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
      }
    } catch (error){
      console.error('Scroll error: ', error);
    }
  }
}