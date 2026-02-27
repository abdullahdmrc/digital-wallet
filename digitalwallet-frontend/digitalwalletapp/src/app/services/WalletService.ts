import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class WalletService {

  private baseUrl = "http://localhost:8081/api/wallets"

  constructor(private http: HttpClient) { }

  createWallet(data: any): Observable<any> {
    return this.http.post(this.baseUrl, data);
  }

  getWallets(): Observable<any> {
    return this.http.get(this.baseUrl);
  }


}