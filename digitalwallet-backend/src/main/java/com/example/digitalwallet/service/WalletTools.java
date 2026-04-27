package com.example.digitalwallet.service;

import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.repository.WalletRepository;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WalletTools {

    private final WalletRepository walletRepository;

    public WalletTools(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

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
}
