package com.example.digitalwallet.service.impl;

import com.example.digitalwallet.model.Transaction;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

@Service
public class PdfService {

    public byte[] generateReceiptPdf(Transaction transaction) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Islem Dekontu", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Islem ID: " + transaction.getId(), normalFont));
            document.add(new Paragraph("Tarih: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(transaction.getDate()), normalFont));
            document.add(new Paragraph("Islem Tipi: " + transaction.getType().name(), normalFont));
            document.add(new Paragraph("Miktar: " + transaction.getAmount() + " " + transaction.getWallet().getCurrency(), normalFont));

            if (transaction.getOppositePartyType() == Transaction.OppositePartyType.IBAN) {
                if (transaction.getType() == Transaction.Type.WITHDRAW) {
                    document.add(new Paragraph("Gonderici Cüzdan: " + transaction.getWallet().getWalletName(), normalFont));
                    document.add(new Paragraph("Alici IBAN: " + transaction.getOppositeParty(), normalFont));
                } else {
                    document.add(new Paragraph("Alici Cüzdan: " + transaction.getWallet().getWalletName(), normalFont));
                    document.add(new Paragraph("Gonderen IBAN: " + transaction.getOppositeParty(), normalFont));
                }
            } else {
                document.add(new Paragraph("Karsi Taraf: " + transaction.getOppositeParty(), normalFont));
            }

            document.add(new Paragraph("Durum: " + transaction.getStatus().name(), normalFont));

            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Bu belge dijital olarak uretilmistir.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF olusturulurken hata olustu", e);
        }
    }
}
