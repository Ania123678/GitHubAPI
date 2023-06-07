package com.example.demo.controller;

import com.example.demo.model.Repository;
import com.example.demo.service.GitHubAPIService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import static org.mockito.Mockito.*;

@SpringBootTest
class GitHubControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private GitHubAPIService gitHubAPIService;

    @Test
    public void testGetRepositories() {
        String username = "usertest";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        when(gitHubAPIService.getUserRepositories(username, acceptHeader))
                .thenReturn(Flux.empty());

        WebTestClient webTestClient = WebTestClient.bindToController(new GitHubController(gitHubAPIService)).build();

        webTestClient.get()
                .uri("/apiv1/repositories/{username}", username)
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Repository.class)
                .hasSize(0);
    }
}
