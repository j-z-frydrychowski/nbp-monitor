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
     * Endpoint do pobierania danych.
     * Przykłady:
     * GET http://localhost:8080/nbp/load/A  -> Pobierze tabelę A (waluty główne)
     * GET http://localhost:8080/nbp/load/B  -> Pobierze tabelę B (waluty egzotyczne)
     */
    @GetMapping("/load/{table}")
    public String loadRates(@PathVariable String table) {
        // Teraz przekazujemy zmienną 'table' do serwisu!
        nbpService.fetchAndSaveRates(table);

        return "Wysłano żądanie pobrania dla tabeli: " + table;
    }
    /**
     * Endpoint do pobierania historii kursu dla danej waluty.
     * Przykład:
     * GET http://localhost:8080/nbp/history/USD  -> Pobierze historię kursu dla dolara amerykańskiego
     */
    @GetMapping("/history/{code}")
    public java.util.List<pl.edu.projekt.core.dto.RateHistoryDto> getCurrencyHistory(@PathVariable String code) {
        return nbpService.getHistory(code);
    }

    @org.springframework.web.bind.annotation.PostMapping("/alert")
    public String addAlert(@org.springframework.web.bind.annotation.RequestBody pl.edu.projekt.core.dto.UserAlertDto dto) {
        nbpService.addAlert(dto);
        return "Dodano alert dla waluty: " + dto.getCurrencyCode();
    }
}
