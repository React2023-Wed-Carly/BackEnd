package pw.react.backend.web;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pw.react.backend.models.Payment;
import pw.react.backend.utils.JsonDateDeserializer;
import pw.react.backend.utils.JsonDateSerializer;

import java.time.LocalDate;

public record PaymentDto(Long id, Long userId,
                         @JsonDeserialize(using = JsonDateDeserializer.class) @JsonSerialize(using = JsonDateSerializer.class)
                         LocalDate date, Long amount) {
    public static PaymentDto valueFrom(Payment p) {
        return new PaymentDto(p.getId(), p.getUserId(), p.getDate(), p.getAmount());
    }

    public static Payment ConvertToPayment(PaymentDto pd) {
        Payment payment = new Payment();
        payment.setId(pd.id);
        payment.setUserId(pd.userId);
        payment.setDate(pd.date);
        payment.setAmount(pd.amount);
        return payment;
    }
}