import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Branch;
import com.example.demo.model.Repository;
import com.example.demo.service.GitHubAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class GitHubAPIServiceTest {

    @Mock
    private WebClient webClient;

    private GitHubAPIService gitHubAPIService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        gitHubAPIService = new GitHubAPIService(webClient);
    }

    @Test
    public void testGetUserRepositories() {
        String username = "testuser";
        String acceptHeader = "application/json";

        // Mock response from webClient
        Repository repository1 = new Repository("repo1", false);
        Repository repository2 = new Repository("repo2", false);
        List<Repository> repositories = Arrays.asList(repository1, repository2);
        when(webClient.get()).thenReturn(webClient);
        when(webClient.uri(eq("/users/{username}/repos"), eq(username))).thenReturn(webClient);
        when(webClient.accept(any(MediaType.class))).thenReturn(webClient);
        when(webClient.header(eq(HttpHeaders.ACCEPT), eq(MediaType.APPLICATION_JSON_VALUE))).thenReturn(webClient);
        when(webClient.retrieve()).thenReturn(webClient);
        when(webClient.onStatus(eq(HttpStatus.NOT_FOUND::equals), any())).thenReturn(webClient);
        when(webClient.bodyToFlux(Repository.class)).thenReturn(Flux.fromIterable(repositories));

        // Mock getBranchesOfRepository
        Branch branch1 = new Branch("branch1");
        Branch branch2 = new Branch("branch2");
        List<Branch> branches = Arrays.asList(branch1, branch2);
        when(gitHubAPIService.getBranchesOfRepository(eq(username), eq("repo1"))).thenReturn(Mono.just(branches));
        when(gitHubAPIService.getBranchesOfRepository(eq(username), eq("repo2"))).thenReturn(Mono.empty());

        Flux<Repository> result = gitHubAPIService.getUserRepositories(username, acceptHeader);

        StepVerifier.create(result)
                .expectNext(repository1.setBranches(branches))
                .expectNext(repository2)
                .expectComplete()
                .verify();
    }

    @Test
    public void testGetUserRepositories_UserNotFound() {
        String username = "nonexistentuser";
        String acceptHeader = "application/json";

        when(webClient.get()).thenReturn(webClient);
        when(webClient.uri(eq("/users/{username}/repos"), eq(username))).thenReturn(webClient);
        when(webClient.accept(any(MediaType.class))).thenReturn(webClient);
        when(webClient.header(eq(HttpHeaders.ACCEPT), eq(MediaType.APPLICATION_JSON_VALUE))).thenReturn(webClient);
        when(webClient.retrieve()).thenReturn(webClient);
        when(webClient.onStatus(eq(HttpStatus.NOT_FOUND::equals), any())).thenReturn(webClient);
        when(webClient.bodyToFlux(Repository.class)).thenReturn(Flux.empty());

        Flux<Repository> result = gitHubAPIService.getUserRepositories(username, acceptHeader);

        StepVerifier.create(result)
                .expectError(UserNotFoundException.class)
                .verify();
    }