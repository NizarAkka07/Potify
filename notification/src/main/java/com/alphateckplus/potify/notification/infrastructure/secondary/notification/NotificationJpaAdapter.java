package com.alphateckplus.potify.notification.infrastructure.secondary.notification;

import com.alphateckplus.potify.data_jpa.entity.notification.NotificationChannel;
import com.alphateckplus.potify.data_jpa.entity.notification.NotificationEntity;
import com.alphateckplus.potify.data_jpa.entity.notification.NotificationStatus;
import com.alphateckplus.potify.data_jpa.entity.notification.NotificationType;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.notification.NotificationEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.notification.application_service.secondary.notification.NotificationRepositoryPort;
import com.alphateckplus.potify.notification.domain.model.Notification;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationJpaAdapter implements NotificationRepositoryPort {

    private final NotificationEntityRepository notificationRepository;
    private final UserEntityRepository userRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = mapToEntity(notification);
        NotificationEntity saved = notificationRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Notification> findById(String id) {
        return notificationRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Notification> findByUserId(String userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private Notification mapToDomain(NotificationEntity entity) {
        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .type(entity.getType().name())
                .channel(entity.getChannel().name())
                .title(entity.getTitle())
                .content(entity.getContent())
                .status(entity.getStatus().name())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private NotificationEntity mapToEntity(Notification domain) {
        UserEntity user = userRepository.findById(domain.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User introuvable: " + domain.getUserId()));

        NotificationStatus status = NotificationStatus.valueOf(domain.getStatus() != null ? domain.getStatus() : "ACTIVE");
        NotificationType type = NotificationType.valueOf(domain.getType());
        NotificationChannel channel = NotificationChannel.valueOf(domain.getChannel() != null ? domain.getChannel() : "NOTIF_APP");

        return NotificationEntity.builder()
                .id(domain.getId())
                .user(user)
                .type(type)
                .channel(channel)
                .title(domain.getTitle())
                .content(domain.getContent())
                .status(status)
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
