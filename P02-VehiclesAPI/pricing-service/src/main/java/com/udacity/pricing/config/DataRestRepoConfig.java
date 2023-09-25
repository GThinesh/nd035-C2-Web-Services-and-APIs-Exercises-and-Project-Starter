package com.udacity.pricing.config;

import com.udacity.pricing.domain.price.Price;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class DataRestRepoConfig implements RepositoryRestConfigurer {
    //https://stackoverflow.com/questions/30912826/expose-all-ids-when-using-spring-data-rest hard coded since only one entity
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Price.class);
    }
}
