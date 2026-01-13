package pl.edu.projekt.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.projekt.core.dto.RateHistoryDto;
import pl.edu.projekt.core.dto.UserAlertDto;
import pl.edu.projekt.core.entity.AppUser;
import pl.edu.projekt.core.entity.Currency;
import pl.edu.projekt.core.entity.FetchLog;
import pl.edu.projekt.core.entity.Rate;
import pl.edu.projekt.core.entity.UserAlert;
import pl.edu.projekt.core.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NbpService {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;
    private final FetchLogRepository fetchLogRepository;
    private final UserAlertRepository userAlertRepository;
    private final AppUserRepository appUserRepository;
    private final RestTemplate restTemplate;

    @Scheduled(fixedRate = 60000)
    public void runAutoFetch() {
        fetchAndSaveRates("A");
    }

    public void fetchAndSaveRates(String tableType) {
        String url = "http://api.nbp.pl/api/exchangerates/tables/" + tableType + "?format=json";

        try {
            NbpTableDto[] response = restTemplate.getForObject(url, NbpTableDto[].class);

            if (response != null && response.length > 0) {
                NbpTableDto table = response[0];
                LocalDate rateDate = LocalDate.parse(table.effectiveDate);

                List<Rate> ratesToSave = new ArrayList<>();

                for (NbpRateDto dto : table.rates) {
                    Currency currency = currencyRepository.findByCode(dto.code)
                            .orElseGet(() -> {
                                Currency newC = new Currency();
                                newC.setCode(dto.code);
                                newC.setName(dto.currency);
                                return currencyRepository.save(newC);
                            });

                    Rate rate = new Rate();
                    rate.setCurrency(currency);
                    rate.setMid(dto.mid);
                    rate.setDate(rateDate);

                    ratesToSave.add(rate);
                }

                rateRepository.saveAll(ratesToSave);

                saveLog("SUCCESS", "Pobrano dane dla tabeli " + tableType + " z dnia: " + rateDate);
                log.info("Sukces: Pobrano i zapisano {} kursów dla tabeli {}", ratesToSave.size(), tableType);

                checkAlerts();
            }
        } catch (Exception e) {
            saveLog("ERROR", "Błąd pobierania tabeli " + tableType + ": " + e.getMessage());
            log.error("Błąd pobierania danych: ", e);
        }
    }


    private void checkAlerts() {
        List<UserAlert> alerts = userAlertRepository.findAll();
        if (alerts.isEmpty()) return;

        Set<String> currenciesInAlerts = alerts.stream()
                .map(UserAlert::getCurrencyCode)
                .collect(Collectors.toSet());

        List<Rate> latestRatesList = rateRepository.findLatestRatesForCurrencies(currenciesInAlerts);

        Map<String, Double> ratesMap = latestRatesList.stream()
                .collect(Collectors.toMap(
                        rate -> rate.getCurrency().getCode(),
                        Rate::getMid
                ));

        log.info("Sprawdzanie {} alertów dla {} walut...", alerts.size(), currenciesInAlerts.size());

        for (UserAlert alert : alerts) {
            Double currentRate = ratesMap.get(alert.getCurrencyCode());

            if (currentRate != null && currentRate > alert.getThreshold()) {
                log.warn("!!! POWIADOMIENIE !!! Użytkownik: {} | Waluta: {} | Kurs: {} > Próg: {}",
                        alert.getUser().getEmail(), alert.getCurrencyCode(), currentRate, alert.getThreshold());
            }
        }
    }

    public List<RateHistoryDto> getHistory(String currencyCode) {
        Currency currency = currencyRepository.findByCode(currencyCode)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono waluty: " + currencyCode));

        return currency.getRates().stream()
                .map(rate -> new RateHistoryDto(rate.getDate(), rate.getMid()))
                .collect(Collectors.toList());
    }

    public void addAlert(UserAlertDto dto) {
        AppUser user = appUserRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o ID: " + dto.getUserId()));

        UserAlert alert = new UserAlert();
        alert.setCurrencyCode(dto.getCurrencyCode().toUpperCase());
        alert.setThreshold(dto.getThreshold());
        alert.setUser(user);

        userAlertRepository.save(alert);
        log.info("Dodano alert dla użytkownika {} na walutę {}", user.getEmail(), dto.getCurrencyCode());
    }

    private void saveLog(String status, String message) {
        FetchLog logEntry = new FetchLog();
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setStatus(status);
        logEntry.setMessage(message);
        fetchLogRepository.save(logEntry);
    }

    @lombok.Data
    static class NbpTableDto {
        private String table;
        private String no;
        private String effectiveDate;
        private List<NbpRateDto> rates;
    }

    @lombok.Data
    static class NbpRateDto {
        private String currency;
        private String code;
        private Double mid;
    }
}