package com.alphateckplus.potify.notification.infrastructure.config;

import com.alphateckplus.potify.data_jpa.repository.notification.NotificationEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.notification.application_service.primary.notification.CreateNotificationService;
import com.alphateckplus.potify.notification.application_service.primary.notification.DefaultCreateNotificationService;
import com.alphateckplus.potify.notification.application_service.primary.notification.DefaultGetNotificationsService;
import com.alphateckplus.potify.notification.application_service.primary.notification.DefaultMarkNotificationReadService;
import com.alphateckplus.potify.notification.application_service.primary.notification.GetNotificationsService;
import com.alphateckplus.potify.notification.application_service.primary.notification.MarkNotificationReadService;
import com.alphateckplus.potify.notification.application_service.secondary.notification.NotificationRepositoryPort;
import com.alphateckplus.potify.notification.infrastructure.secondary.notification.NotificationJpaAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationBeanConfiguration {

    @Bean
    public NotificationRepositoryPort notificationRepositoryPort(
            NotificationEntityRepository notificationRepository,
            UserEntityRepository userRepository) {
        return new NotificationJpaAdapter(notificationRepository, userRepository);
    }

    @Bean
    public CreateNotificationService createNotificationService(NotificationRepositoryPort repositoryPort) {
        return new DefaultCreateNotificationService(repositoryPort);
    }

    @Bean
    public GetNotificationsService getNotificationsService(NotificationRepositoryPort repositoryPort) {
        return new DefaultGetNotificationsService(repositoryPort);
    }

    @Bean
    public MarkNotificationReadService markNotificationReadService(NotificationRepositoryPort repositoryPort) {
        return new DefaultMarkNotificationReadService(repositoryPort);
    }
}
