package com.alphateckplus.potify.payment.application_service.primary.payment.initiate_stripe_payment;

import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.StripeGatewayPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.domain.model.ContributionStatus;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutRequest;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class DefaultInitiateStripePaymentService implements InitiateStripePaymentService {

    private final PaymentRepositoryPort repositoryPort;
    private final StripeGatewayPort stripeGatewayPort;
    private final String frontendUrl;

    @Override
    @Transactional
    public CheckoutResponse execute(CheckoutRequest request) throws Exception {
        log.info("Initiating Stripe payment flow for pool: {}", request.getPoolId());

        Contribution contribution = Contribution.builder()
                .poolId(request.getPoolId() != null ? request.getPoolId().toString() : null)
                .userId(request.getUserId() != null ? request.getUserId().toString() : null)
                .amount(request.getAmount())
                .contributorEmail(request.getContributorEmail())
                .contributorName(request.isAnonymous() ? "Donateur anonyme" : request.getContributorName())
                .message(request.getMessage())
                .anonymous(request.isAnonymous())
                .paymentMethod("STRIPE")
                .status(ContributionStatus.PENDING)
                .build();

        contribution = repositoryPort.saveContribution(contribution);

        String cancelUrl = frontendUrl + "/#/pools/" + (request.getPoolId() != null ? request.getPoolId().toString() : "");
        CheckoutResponse checkoutResponse = stripeGatewayPort.initiateStripePayment(contribution, cancelUrl);

        contribution.setPaymentMethod("STRIPE:" + checkoutResponse.getSessionId());
        repositoryPort.saveContribution(contribution);

        return checkoutResponse;
    }
}
