package com.example.demo.service;

import com.example.demo.model.Branch;
import com.example.demo.model.Repository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GitHubAPIServiceTest {

    private WireMockServer wireMockServer;

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testGetUserRepositories() {

        String username = "testuser";
        String repositoriesResponseBody = "[{\"name\": \"repo1\", \"fork\": false}, {\"name\": \"repo2\", \"fork\": true}]";
        stubFor(get(urlEqualTo("/users/testuser/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[{\"name\": \"repo1\", \"fork\": false}, {\"name\": \"repo2\", \"fork\": true}]")));
        GitHubAPIService gitHubAPIService = new GitHubAPIService(webTestClient);
        Flux<Repository> repositories = gitHubAPIService.getUserRepositories(username, MediaType.APPLICATION_JSON_VALUE);
        repositories.as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testGetBranchesOfRepository() {
        String username = "testuser";
        String repository = "repo1";
        String branchesResponseBody = "[{\"name\": \"branch1\"}, {\"name\": \"branch2\"}]";
        stubFor(get(urlEqualTo("/users/testuser/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[{\"name\": \"repo1\", \"fork\": false}, {\"name\": \"repo2\", \"fork\": true}]")));
        GitHubAPIService gitHubAPIService = new GitHubAPIService(webTestClient);
        Mono<List<Branch>> branches = gitHubAPIService.getBranchesOfRepository(username, repository);
        branches.as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }
}
