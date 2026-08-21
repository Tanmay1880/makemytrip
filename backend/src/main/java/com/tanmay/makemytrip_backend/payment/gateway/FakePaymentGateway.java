package com.tanmay.makemytrip_backend.payment.gateway;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FakePaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult processPayment(BigDecimal amount) {

        // Temporary simulation.
        // Later this will be replaced by a real payment provider.
        return PaymentResult.SUCCESS;
    }
}