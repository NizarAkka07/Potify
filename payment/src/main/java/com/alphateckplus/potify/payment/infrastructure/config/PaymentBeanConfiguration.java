package com.alphateckplus.potify.payment.infrastructure.config;

import com.alphateckplus.potify.payment.application_service.primary.payment.confirm_paypal_payment.ConfirmPayPalPaymentService;
import com.alphateckplus.potify.payment.application_service.primary.payment.confirm_paypal_payment.DefaultConfirmPayPalPaymentService;
import com.alphateckplus.potify.payment.application_service.primary.payment.confirm_stripe_payment.ConfirmStripePaymentService;
import com.alphateckplus.potify.payment.application_service.primary.payment.confirm_stripe_payment.DefaultConfirmStripePaymentService;
import com.alphateckplus.potify.payment.application_service.primary.payment.get_pool_contributions.DefaultGetPoolContributionsService;
import com.alphateckplus.potify.payment.application_service.primary.payment.get_pool_contributions.GetPoolContributionsService;
import com.alphateckplus.potify.payment.application_service.primary.payment.get_transactions.DefaultGetTransactionsService;
import com.alphateckplus.potify.payment.application_service.primary.payment.get_transactions.GetTransactionsService;
import com.alphateckplus.potify.payment.application_service.primary.payment.get_user_contributions.DefaultGetUserContributionsService;
import com.alphateckplus.potify.payment.application_service.primary.payment.get_user_contributions.GetUserContributionsService;
import com.alphateckplus.potify.payment.application_service.primary.payment.initiate_paypal_payment.DefaultInitiatePayPalPaymentService;
import com.alphateckplus.potify.payment.application_service.primary.payment.initiate_paypal_payment.InitiatePayPalPaymentService;
import com.alphateckplus.potify.payment.application_service.primary.payment.initiate_stripe_payment.DefaultInitiateStripePaymentService;
import com.alphateckplus.potify.payment.application_service.primary.payment.initiate_stripe_payment.InitiateStripePaymentService;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PayPalGatewayPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.StripeGatewayPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentBeanConfiguration {

    @Value("${stripe.frontend-url}")
    private String frontendUrl;

    @Bean
    public InitiateStripePaymentService initiateStripePaymentService(
            PaymentRepositoryPort repositoryPort,
            StripeGatewayPort stripeGatewayPort) {
        return new DefaultInitiateStripePaymentService(repositoryPort, stripeGatewayPort, frontendUrl);
    }

    @Bean
    public ConfirmStripePaymentService confirmStripePaymentService(
            PaymentRepositoryPort repositoryPort,
            StripeGatewayPort stripeGatewayPort,
            com.alphateckplus.potify.payment.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultConfirmStripePaymentService(repositoryPort, stripeGatewayPort, notificationEventPublisherPort);
    }

    @Bean
    public InitiatePayPalPaymentService initiatePayPalPaymentService(
            PaymentRepositoryPort repositoryPort,
            PayPalGatewayPort payPalGatewayPort) {
        return new DefaultInitiatePayPalPaymentService(repositoryPort, payPalGatewayPort, frontendUrl);
    }

    @Bean
    public ConfirmPayPalPaymentService confirmPayPalPaymentService(
            PaymentRepositoryPort repositoryPort,
            PayPalGatewayPort payPalGatewayPort,
            com.alphateckplus.potify.payment.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultConfirmPayPalPaymentService(repositoryPort, payPalGatewayPort, notificationEventPublisherPort);
    }

    @Bean
    public GetPoolContributionsService getPoolContributionsService(PaymentRepositoryPort repositoryPort) {
        return new DefaultGetPoolContributionsService(repositoryPort);
    }

    @Bean
    public GetUserContributionsService getUserContributionsService(PaymentRepositoryPort repositoryPort) {
        return new DefaultGetUserContributionsService(repositoryPort);
    }

    @Bean
    public GetTransactionsService getTransactionsService(PaymentRepositoryPort repositoryPort) {
        return new DefaultGetTransactionsService(repositoryPort);
    }

    @Bean
    public com.alphateckplus.potify.payment.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort(
            org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate) {
        return new com.alphateckplus.potify.payment.infrastructure.secondary.notification.KafkaNotificationEventPublisherAdapter(kafkaTemplate);
    }
}
