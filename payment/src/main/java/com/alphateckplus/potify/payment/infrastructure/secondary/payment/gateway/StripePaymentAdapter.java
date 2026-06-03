package com.alphateckplus.potify.payment.infrastructure.secondary.payment.gateway;

import com.alphateckplus.potify.payment.application_service.secondary.payment.StripeGatewayPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class StripePaymentAdapter implements StripeGatewayPort {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.frontend-url}")
    private String frontendUrl;

    @Override
    public CheckoutResponse initiateStripePayment(Contribution contribution, String cancelUrl) throws Exception {
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/#/payment/success?method=stripe&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .putMetadata("contribution_id", contribution.getId())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(contribution.getAmount().multiply(new BigDecimal(100)).longValue())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Contribution à la cagnotte")
                                        .setDescription("Merci pour votre soutien !")
                                        .build())
                                .build())
                        .build())
                .build();

        Session session = Session.create(params);

        return new CheckoutResponse(session.getId(), session.getUrl());
    }

    @Override
    public void confirmStripePayment(String sessionId) throws Exception {
        Stripe.apiKey = stripeApiKey;
        Session session = Session.retrieve(sessionId);

        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            throw new IllegalStateException("Le paiement n'a pas été validé par Stripe");
        }
    }
}
