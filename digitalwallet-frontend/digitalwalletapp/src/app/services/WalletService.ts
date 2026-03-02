import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";



@Injectable({
  providedIn: 'root'
})
export class WalletService {

  private baseUrl = "http://localhost:8081/api/wallets"
  private depositUrl="http://localhost:8081/api/deposits"
  private withdrawUrl="http://localhost:8081/api/withdraws"

  constructor(private http: HttpClient) { }

  createWallet(data: any): Observable<any> {
    return this.http.post(this.baseUrl, data);
  }

  getWallets(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

 getWalletById(id: any): Observable<any> {
  return this.http.get(`${this.baseUrl}/${id}`);
 }
 
 getTransactionsByWallet(id: any) :Observable<any>{
  return this.http.get(`${this.baseUrl}/${id}/transactions`);
 }
 
 deposit(data: any): Observable<any> {
  return this.http.post(this.depositUrl,data);
 }

 withDraw(data: any): Observable<any> {
  return this.http.post(this.withdrawUrl,data);
 }


}