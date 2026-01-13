package pl.edu.projekt.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.edu.projekt.core.dto.RateHistoryDto;
import pl.edu.projekt.core.dto.UserAlertDto;
import pl.edu.projekt.core.service.NbpService;

import java.util.List;

@RestController
@RequestMapping("/nbp")
@RequiredArgsConstructor
public class NbpController {

    private final NbpService nbpService;

    @GetMapping("/load/{table}")
    public String loadRates(@PathVariable String table) {
        nbpService.fetchAndSaveRates(table);
        return "Zlecono pobranie danych dla tabeli: " + table;
    }

    @GetMapping("/history/{code}")
    public List<RateHistoryDto> getCurrencyHistory(@PathVariable String code) {
        return nbpService.getHistory(code);
    }

    @PostMapping("/alert")
    public String addAlert(@RequestBody UserAlertDto dto) {
        if (dto.getUserId() == null) {
            dto.setUserId(1L);
        }
        nbpService.addAlert(dto);
        return "Dodano alert dla waluty: " + dto.getCurrencyCode();
    }
}