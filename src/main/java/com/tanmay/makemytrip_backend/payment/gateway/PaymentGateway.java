package com.tanmay.makemytrip_backend.payment.gateway;

import java.math.BigDecimal;

public interface PaymentGateway {

    PaymentResult processPayment(BigDecimal amount);
}