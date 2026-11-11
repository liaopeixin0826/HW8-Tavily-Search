
package com.example.bmi.controller;

import com.example.bmi.service.TavilySearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private TavilySearchService tavilySearchService;

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            Model model) {

        Map<String, String> results = Map.of();

        if (!query.trim().isEmpty()) {
            results = tavilySearchService.search(query);
        }

        model.addAttribute("query", query);
        model.addAttribute("results", results);
        model.addAttribute("hasSearched", !query.trim().isEmpty());

        return "search";
    }
}
