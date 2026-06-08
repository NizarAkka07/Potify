package com.alphateckplus.potify.pool.application_service.primary.pool.message;

import com.alphateckplus.potify.pool.application_service.secondary.notification.NotificationEventPublisherPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.MessageRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.application_service.secondary.pool.ReactionRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Message;
import com.alphateckplus.potify.pool.domain.model.Pool;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DefaultMessageService implements MessageService {

    private final MessageRepositoryPort messageRepositoryPort;
    private final ReactionRepositoryPort reactionRepositoryPort;
    private final PoolRepositoryPort poolRepositoryPort;
    private final NotificationEventPublisherPort notificationEventPublisherPort;

    @Override
    public Message addMessage(Message message) {
        Message saved = messageRepositoryPort.save(message);
        try {
            Pool pool = poolRepositoryPort.findById(saved.getPoolId())
                    .orElseThrow(() -> new RuntimeException("Cagnotte introuvable"));
            
            // Notify pool owner (only if they are not the message author)
            if (!pool.getOwnerId().equals(saved.getUserId())) {
                String title = "Nouveau message sur votre cagnotte !";
                String content = saved.getUserName() + " a écrit : \"" + saved.getContent() + "\"";
                notificationEventPublisherPort.publish(pool.getOwnerId(), "MESSAGE", title, content);
            }
        } catch (Exception e) {
            log.warn("Impossible d'envoyer la notification de message", e);
        }
        return saved;
    }

    @Override
    public List<Message> getPoolMessages(String poolId) {
        return messageRepositoryPort.findByPoolId(poolId);
    }

    @Override
    public void toggleReaction(String messageId, String userId, String type) {
        log.info("toggleReaction called: messageId={}, userId={}, type={}", messageId, userId, type);
        boolean added = reactionRepositoryPort.addOrRemoveReaction(messageId, userId, type);
        if (added) {
            try {
                Message msg = messageRepositoryPort.findById(messageId)
                        .orElseThrow(() -> new RuntimeException("Message introuvable"));
                
                // Do not notify if the reactor is the message author
                if (!msg.getUserId().equals(userId)) {
                    Pool pool = poolRepositoryPort.findById(msg.getPoolId())
                            .orElseThrow(() -> new RuntimeException("Cagnotte introuvable"));
                    
                    String title = "Nouvelle réaction !";
                    String content = "Quelqu'un a réagi à votre message sur la cagnotte '" + pool.getTitle() + "'.";
                    notificationEventPublisherPort.publish(msg.getUserId(), "REACTION", title, content);
                    log.info("Reaction notification event published successfully for user {}", msg.getUserId());
                } else {
                    log.info("Reactor is the message author. Skipping notification.");
                }
            } catch (Exception e) {
                log.warn("Impossible d'envoyer la notification de réaction", e);
            }
        }
    }

    @Override
    public Message getMessage(String messageId) {
        return messageRepositoryPort.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));
    }
}
