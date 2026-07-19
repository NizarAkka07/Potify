package com.alphateckplus.potify.pool.infrastructure.config;

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
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.CagnotteWalletEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.notification.EmailNotificationAdapter;
import com.alphateckplus.potify.pool.application_service.secondary.pool.UserCheckPort;
import com.alphateckplus.potify.pool.application_service.primary.pool.workflow.*;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.UserCheckAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolGenerationGatewayPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.ai.MistralPoolGenerationAdapter;
import com.alphateckplus.potify.pool.application_service.primary.pool.generate_pool_structure.GeneratePoolStructureService;
import com.alphateckplus.potify.pool.application_service.primary.pool.generate_pool_structure.DefaultGeneratePoolStructureService;
import com.alphateckplus.potify.pool.application_service.primary.pool.update_timeline.PoolUpdateService;
import com.alphateckplus.potify.pool.application_service.primary.pool.update_timeline.DefaultPoolUpdateService;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolUpdateRepositoryPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.PoolUpdatePersistenceMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository.PoolUpdateJpaAdapter;

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
            com.alphateckplus.potify.data_jpa.repository.pool.MessageReportEntityRepository messageReportEntityRepository,
            MessagePersistenceMapper messagePersistenceMapper) {
        return new MessageJpaAdapter(messageEntityRepository, poolEntityRepository, userEntityRepository, messageReportEntityRepository, messagePersistenceMapper);
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
            PoolPersistenceMapper poolPersistenceMapper,
            com.alphateckplus.potify.data_jpa.repository.pool.PoolReportEntityRepository poolReportEntityRepository) {
        return new PoolJpaAdapter(poolEntityRepository, userEntityRepository, poolPersistenceMapper, poolReportEntityRepository);
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
    public UpdatePoolService updatePoolService(PoolRepositoryPort poolRepositoryPort, UserCheckPort userCheckPort) {
        return new DefaultUpdatePoolService(poolRepositoryPort, userCheckPort);
    }

    @Bean
    public MessageService messageService(MessageRepositoryPort messageRepositoryPort, 
                                         ReactionRepositoryPort reactionRepositoryPort,
                                         PoolRepositoryPort poolRepositoryPort,
                                         com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultMessageService(messageRepositoryPort, reactionRepositoryPort, poolRepositoryPort, notificationEventPublisherPort);
    }

    @Bean
    public DeletePoolService deletePoolService(PoolRepositoryPort poolRepositoryPort) {
        return new DefaultDeletePoolService(poolRepositoryPort);
    }

    @Bean
    public com.alphateckplus.potify.pool.application_service.primary.pool.report_pool.ReportPoolService reportPoolService(PoolRepositoryPort poolRepositoryPort) {
        return new com.alphateckplus.potify.pool.application_service.primary.pool.report_pool.DefaultReportPoolService(poolRepositoryPort);
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
            PoolRepositoryPort poolRepositoryPort,
            com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultInvitationService(invitationRepositoryPort, userCheckPort, poolNotificationPort, poolRepositoryPort, notificationEventPublisherPort);
    }

    @Bean
    public PoolGenerationGatewayPort poolGenerationGatewayPort() {
        return new MistralPoolGenerationAdapter();
    }

    @Bean
    public GeneratePoolStructureService generatePoolStructureService(
            PoolGenerationGatewayPort poolGenerationGatewayPort) {
        return new DefaultGeneratePoolStructureService(poolGenerationGatewayPort);
    }

    @Bean
    public com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort(
            org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate) {
        return new com.alphateckplus.potify.pool.infrastructure.secondary.notification.KafkaNotificationEventPublisherAdapter(kafkaTemplate);
    }

    @Bean
    public PublishPoolService publishPoolService(
            PoolRepositoryPort poolRepositoryPort,
            UserCheckPort userCheckPort,
            com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultPublishPoolService(poolRepositoryPort, userCheckPort, notificationEventPublisherPort);
    }

    @Bean
    public ApprovePoolService approvePoolService(
            PoolRepositoryPort poolRepositoryPort,
            WalletRepositoryPort walletRepositoryPort,
            com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultApprovePoolService(poolRepositoryPort, walletRepositoryPort, notificationEventPublisherPort);
    }

    @Bean
    public RejectPoolService rejectPoolService(
            PoolRepositoryPort poolRepositoryPort,
            com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultRejectPoolService(poolRepositoryPort, notificationEventPublisherPort);
    }

    @Bean
    public SuspendPoolService suspendPoolService(
            PoolRepositoryPort poolRepositoryPort,
            com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultSuspendPoolService(poolRepositoryPort, notificationEventPublisherPort);
    }

    @Bean
    public ClosePoolService closePoolService(
            PoolRepositoryPort poolRepositoryPort,
            UserCheckPort userCheckPort,
            com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultClosePoolService(poolRepositoryPort, userCheckPort, notificationEventPublisherPort);
    }

    @Bean
    public PoolUpdatePersistenceMapper poolUpdatePersistenceMapper() {
        return new PoolUpdatePersistenceMapper();
    }

    @Bean
    public PoolUpdateRepositoryPort poolUpdateRepositoryPort(
            com.alphateckplus.potify.data_jpa.repository.pool.PoolUpdateEntityRepository poolUpdateEntityRepository,
            PoolEntityRepository poolEntityRepository,
            PoolUpdatePersistenceMapper poolUpdatePersistenceMapper,
            com.alphateckplus.potify.data_jpa.repository.payment.ContributionEntityRepository contributionEntityRepository) {
        return new PoolUpdateJpaAdapter(poolUpdateEntityRepository, poolEntityRepository, poolUpdatePersistenceMapper, contributionEntityRepository);
    }

    @Bean
    public PoolUpdateService poolUpdateService(
            PoolUpdateRepositoryPort poolUpdateRepositoryPort,
            PoolRepositoryPort poolRepositoryPort,
            com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort notificationEventPublisherPort) {
        return new DefaultPoolUpdateService(poolUpdateRepositoryPort, poolRepositoryPort, notificationEventPublisherPort);
    }
}
