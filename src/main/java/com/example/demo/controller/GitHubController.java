package com.example.demo.controller;

import com.example.demo.exception.ErrorJsonResponse;
import com.example.demo.exception.MediaTypeNotAcceptableException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Repository;
import com.example.demo.service.GitHubAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apiv1")
public class GitHubController {

    private final GitHubAPIService gitHubAPIService;

    public GitHubController(GitHubAPIService gitHubAPIService) {
        this.gitHubAPIService = gitHubAPIService;
    }

    //TODO: jedna linijka, if wyrzucić []

    @GetMapping(value = "/repositories/{username}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Flux<Repository> getUserRepositories(@PathVariable String username, @RequestHeader("Accept") String acceptHeader) {
        return gitHubAPIService.getUserRepositories(username, acceptHeader);
    }
}

