package com.example.demo.service;

import com.example.demo.exception.UserNotFoundException;

import com.example.demo.model.Branch;
import com.example.demo.model.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;


@Service
public class GitHubAPIService {


    private String token = "";
    private final WebClient webClient;

    public GitHubAPIService(WebClient webClient) {
        System.out.println("token: " + token);
        this.webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    public Flux<Repository> getUserRepositories(String username, String acceptHeader) {
        return webClient
                .get()
                .uri("/users/{username}/repos", username)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()

                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse ->
                        Mono.error(() -> new UserNotFoundException(HttpStatus.NOT_FOUND.value(), "USER NOT FOUND")))

                .bodyToFlux(Repository.class)
                .filter(repository -> !repository.isFork())
                .flatMap(repository -> getBranchesOfRepository(username, repository.getName())
                        .map(branches -> {
                            repository.setBranches(branches);
                            return repository;
                        })
                        .defaultIfEmpty(repository));
    }

    public Mono<List<Branch>> getBranchesOfRepository(String username, String repository) {
        Mono<List<Branch>> branches = webClient.get()
                .uri("/repos/{username}/{repository}/branches", username, repository)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Branch.class)
                .collectList();
        return branches;
    }

}