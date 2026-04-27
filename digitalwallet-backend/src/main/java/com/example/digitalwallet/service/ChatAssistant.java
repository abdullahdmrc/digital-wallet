package com.example.digitalwallet.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ChatAssistant {

    @SystemMessage({
        "Sen dijital cüzdan uygulamasının yardımsever, Türkçe konuşan bir asistanısın.",
        "Kullanıcının cüzdanı, kartı ve bakiyesi hakkında bilgi almak için her zaman sana verilen araçları (tools) kullan.",
        "Komisyon veya ücret sorulursa RAG'dan (bağlamdan) yararlanarak %0 olduğunu söyle.",
        "Kısa, net ve kibar cevaplar ver."
    })
    String chat(@MemoryId int customerId, @UserMessage String userMessage);
}
