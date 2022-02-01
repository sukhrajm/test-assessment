package co.copper.test.services;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import co.copper.test.datamodel.User;
import co.copper.test.response.UserCollection;
import co.copper.test.response.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.copper.test.storage.TestJavaRepository;

import static java.util.stream.Collectors.toList;

@Service
public class TestJavaService {

    private static final Logger log = LoggerFactory.getLogger(TestJavaService.class);
    private final TestJavaRepository testRepo;
    private final AsyncHttpClient httpClient;

    @Autowired
    public TestJavaService(TestJavaRepository testRepo, AsyncHttpClient httpClient) {
        this.testRepo = testRepo;
        this.httpClient = httpClient;
    }

    public CompletableFuture<String> getOk() {
        log.debug(testRepo.getById(1L).get(0).getVal());
        return this.httpClient.prepareGet("https://postman-echo.com/get").execute().toCompletableFuture()
            .handle((res, t) -> res.getResponseBody());
    }

    public CompletableFuture<List<User>> getUsers() {
        return this.httpClient.prepareGet("https://randomuser.me/api?results=20")
            .execute()
            .toCompletableFuture()
            .handle((response, t) -> insertIntoDatabase(response.getResponseBody()));
    }

    private List<User> insertIntoDatabase(String body) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserCollection userCollection = null;
        List<User> usersAddedToDb = null;
        try {
            userCollection = objectMapper.readValue(body, UserCollection.class);

            usersAddedToDb = userCollection.getResults()
                .stream()
                .map(userResponse -> testRepo.insert(create(userResponse)))
                .flatMap(Collection::stream)
                .collect(toList());

            if (usersAddedToDb.size() != userCollection.getResults().size()) {
                log.error("not all users were added to database");
            } else {
                log.info("all users successfully added to database");
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return usersAddedToDb;
    }

    /**
     * Create a User object from a UserResponse object
     */
    private User create(UserResponse userResponse) {

        return new User(userResponse.getName().getFirst(),
            userResponse.getName().getLast(),
            userResponse.getEmail(),
            userResponse.getLogin().getPassword());

    }
}
