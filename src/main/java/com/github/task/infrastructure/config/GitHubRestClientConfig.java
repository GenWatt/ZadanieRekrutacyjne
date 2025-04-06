package com.github.task.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.github.task.domain.Constants;

@Configuration
public class GitHubRestClientConfig {

    @Bean
    @Qualifier(Constants.GITHUB_CLIENT_QUALIFIER)
    RestClient githubRestClient() {
        return RestClient.builder()
                .baseUrl(Constants.GITHUB_API_URL)
                .build();
    }
}