
package com.example.bmi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TavilySearchService {

    @Value("${tavily.enabled:false}")
    private boolean enabled;

    @Value("${tavily.apiKey:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> search(String query) {

        Map<String, String> results = new LinkedHashMap<>();

        // 檢查 API 是否啟用
        if (!enabled || apiKey.isEmpty()) {
            return results;
        }

        // 設定 HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 設定搜尋內容
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("query", query);
        body.put("max_results", 10);

        // 建立 HTTP Request
        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        // 呼叫 Tavily API
        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        "https://api.tavily.com/search",
                        request,
                        Map.class
                );

        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null) {
            return results;
        }

        // 取得搜尋結果
        Object itemsObj = responseBody.get("results");

        if (itemsObj instanceof List) {

            List<?> items = (List<?>) itemsObj;

            for (Object itemObj : items) {

                if (itemObj instanceof Map) {

                    Map<?, ?> item = (Map<?, ?>) itemObj;

                    Object titleObj = item.get("title");
                    Object urlObj = item.get("url");

                    String title = titleObj == null
                            ? null : titleObj.toString();

                    String url = urlObj == null
                            ? null : urlObj.toString();

                    if (title != null && !title.isEmpty()
                            && url != null && !url.isEmpty()) {

                        results.put(title, url);
                    }
                }
            }
        }

        return results;
    }
}
