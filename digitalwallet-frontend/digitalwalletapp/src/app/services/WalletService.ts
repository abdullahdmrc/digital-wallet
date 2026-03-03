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
  private transactionUrl="http://localhost:8081/api/transactions"
  private approveUrl="http://localhost:8081/api/transactions/approve"
  private denyUrl="http://localhost:8081/api/transactions/deny"

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

 getAllTransactions(){
  return this.http.get(`${this.transactionUrl}`)
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

 approveTransaction(id: any) {
  return this.http.post(`${this.approveUrl}/${id}`, {}); 
}

denyTransaction(id: any) {
  return this.http.post(`${this.denyUrl}/${id}`, {});
}


}