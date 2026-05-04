package com.example.digitalwallet.service;

import com.example.digitalwallet.model.Transaction;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.repository.TransactionRepository;
import com.example.digitalwallet.repository.UserRepository;
import com.example.digitalwallet.repository.WalletRepository;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WalletTools {

    /*
    * In this class , we define all the user interactions with chatbot
    * All methods defining done here
    *
    *
    *
     */
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;


    @Tool("Müşterinin mevcut cüzdanlarını (kartlarını) ve bunların kullanılabilir bakiyelerini getirir.")
    public String getWalletBalances(@ToolMemoryId int customerId) {
        List<Wallet> wallets = walletRepository.getWalletByCustomer_Id(customerId);
        if (wallets == null || wallets.isEmpty()) {
            return "Müşterinin henüz bir cüzdanı/kartı bulunmamaktadır.";
        }
        return wallets.stream()
                .map(w -> "Cüzdan Adı: " + w.getWalletName() + 
                          ", Kullanılabilir Bakiye: " + w.getUsableBalance() + " " + w.getCurrency())
                .collect(Collectors.joining("; "));
    }

    @Tool("Kullanıcının son finansal işlemlerini listeler. Eğer kullanıcı sayı belirtmemişse, bu parametre boş (null) olabilir.")
    public String getRecentTransactions(Integer number) {

        int limit = (number == null) ? 3 : number;
        int currentUserId = getCurrentUserId();

        List<Transaction> recentTransactions = transactionRepository
                .findByWallet_Customer_User_IdOrderByDateDesc(currentUserId, PageRequest.of(0, limit));

        if (recentTransactions.isEmpty()) {
            return "Kullanıcının henüz hiçbir işlemi bulunmamaktadır.";
        }

        return recentTransactions.stream()
                .map(t -> "Tarih: " + t.getDate() + ", Tutar: " + t.getAmount() + " TL")
                .collect(Collectors.joining(" | "));
    }

    @Tool("Kullanıcının belirli bir harcama kategorisindeki (FOOD, SHOPPING, FUEL, CINEMA, TRAVEL) harcamalarının toplamını ve listesini döner.")
    public String getSpendingByCategory(Transaction.SpendingCategory category) {

        if (category == null) {
            return "Lütfen sorgulamak istediğiniz kategoriyi (FOOD, SHOPPING vb.) belirtin.";
        }

        int currentUserId = getCurrentUserId();

        List<Transaction> transactions = transactionRepository
                .findByWallet_Customer_User_IdAndSpendingCategory(currentUserId, category);

        if (transactions.isEmpty()) {
            return category.name() + " kategorisinde henüz hiçbir harcamanız bulunmamaktadır.";
        }

        double totalSpent = transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.WITHDRAW)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return String.format("%s kategorisinde toplam harcamanız: %.2f TL. (Toplam %d adet işlem bulundu)",
                category.name(), totalSpent, transactions.size());
    }

    @Tool("Kullanıcı aylık bazda (Örn: Nisan ayı) hesap özeti ve kategori bazlı harcama dağılımını isterse bu aracı kullan. Aydan sadece sayıyı çıkarıp gönder.")
    public String getMonthlySummarize(Integer targetMonth) {

        if (targetMonth == null || targetMonth < 1 || targetMonth > 12) {
            return "Lütfen hesap özeti istediğiniz ayın adını veya numarasını (Örn: Nisan) açıkça belirtin.";
        }
        int currentUserId = getCurrentUserId();

        List<Transaction> allTransactions = transactionRepository.findAllByWallet_Customer_User_Id(currentUserId);

        List<Transaction> monthlyWithdrawals = allTransactions.stream()
                .filter(t -> t.getType() == Transaction.Type.WITHDRAW)
                .filter(t -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(t.getDate());
                    int txMonth = cal.get(Calendar.MONTH) + 1;
                    return txMonth == targetMonth;
                })
                .collect(Collectors.toList());

        String[] months = {"", "Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};


        if (monthlyWithdrawals.isEmpty()) {
            return months[targetMonth] + " ayında henüz hiçbir harcamanız bulunmamaktadır.";
        }


        Map<Transaction.SpendingCategory, Double> categoryTotals = monthlyWithdrawals.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getSpendingCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));


        double grandTotal = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();


        StringBuilder sb = new StringBuilder();
        sb.append(months[targetMonth]).append(" Ayı Hesap Özeti:\n\n");
        sb.append("| Harcama Kategorisi | Toplam Tutar |\n");
        sb.append("|---|---|\n");

        for (Map.Entry<Transaction.SpendingCategory, Double> entry : categoryTotals.entrySet()) {
            sb.append(String.format("| %s | %.2f TL |\n", entry.getKey(), entry.getValue()));
        }

        sb.append(String.format("\n**Aylık Genel Toplam:** %.2f TL\n", grandTotal));
        sb.append(String.format("*(Bu ay toplam %d adet işlem yaptınız)*", monthlyWithdrawals.size()));

        return sb.toString();
    }



    public int getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<User> user=userRepository.findByUsername(currentPrincipalName);
        return user.get().getId();

    }
}
