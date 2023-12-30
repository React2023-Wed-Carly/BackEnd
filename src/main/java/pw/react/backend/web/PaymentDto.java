package pw.react.backend.web;
import pw.react.backend.models.Payment;
public record PaymentDto(Long id, Long userId, String date, Double amount) {
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