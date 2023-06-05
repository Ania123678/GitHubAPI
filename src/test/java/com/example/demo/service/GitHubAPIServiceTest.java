package com.example.demo.service;


import com.example.demo.model.Repository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GitHubControllerTest {

    private MockWebServer mockWebServer;
    private WebClient webClient;
    private GitHubAPIService gitHubAPIService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        webClient = WebClient.create(mockWebServer.url("/").toString());
        gitHubAPIService = new GitHubAPIService(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetUserRepositories() throws InterruptedException {

        String username = "testuser";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        String repositoriesResponseBody = "[{\"name\": \"repo1\", \"fork\": false}, {\"name\": \"repo2\", \"fork\": true}]";
        MockResponse repositoriesResponse = new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(repositoriesResponseBody);
        mockWebServer.enqueue(repositoriesResponse);

        Flux<Repository> repositories = gitHubAPIService.getUserRepositories(username, acceptHeader);

        StepVerifier.create(repositories)
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof WebClientResponseException);
                    WebClientResponseException responseException = (WebClientResponseException) throwable;
                    assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatusCode());


                    String responseBody = responseException.getResponseBodyAsString();
                    assertNotNull(responseBody);
                    assertTrue(responseBody.contains("Unauthorized"));
                    assertTrue(responseBody.contains("Access denied"));
                });
    }
}
