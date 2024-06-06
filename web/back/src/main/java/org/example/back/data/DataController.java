package org.example.back.data;

import lombok.AllArgsConstructor;
import org.example.back.service.DataAPI;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
@CrossOrigin
@AllArgsConstructor
public class DataController {
    private final DataAPI dataAPI;

    @GetMapping("/getCriterias")
    public List<String> getCriterias() {
        return dataAPI.getCriterias()
    }

    @GetMapping("/getAlternatives")
    public List<Map<String, String>> getAlternatives() {
        return dataAPI.getAlternatives()
    }
}
