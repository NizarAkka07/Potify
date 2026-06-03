package com.alphateckplus.potify.payment.application_service.primary.payment.confirm_stripe_payment;

import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.StripeGatewayPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.domain.model.ContributionStatus;
import com.alphateckplus.potify.payment.domain.model.Transaction;
import com.alphateckplus.potify.payment.domain.model.TransactionStatus;
import com.alphateckplus.potify.payment.domain.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
public class DefaultConfirmStripePaymentService implements ConfirmStripePaymentService {

    private final PaymentRepositoryPort repositoryPort;
    private final StripeGatewayPort stripeGatewayPort;

    @Override
    @Transactional
    public void execute(String sessionId) throws Exception {
        log.info("Confirming Stripe payment for session: {}", sessionId);

        stripeGatewayPort.confirmStripePayment(sessionId);

        String stripeSessionId = sessionId.startsWith("STRIPE:") ? sessionId : "STRIPE:" + sessionId;
        Contribution contribution = repositoryPort.findContributionByPaymentMethod(stripeSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Contribution introuvable pour la session Stripe: " + sessionId));

        if (contribution.getStatus() == ContributionStatus.CONFIRMED) {
            log.info("Stripe payment already confirmed for contribution: {}", contribution.getId());
            return;
        }

        processSuccessfulPayment(contribution);
    }

    private void processSuccessfulPayment(Contribution contribution) {
        contribution.setStatus(ContributionStatus.CONFIRMED);
        repositoryPort.saveContribution(contribution);

        repositoryPort.creditPoolAndWallet(contribution.getPoolId(), contribution.getAmount());

        Transaction transaction = Transaction.builder()
                .walletId(contribution.getPoolId())
                .contributionId(contribution.getId())
                .type(TransactionType.DEPOSIT)
                .amount(contribution.getAmount())
                .fees(BigDecimal.ZERO)
                .status(TransactionStatus.SUCCESS)
                .build();
        repositoryPort.saveTransaction(transaction);

        log.info("Payment successfully processed for contribution ID: {}", contribution.getId());
    }
}
