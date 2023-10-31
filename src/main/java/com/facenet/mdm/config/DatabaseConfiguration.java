package com.facenet.mdm.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories({ "com.facenet.mdm.repository" })
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

    private final JpaProperties jpaProperties;

    @Autowired
    public DatabaseConfiguration(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
        DataSource dataSource,
        MultiTenantConnectionProvider multiTenantConnectionProvider,
        CurrentTenantIdentifierResolver tenantIdentifierResolver
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.facenet.mdm.domain");
        em.setJpaVendorAdapter(this.jpaVendorAdapter());
        Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
        jpaPropertiesMap.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        jpaPropertiesMap.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        jpaPropertiesMap.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        em.setJpaPropertyMap(jpaPropertiesMap);
        return em;
    }
}
