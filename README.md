# GithubAPI
This project contains a GitHub API for retrieving data about a user's repositories and associated tests.

## Project Setup:

1. Open it in your preferred IDE.
2. Select SDK as Java 17

## Testing the API:
1. Store the generated API token from GitHub in the token variable of the GitHubService class for authorization purposes
2. Run DemoApplication.main().
3. In your preferred API tool, create a GET request with the following URL: http://localhost:8080/apiv1/repositories/{username}.
4. In the username field, enter the GitHub username.
5. Add the header: Accept: application/json.

## Additional Information:
- Project requires Java 17
- The API does not return data in XML format. To test the error handler, add the header: Accept: application/xml.
- The API handles a 404 error by returning an error message in JSON format.
