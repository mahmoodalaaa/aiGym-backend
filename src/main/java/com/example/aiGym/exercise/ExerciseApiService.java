package com.example.aiGym.exercise;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
public class ExerciseApiService {

    private final RestTemplate restTemplate;

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.host}")
    private String rapidApiHost;

    private static final String BASE_URL = "https://exercisedb-13001.p.rapidapi.com/api/exercises1/";

    public ExerciseApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ExerciseDTO> searchExercises(String keyword, int offset, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "SearchExercises")
                .queryParam("keyword", keyword)
                .queryParam("offset", offset)
                .queryParam("limit", Math.min(limit, 25))
                .toUriString();

        return fetchExercises(url);
    }

    public List<ExerciseDTO> getExercisesByMuscle(String muscle, int offset, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "GetExercisesByMuscle")
                .queryParam("muscle", muscle)
                .queryParam("offset", offset)
                .queryParam("limit", Math.min(limit, 25))
                .toUriString();

        return fetchExercises(url);
    }

    public List<ExerciseDTO> getExercisesByBodypart(String bodypart, int offset, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "SearchExercises")
                .queryParam("keyword", bodypart)
                .queryParam("offset", offset)
                .queryParam("limit", Math.min(limit, 25))
                .toUriString();

        return fetchExercises(url);
    }

    public List<String> getAllMuscles() {
        return fetchNames(BASE_URL + "GetAllMuscles");
    }

    public List<String> getAllBodyparts() {
        return fetchNames(BASE_URL + "GetAllBodyparts");
    }

    private List<String> fetchNames(String url) {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        try {
            ResponseEntity<ExerciseDBResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, ExerciseDBResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getData().stream()
                        .map(ExerciseDTO::getName)
                        .toList();
            }
        } catch (Exception e) {
            System.err.println("Error fetching from " + url + ": " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<ExerciseDTO> fetchExercises(String url) {
        HttpHeaders headers = createHeaders();
        // The API reference shows a body is required even for GET-like POST requests
        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        try {
            ResponseEntity<ExerciseDBResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, ExerciseDBResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            System.err.println("Error fetching exercises from " + url + ": " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-rapidapi-key", rapidApiKey);
        headers.set("x-rapidapi-host", rapidApiHost);
        return headers;
    }
}
