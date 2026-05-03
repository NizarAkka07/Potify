package com.alphateckplus.potify.pool.infrastructure.config;

import com.alphateckplus.potify.pool.application_service.primary.pool.create_pool.CreatePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.create_pool.DefaultCreatePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.delete_pool.DefaultDeletePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.delete_pool.DeletePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.DefaultGetPoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.get_pool.GetPoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.list_pools.DefaultListPoolsService;
import com.alphateckplus.potify.pool.application_service.primary.pool.list_pools.ListPoolsService;
import com.alphateckplus.potify.pool.application_service.primary.pool.update_pool.DefaultUpdatePoolService;
import com.alphateckplus.potify.pool.application_service.primary.pool.update_pool.UpdatePoolService;
import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolRepositoryPort;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.PoolJpaAdapter;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.PoolPersistenceMapper;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration explicite des beans du microservice pool.
 *
 * <p>Centralise le wiring Spring pour garder les classes applicatives pures.
 */
@Configuration
public class PoolBeanConfiguration {

    @Bean
    public PoolPersistenceMapper poolPersistenceMapper() {
        return new PoolPersistenceMapper();
    }

    @Bean
    public PoolRepositoryPort poolRepositoryPort(
            PoolEntityRepository poolEntityRepository,
            UserEntityRepository userEntityRepository,
            PoolPersistenceMapper poolPersistenceMapper) {
        return new PoolJpaAdapter(poolEntityRepository, userEntityRepository, poolPersistenceMapper);
    }

    @Bean
    public CreatePoolService createPoolService(PoolRepositoryPort poolRepositoryPort) {
        return new DefaultCreatePoolService(poolRepositoryPort);
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
    public DeletePoolService deletePoolService(PoolRepositoryPort poolRepositoryPort) {
        return new DefaultDeletePoolService(poolRepositoryPort);
    }
}
