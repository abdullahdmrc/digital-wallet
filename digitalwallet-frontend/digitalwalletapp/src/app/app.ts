import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { RouterLink,RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-root',
  imports: [RouterLink,RouterOutlet],
 template: `
    <router-outlet></router-outlet>
  `,
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('digitalwalletapp');

 
}
