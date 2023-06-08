package com.example.demo.controller;


import com.example.demo.model.Repository;
import com.example.demo.service.GitHubAPIService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import static org.mockito.Mockito.*;

@SpringBootTest
class GitHubControllerTest {

    private WireMockServer wireMockServer;
    private WebTestClient webTestClient;

    private GitHubAPIService gitHubAPIService;

    @Value("${github.token}")
    private String token;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        webTestClient = WebTestClient.bindToController(new GitHubController(new GitHubAPIService(token, WebClient.builder())))
                .configureClient()
                .baseUrl("http://localhost:" + wireMockServer.port())
                .build();

        gitHubAPIService = new GitHubAPIService(token, webTestClient);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testGetRepositories() {
        String username = "usertest";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        when(gitHubAPIService.getUserRepositories(username, acceptHeader))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/apiv1/repositories/{username}", username)
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Repository.class)
                .hasSize(0);
    }

    @Test
    void testGetRepositories_NotFound() {
        String username = "usertest";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        wireMockServer.stubFor(get("/users/" + username + "/repos")
                .withHeader(HttpHeaders.ACCEPT, equalTo(acceptHeader))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));

        webTestClient.get()
                .uri("/repositories/{username}", username)
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetRepositories_NotAcceptable() {
        String username = "usertest";
        String acceptHeader = MediaType.APPLICATION_XML_VALUE;

        wireMockServer.stubFor(get("/users/" + username + "/repos")
                .withHeader(HttpHeaders.ACCEPT, equalTo(acceptHeader))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_ACCEPTABLE.value())));

        webTestClient.get()
                .uri("/repositories/{username}", username)
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }
}
