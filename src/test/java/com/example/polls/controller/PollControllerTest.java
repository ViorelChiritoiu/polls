package com.example.polls.controller;

import com.example.polls.payload.ChoiceRequest;
import com.example.polls.payload.PollRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PollControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getPollsTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity(createUrlWithPort("/api/polls"), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getPollsTestBody() throws IOException {
        URL url = new URL(createUrlWithPort("/api/polls"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String readLine;
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuffer response = new StringBuffer();
                while((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
                System.out.println("JSON String Result " + response.toString());
            }
        } else {
            System.out.println("GET not worked");
        }
    }

    private String createUrlWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    public void createPollTest() throws IOException {
        final String POST_PARAMS = "{\n" + "    \"question\": \"Question3\",\r\n" +
                "   \"choices\": [\r\n" +
                "       {\r\n" +
                "           \"text\": \"text1\"\r\n" +
                "   },\r\n" +
                        "{\r\n" +
                "           \"text\": \"text2\"\r\n" +
                "}]," +
                "\"pollLength\": {\r\n" +
                "   \"days\": 3,\r\n" +
                "   \"hours\": 5\r\n" +
                "}\r\n" +
                "}";
        System.out.println(POST_PARAMS);
        URL url = new URL(createUrlWithPort("/api/polls/create"));
        HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");
        postConnection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTY5MjQyMjkxLCJleHAiOjE1Njk4NDcwOTF9.cQA9E_VTevm6k6ogLwbr_2vSlO8gBt5wo5Hrb6cd39Jcp5JR8yvACBC3dTDACAVjNYRHj_Od7wmTu2va_k_WqQ");

        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();

        int responseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code :  " + responseCode);
        System.out.println("POST Response Message : " + postConnection.getResponseMessage());

        if(responseCode == HttpURLConnection.HTTP_CREATED) {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()))) {
                String inputLine;
                StringBuffer response = new StringBuffer();

                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("JSON String Result " + response.toString());
            }
        } else {
            System.out.println("POST not worked");
        }
    }

    @Test
    public void createPollTest1() throws IOException, URISyntaxException {
        final String baseUrl= createUrlWithPort("api/polls/create");
        URI uri = new URI(baseUrl);

        PollRequest pollRequest = new PollRequest();
        pollRequest.setQuestion("To be or not to be");

        List<ChoiceRequest> choiceRequests = new ArrayList<>();
        ChoiceRequest choiceRequest1 = new ChoiceRequest();
        choiceRequest1.setText("text3");
        choiceRequests.add(choiceRequest1);
        pollRequest.setChoices(choiceRequests);


    }
}