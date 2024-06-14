package com.example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.lang.Integer;

@RestController
@RequestMapping("/numbers")
public class App {
    private final Queue<Integer> numbersQueue = new LinkedList<>();
    private final int WINDOW_SIZE = 10;
    private final String BEARER_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiZXhwIjoxNzE4MzQ4NDQ0LCJpYXQiOjE3MTgzNDgxNDQsImlzcyI6IkFmZm9yZG1lZCIsImp0aSI6IjU4M2IwMzRkLWM3MTgtNGRjYi04MjNkLTViZjViM2FmMDdlMyIsInN1YiI6IjcyNzgyMXR1Y3MwNTVAc2tjdC5lZHUuaW4ifSwiY29tcGFueU5hbWUiOiJTcmlLcmlzaG5hSW5zdGl0dXRpb25zIiwiY2xpZW50SUQiOiI1ODNiMDM0ZC1jNzE4LTRkY2ItODIzZC01YmY1YjNhZjA3ZTMiLCJjbGllbnRTZWNyZXQiOiJReVZldFdhQndUU1NGY0l1Iiwib3duZXJOYW1lIjoiSGFyc2hhbiBTcmVlIFAgTSIsIm93bmVyRW1haWwiOiI3Mjc4MjF0dWNzMDU1QHNrY3QuZWR1LmluIiwicm9sbE5vIjoiNzI3ODIxVFVDUzA1NSJ9.GRodjMdkMA3wiEud_GUExri0U6O4enMXQ47gHFdGeL0";

    @GetMapping("/{type}")
    public ResponseEntity<?> getNumbers(@PathVariable String type) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://20.244.56.144/test/" + type;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(BEARER_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<NumberResponse> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, NumberResponse.class);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching data from server.");
        }

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            NumberResponse numberResponse = response.getBody();
            for (Integer number : numberResponse.getNumbers()) {
                if (!numbersQueue.contains(number)) {
                    if (numbersQueue.size() >= WINDOW_SIZE) {
                        numbersQueue.poll();
                    }
                    numbersQueue.offer(number);
                }
            }

            double avg = numbersQueue.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            return ResponseEntity.ok(new AverageResponse(new LinkedList<>(numbersQueue), numberResponse.getNumbers(), avg));
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Failed to fetch numbers.");
        }
    }
}

class NumberResponse {
    private List<Integer> numbers;

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}

class AverageResponse {
    private List<Integer> windowPrevState;
    private List<Integer> windowCurrState;
    private List<Integer> numbers;
    private double avg;

    public AverageResponse(List<Integer> windowPrevState, List<Integer> numbers, double avg) {
        this.windowPrevState = windowPrevState;
        this.windowCurrState = new LinkedList<>(windowPrevState);
        this.windowCurrState.addAll(numbers);
        this.numbers = numbers;
        this.avg = avg;
    }
}
