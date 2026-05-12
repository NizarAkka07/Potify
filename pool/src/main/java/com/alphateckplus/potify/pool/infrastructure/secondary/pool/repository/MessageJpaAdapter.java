package com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository;

import com.alphateckplus.potify.data_jpa.entity.pool.MessageEntity;
import com.alphateckplus.potify.data_jpa.entity.pool.PoolEntity;
import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.pool.MessageEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.pool.application_service.secondary.pool.MessageRepositoryPort;
import com.alphateckplus.potify.pool.domain.model.Message;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.MessagePersistenceMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class MessageJpaAdapter implements MessageRepositoryPort {

    private final MessageEntityRepository messageEntityRepository;
    private final PoolEntityRepository poolEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final MessagePersistenceMapper mapper;

    @Override
    public Message save(Message message) {
        MessageEntity entity = mapper.toEntity(message);
        
        PoolEntity pool = poolEntityRepository.findById(message.getPoolId())
                .orElseThrow(() -> new IllegalArgumentException("Cagnotte non trouvee"));
        UserEntity user = userEntityRepository.findById(message.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouve"));
        
        entity.setPool(pool);
        entity.setUser(user);
        
        return mapper.toDomain(messageEntityRepository.save(entity));
    }

    @Override
    public List<Message> findByPoolId(String poolId) {
        return messageEntityRepository.findByPoolIdOrderByCreatedAtDesc(poolId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        messageEntityRepository.deleteById(id);
    }

    @Override
    public java.util.Optional<Message> findById(String id) {
        return messageEntityRepository.findById(id).map(mapper::toDomain);
    }
}
