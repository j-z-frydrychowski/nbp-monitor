package pl.edu.projekt.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.projekt.core.service.NbpService;

@RestController
@RequestMapping("/nbp")
public class NbpController {

    private final NbpService nbpService;

    public NbpController(NbpService nbpService) {
        this.nbpService = nbpService;
    }

    /**
     * Endpoint do ręcznego wymuszenia pobrania danych.
     * Użycie: GET http://localhost:8080/nbp/load/A
     */
    @GetMapping("/load/{table}")
    public String loadRates(@PathVariable String table) {
        nbpService.fetchAndSaveRates(table);
        return "Pomyślnie pobrano i zapisano dane dla tabeli: " + table;
    }
}
