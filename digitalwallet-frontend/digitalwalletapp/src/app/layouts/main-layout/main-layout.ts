import { Component } from '@angular/core';
import { RouterModule, RouterLink } from '@angular/router';
import { ChatbotWidget } from '../../components/chatbot-widget/chatbot-widget';

@Component({
  selector: 'app-main-layout',
  imports: [RouterLink, RouterModule, ChatbotWidget],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css',
})
export class MainLayout {

}
