package com.example.demo.service;

import com.example.demo.exception.MediaTypeNotAcceptableException;
import com.example.demo.exception.UserNotFoundException;

import com.example.demo.filter.XmlHeaderCheckFilter;
import com.example.demo.model.Branch;
import com.example.demo.model.Repository;

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

    //TODO: Value{$""}
    private String token = "";
    private final WebClient webClient;

    public GitHubAPIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .filter(new XmlHeaderCheckFilter())
                .build();
    }

    public Flux<Repository> getUserRepositories(String username, String acceptHeader) {
        if (acceptHeader.equals(MediaType.APPLICATION_XML_VALUE)) {
            throw new MediaTypeNotAcceptableException(HttpStatus.NOT_ACCEPTABLE.value(), "Not Acceptable");
        }
        Flux<Repository> repos = webClient
                .get()
                .uri("/users/{username}/repos", username)
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
        return  repos;
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