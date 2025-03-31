package com.github.task.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.github.task.domain.Constants;

@Configuration
public class GitHubRestTemplateConfig {

    @Bean
    @Qualifier("githubRestTemplate")
    RestTemplate githubRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(Constants.GITHUB_API_URL));

        return restTemplate;
    }
}