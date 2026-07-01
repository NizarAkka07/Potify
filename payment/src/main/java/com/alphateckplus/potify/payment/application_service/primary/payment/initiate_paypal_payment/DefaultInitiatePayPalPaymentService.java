package com.alphateckplus.potify.payment.application_service.primary.payment.initiate_paypal_payment;

import com.alphateckplus.potify.payment.application_service.secondary.payment.PayPalGatewayPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.domain.model.ContributionStatus;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutRequest;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class DefaultInitiatePayPalPaymentService implements InitiatePayPalPaymentService {

    private final PaymentRepositoryPort repositoryPort;
    private final PayPalGatewayPort payPalGatewayPort;
    private final String frontendUrl;

    @Override
    @Transactional
    public CheckoutResponse execute(CheckoutRequest request) throws Exception {
        log.info("Initiating PayPal payment flow for pool: {}", request.getPoolId());

        Contribution contribution = Contribution.builder()
                .poolId(request.getPoolId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .contributorEmail(request.getContributorEmail())
                .contributorName(request.isAnonymous() ? "Donateur anonyme" : request.getContributorName())
                .message(request.getMessage())
                .anonymous(request.isAnonymous())
                .paymentMethod("PAYPAL")
                .status(ContributionStatus.PENDING)
                .build();

        contribution = repositoryPort.saveContribution(contribution);

        String cancelUrl = frontendUrl + "/#/pools/" + (request.getPoolId() != null ? request.getPoolId().toString() : "");
        CheckoutResponse checkoutResponse = payPalGatewayPort.initiatePayPalPayment(contribution, cancelUrl);

        contribution.setPaymentMethod("PAYPAL:" + checkoutResponse.getSessionId());
        repositoryPort.saveContribution(contribution);

        return checkoutResponse;
    }
}
