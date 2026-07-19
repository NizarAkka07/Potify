package com.alphateckplus.potify.payment.application_service.primary.payment.withdraw;

import com.alphateckplus.potify.payment.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.PaymentRepositoryPort;
import com.alphateckplus.potify.payment.application_service.secondary.payment.UserCheckPort;
import com.alphateckplus.potify.payment.domain.model.Transaction;
import com.alphateckplus.potify.payment.domain.model.TransactionStatus;
import com.alphateckplus.potify.payment.domain.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
public class DefaultWithdrawService implements WithdrawService {

    private final PaymentRepositoryPort repositoryPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;
    private final UserCheckPort userCheckPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(WithdrawRequest request) throws Exception {
        log.info("Processing withdrawal request for pool: {}, amount: {}", request.getPoolId(), request.getAmount());

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être supérieur à 0");
        }

        // Validate that user is owner using stateless SecurityContext
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé : Utilisateur non authentifié");
        }
        
        String currentUserEmail = auth.getName();
        String authenticatedUserId = userCheckPort.getIdByEmail(currentUserEmail);
        String ownerId = repositoryPort.getPoolOwnerId(request.getPoolId());
        
        boolean isOwner = ownerId != null && ownerId.equals(authenticatedUserId);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_PAYMENT") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("Seul le propriétaire de la cagnotte peut effectuer un retrait");
        }

        // No fees applied on withdrawal anymore, as they are deducted on deposit
        BigDecimal fees = BigDecimal.ZERO;

        // Debit wallet available balance (reserve funds)
        repositoryPort.debitWallet(request.getPoolId(), request.getAmount());

        // Create transaction trace with PENDING status
        Transaction transaction = Transaction.builder()
                .walletId(request.getPoolId())
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .fees(fees)
                .status(TransactionStatus.PENDING)
                .iban(request.getIban())
                .accountHolderName(request.getAccountHolderName())
                .bankName(request.getBankName())
                .build();
        
        repositoryPort.saveTransaction(transaction);

        try {
            String poolTitle = repositoryPort.getPoolTitle(request.getPoolId());
            String title = "Demande de retrait enregistrée";
            String content = "Votre demande de retrait de " + request.getAmount() + "€ pour la cagnotte '" + poolTitle + "' est en cours de traitement (aucun frais supplémentaire n'est prélevé).";
            notificationEventPublisherPort.publish(request.getUserId(), "WITHDRAWAL_REQUESTED", title, content);
        } catch (Exception e) {
            log.error("Erreur lors de la publication de la notification de retrait", e);
        }

        log.info("Withdrawal request created with PENDING status. Amount: {}, Fees applied: {}", request.getAmount(), fees);
    }
}
