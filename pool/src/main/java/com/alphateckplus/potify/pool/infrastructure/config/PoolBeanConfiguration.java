package com.alphateckplus.potify.pool.infrastructure.config;

import com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution.CreateContributionService;
import com.alphateckplus.potify.pool.application_service.primary.contribution.create_contribution.DefaultCreateContributionService;
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
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.repository.PoolJpaAdapter;
import com.alphateckplus.potify.pool.infrastructure.secondary.pool.mapper.PoolPersistenceMapper;
import com.alphateckplus.potify.pool.infrastructure.secondary.contribution.repository.ContributionJpaAdapter;
import com.alphateckplus.potify.pool.infrastructure.secondary.contribution.mapper.ContributionPersistenceMapper;
import com.alphateckplus.potify.pool.application_service.secondary.contribution.ContributionRepositoryPort;
import com.alphateckplus.potify.data_jpa.repository.pool.PoolEntityRepository;
import com.alphateckplus.potify.data_jpa.repository.payment.ContributionEntityRepository;
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
            PoolRepositoryPort poolRepositoryPort) {
        return new DefaultCreateContributionService(contributionRepositoryPort, poolRepositoryPort);
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
