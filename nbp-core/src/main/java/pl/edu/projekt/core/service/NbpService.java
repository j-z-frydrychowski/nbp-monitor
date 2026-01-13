package pl.edu.projekt.core.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.projekt.core.dto.RateHistoryDto;
import pl.edu.projekt.core.dto.UserAlertDto;
import pl.edu.projekt.core.entity.*;
import pl.edu.projekt.core.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class NbpService {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;
    private final FetchLogRepository fetchLogRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AppUserRepository appUserRepository;
    private final UserAlertRepository userAlertRepository;

    @Scheduled(fixedRate = 60000)
    public void runAutoFetch() {
        fetchAndSaveRates("A");
    }

    public void fetchAndSaveRates(String tableType) {
        // Budujemy URL dynamicznie, wstawiając parametr tableType
        String url = "http://api.nbp.pl/api/exchangerates/tables/" + tableType + "?format=json";

        try {
            NbpTableDto[] response = restTemplate.getForObject(url, NbpTableDto[].class);

            if (response != null && response.length > 0) {
                NbpTableDto table = response[0];
                LocalDate rateDate = LocalDate.parse(table.effectiveDate);

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
                    rateRepository.save(rate);
                }
                saveLog("SUCCESS", "Pobrano dane dla tabeli " + tableType + " z dnia: " + rateDate);
                System.out.println("Sukces: Pobrano tabelę " + tableType);

                checkAlerts();
            }
        } catch (Exception e) {
            saveLog("ERROR", "Błąd pobierania tabeli " + tableType + ": " + e.getMessage());
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void saveLog(String status, String message) {
        FetchLog log = new FetchLog();
        log.setTimestamp(LocalDateTime.now());
        log.setStatus(status);
        log.setMessage(message);
        fetchLogRepository.save(log);
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

    public java.util.List<RateHistoryDto> getHistory(String currencyCode) {
        Currency currency = currencyRepository.findByCode(currencyCode)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono waluty o kodzie: " + currencyCode));

        return currency.getRates().stream()
                .map(rate -> new RateHistoryDto(rate.getDate(), rate.getMid()))
                .collect(toList());
    }

    public void addAlert(UserAlertDto dto) {

        AppUser user = appUserRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Brak użytkowników w bazie!"));

        // 2. Tworzymy encję Alertu
        UserAlert alert = new pl.edu.projekt.core.entity.UserAlert();
        alert.setCurrencyCode(dto.getCurrencyCode().toUpperCase()); // np. usd -> USD
        alert.setThreshold(dto.getThreshold());
        alert.setUser(user);


        userAlertRepository.save(alert);
        System.out.println("Dodano alert dla waluty " + dto.getCurrencyCode() + " powyżej " + dto.getThreshold());
    }

    private void checkAlerts(){
        List<UserAlert> alerts = userAlertRepository.findAll();

        if (alerts.isEmpty()) {
            return;
        }

        for (UserAlert alert : alerts) {
            currencyRepository.findByCode(alert.getCurrencyCode()).ifPresent(currency ->{
                rateRepository.findTopByCurrencyOrderByDateDesc(currency).ifPresent(rate -> {
                    if (rate.getMid() > alert.getThreshold()) {
                       System.out.println("ATTENTION!\n Currency " + alert.getCurrencyCode() +
                               " exceeded threshold of " + alert.getThreshold() +
                               ". Current rate: " + rate.getMid() +
                               ". Notify user: " + alert.getUser().getEmail());

                    }
                });
            });
        }
    }
}
