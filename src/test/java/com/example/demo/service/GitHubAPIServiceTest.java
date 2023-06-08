package com.example.demo.service;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Branch;
import com.example.demo.model.Owner;
import com.example.demo.model.Repository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest
class GitHubAPIServiceTest {

    private WireMockServer wireMockServer;

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    private static GitHubAPIService gitHubAPIService;

    @Value("${github.token}")
    private String token;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());

        gitHubAPIService = new GitHubAPIService(token, WebClient.builder()
                .baseUrl(String.format("http://localhost:%s", wireMockServer.port()))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        );
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testGetUserRepositories() {

        Owner owner = new Owner();
        owner.setLogin("usertest");

        Repository expectedRepo1 = new Repository();
        expectedRepo1.setName("repo1");
        expectedRepo1.setOwner(owner);
        expectedRepo1.setFork(false);


        Repository expectedRepo2 = new Repository();
        expectedRepo2.setName("repo2");
        expectedRepo2.setOwner(owner);
        expectedRepo2.setFork(true);


        stubFor(get(urlEqualTo("/users/usertest/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[{\"name\": \"repo1\", \"owner\": {\"login\": \"usertest\"}, \"fork\": false, \"branches\": []}, {\"name\": \"repo2\", \"owner\": {\"login\": \"usertest\"}, \"fork\": true, \"branches\": []}]")));

        Flux<Repository> repos = gitHubAPIService.getUserRepositories("usertest", MediaType.APPLICATION_JSON_VALUE);

        StepVerifier.create(repos)
                .expectNext(expectedRepo1)
                .expectNext(expectedRepo2)
                .verifyComplete();
    }


    @Test
    void testGetBranchesOfRepository() {

        String username = "usertest";
        String repository = "repo1";

        Branch branch1 = new Branch();
        branch1.setName("branch1");

        Branch branch2 = new Branch();
        branch2.setName("branch2");

        List<Branch> expectedBranches = List.of(branch1, branch2);

        stubFor(get(urlEqualTo("/repos/usertest/repo1/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[{\"name\": \"branch1\"}, {\"name\": \"branch2\"}]")));

        Mono<List<Branch>> branches = gitHubAPIService.getBranchesOfRepository(username, repository);

        StepVerifier.create(branches)
                .expectNext(expectedBranches)
                .verifyComplete();
    }

    @Test
    void testGetUserRepositories_NotAcceptable() {
        stubFor(get(urlEqualTo("/users/usertest/repos"))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_XML_VALUE))
                .willReturn(aResponse()
                        .withStatus(406)));

        Flux<Repository> repos = gitHubAPIService.getUserRepositories("usertest", MediaType.APPLICATION_XML_VALUE);

        StepVerifier.create(repos)
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException &&
                        ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_ACCEPTABLE)
                .verify();
    }

    @Test
    void testGetUserRepositories_NotFound() {
        stubFor(get(urlEqualTo("/users/usertest/repos"))
                .willReturn(aResponse()
                        .withStatus(404)));

        Flux<Repository> repos = gitHubAPIService.getUserRepositories("usertest", MediaType.APPLICATION_JSON_VALUE);

        StepVerifier.create(repos)
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException &&
                        ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }
}
