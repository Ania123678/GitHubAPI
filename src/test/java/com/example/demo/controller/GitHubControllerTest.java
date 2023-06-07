package com.example.demo.controller;

import com.example.demo.model.Repository;
import com.example.demo.service.GitHubAPIService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(new GitHubController(gitHubAPIService))
                .configureClient()
                .build();
    }

    @Test
    void testGetUserRepositoriesJSON() {

        String username = "TestUser12";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        Repository repository1 = new Repository();
        repository1.setName("repo1");
        repository1.setFork(false);

        Repository repository2 = new Repository();
        repository2.setName("repo2");
        repository2.setFork(true);

        Flux<Repository> mockResponse = Flux.just(repository1, repository2);

        when(gitHubAPIService.getUserRepositories(username, acceptHeader)).thenReturn(mockResponse);

        //TODO: webTestClient assert?
        webTestClient.get()
                .uri("/apiv1/repositories/{username}", username)
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Repository.class)
                .value(repositories -> {
                    Assertions.assertEquals(2, repositories.size());
                });
    }

}
