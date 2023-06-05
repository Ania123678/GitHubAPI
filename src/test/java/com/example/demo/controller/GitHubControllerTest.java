package com.example.demo.controller;


import com.example.demo.exception.MediaTypeNotAcceptableException;
import com.example.demo.model.Repository;
import com.example.demo.service.GitHubAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import reactor.core.publisher.Flux;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GitHubControllerTest {

    @Mock
    private GitHubAPIService gitHubAPIService;

    private GitHubController gitHubController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        gitHubController = new GitHubController(gitHubAPIService);
    }

    @Test
    void testGetUserRepositoriesJSON() {
        String username = "TestUser12";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        Flux<Repository> repositories = Flux.empty();

        when(gitHubAPIService.getUserRepositories(username, acceptHeader))
                .thenReturn(repositories);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.ACCEPT, acceptHeader);

        Flux<Repository> actualRepositories = gitHubController.getUserRepositories(username, acceptHeader);

        verify(gitHubAPIService).getUserRepositories(username, acceptHeader);

        assertNotNull(actualRepositories);
        assertEquals(repositories, actualRepositories);
    }

    @Test
    void testGetUserRepositoriesXML() {
        String username = "TestUser12";
        String acceptHeader = MediaType.APPLICATION_XML_VALUE;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.ACCEPT, acceptHeader);


        MediaTypeNotAcceptableException exception = assertThrows(
                MediaTypeNotAcceptableException.class,
                () -> gitHubController.getUserRepositories(username, acceptHeader)
        );

        assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), exception.getStatus());
        assertEquals("Not Acceptable", exception.getMessage());

        verifyNoMoreInteractions(gitHubAPIService);
    }
}
