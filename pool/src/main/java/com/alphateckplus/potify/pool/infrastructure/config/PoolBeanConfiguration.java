package com.alphateckplus.potify.pool.infrastructure.config;

import com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution.CreateContributionService;
import com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution.DefaultCreateContributionService;
import com.alphateckplus.potify.pool.application_service.primary.contribution.list_contributions.DefaultListContributionsService;
import com.alphateckplus.potify.pool.application_service.primary.contribution.list_contributions.ListContributionsService;
import com.alphateckplus.potify.pool.application_service.primary.pool.create_pool.CreatePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.create_pool.DefaultCreatePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.delete_pool.DefaultDeletePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.delete_pool.DeletePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.DefaultGetPoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.GetPoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.list_pools.DefaultListPoolsService;
import com.alphateckplus.potify.pool.application_service.primary.pool.list_pools.ListPoolsService;
import com.alphateckplus.potify.pool.application_service.primary.pool.message.DefaultMessageService;
import com.alphateckplus.potify.pool.application_service.primary.pool.message.MessageService;
import com.alphateckplus.potify.pool.application_service.secondary.pool.MessageRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.ReactionRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.WalletRepositoryPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.MessagePersistenceMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.WalletPersistenceMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository.MessageJpaAdapter;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository.ReactionJpaAdapter;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository.WalletJpaAdapter;
import com.alphateckplus.potify.data_jpa.repository.pool.MessageEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.CommentReactionEntityRepository;
import com.alphateckplus.potify.pool.application_service.primary.pool.invitation.DefaultInvitationService;
import com.alphateckplus.potify.pool.application_service.primary.pool.invitation.InvitationService;
import com.alphateckplus.potify.pool.application_service.secondary.pool.InvitationRepositoryPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.InvitationPersistenceMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository.InvitationJpaAdapter;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolInvitationEntityRepository;
import com.alphateckplus.potify.pool.application_service.primary.pool.update_pool.DefaultUpdatePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.update_pool.UpdatePoolService;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository.PoolJpaAdapter;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.PoolPersistenceMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.PhasePersistenceMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.contribution.repository.ContributionJpaAdapter;
import com.alphateckplus.potify.pool.infrastructure.secondary.contribution.mapper.ContributionPersistenceMapper;
import com.alphateckplus.potify.pool.application_service.secondary.contribution.ContributionRepositoryPort;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.CagnotteWalletEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.payment.ContributionEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.notification.EmailNotificationAdapter;
import com.alphateckplus.potify.pool.application_service.secondary.pool.UserCheckPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.UserCheckAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Configuration explicite des beans du microservice pool.
 *
 * <p>Centralise le wiring Spring pour garder les classes applicatives pures.
 */
@Configuration
public class PoolBeanConfiguration {

    @Bean
    public ReactionRepositoryPort reactionRepositoryPort(
            CommentReactionEntityRepository reactionRepository,
            MessageEntityRepository messageRepository,
            UserEntityRepository userRepository) {
        return new ReactionJpaAdapter(reactionRepository, messageRepository, userRepository);
    }

    @Bean
    public WalletPersistenceMapper walletPersistenceMapper() {
        return new WalletPersistenceMapper();
    }

    @Bean
    public WalletRepositoryPort walletRepositoryPort(
            CagnotteWalletEntityRepository walletEntityRepository,
            PoolEntityRepository poolEntityRepository,
            WalletPersistenceMapper walletPersistenceMapper) {
        return new WalletJpaAdapter(walletEntityRepository, poolEntityRepository, walletPersistenceMapper);
    }

    @Bean
    public InvitationPersistenceMapper invitationPersistenceMapper() {
        return new InvitationPersistenceMapper();
    }

    @Bean
    public InvitationRepositoryPort invitationRepositoryPort(
            PoolInvitationEntityRepository invitationRepository,
            PoolEntityRepository poolRepository,
            InvitationPersistenceMapper invitationPersistenceMapper) {
        return new InvitationJpaAdapter(invitationRepository, poolRepository, invitationPersistenceMapper);
    }

    @Bean
    public MessagePersistenceMapper messagePersistenceMapper() {
        return new MessagePersistenceMapper();
    }

    @Bean
    public MessageRepositoryPort messageRepositoryPort(
            MessageEntityRepository messageEntityRepository,
            PoolEntityRepository poolEntityRepository,
            UserEntityRepository userEntityRepository,
            MessagePersistenceMapper messagePersistenceMapper) {
        return new MessageJpaAdapter(messageEntityRepository, poolEntityRepository, userEntityRepository, messagePersistenceMapper);
    }

    @Bean
    public PhasePersistenceMapper phasePersistenceMapper() {
        return new PhasePersistenceMapper();
    }

    @Bean
    public PoolPersistenceMapper poolPersistenceMapper(
            WalletPersistenceMapper walletPersistenceMapper,
            InvitationPersistenceMapper invitationPersistenceMapper,
            PhasePersistenceMapper phasePersistenceMapper) {
        return new PoolPersistenceMapper(walletPersistenceMapper, invitationPersistenceMapper, phasePersistenceMapper);
    }

    @Bean
    public PoolRepositoryPort poolRepositoryPort(
            PoolEntityRepository poolEntityRepository,
            UserEntityRepository userEntityRepository,
            PoolPersistenceMapper poolPersistenceMapper) {
        return new PoolJpaAdapter(poolEntityRepository, userEntityRepository, poolPersistenceMapper);
    }

    @Bean
    public ContributionPersistenceMapper contributionPersistenceMapper() {
        return new ContributionPersistenceMapper();
    }

    @Bean
    public ContributionRepositoryPort contributionRepositoryPort(
            ContributionEntityRepository contributionEntityRepository,
            PoolEntityRepository poolEntityRepository,
            UserEntityRepository userEntityRepository,
            ContributionPersistenceMapper contributionPersistenceMapper) {
        return new ContributionJpaAdapter(
                contributionEntityRepository,
                poolEntityRepository,
                userEntityRepository,
                contributionPersistenceMapper
        );
    }

    @Bean
    public CreateContributionService createContributionService(
            ContributionRepositoryPort contributionRepositoryPort,
            PoolRepositoryPort poolRepositoryPort,
            WalletRepositoryPort walletRepositoryPort) {
        return new DefaultCreateContributionService(contributionRepositoryPort, poolRepositoryPort, walletRepositoryPort);
    }

    @Bean
    public ListContributionsService listContributionsService(ContributionRepositoryPort contributionRepositoryPort, PoolRepositoryPort poolRepositoryPort) {
        return new DefaultListContributionsService(contributionRepositoryPort, poolRepositoryPort);
    }

    @Bean
    public CreatePoolService createPoolService(PoolRepositoryPort poolRepositoryPort, WalletRepositoryPort walletRepositoryPort) {
        return new DefaultCreatePoolService(poolRepositoryPort, walletRepositoryPort);
    }

    @Bean
    public GetPoolService getPoolService(PoolRepositoryPort poolRepositoryPort) {
        return new DefaultGetPoolService(poolRepositoryPort);
    }

    @Bean
    public ListPoolsService listPoolsService(PoolRepositoryPort poolRepositoryPort) {
        return new DefaultListPoolsService(poolRepositoryPort);
    }

    @Bean
    public UpdatePoolService updatePoolService(PoolRepositoryPort poolRepositoryPort) {
        return new DefaultUpdatePoolService(poolRepositoryPort);
    }

    @Bean
    public MessageService messageService(MessageRepositoryPort messageRepositoryPort, ReactionRepositoryPort reactionRepositoryPort) {
        return new DefaultMessageService(messageRepositoryPort, reactionRepositoryPort);
    }

    @Bean
    public DeletePoolService deletePoolService(PoolRepositoryPort poolRepositoryPort) {
        return new DefaultDeletePoolService(poolRepositoryPort);
    }

    @Bean
    public UserCheckPort userCheckPort(UserEntityRepository userEntityRepository) {
        return new UserCheckAdapter(userEntityRepository);
    }

    @Bean
    public NotificationPort poolNotificationPort(JavaMailSender mailSender) {
        return new EmailNotificationAdapter(mailSender);
    }

    @Bean
    public InvitationService invitationService(
            InvitationRepositoryPort invitationRepositoryPort,
            UserCheckPort userCheckPort,
            NotificationPort poolNotificationPort,
            PoolRepositoryPort poolRepositoryPort) {
        return new DefaultInvitationService(invitationRepositoryPort, userCheckPort, poolNotificationPort, poolRepositoryPort);
    }
}
