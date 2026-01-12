package pl.edu.projekt.core.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.projekt.core.entity.Currency;
import pl.edu.projekt.core.entity.FetchLog;
import pl.edu.projekt.core.entity.Rate;
import pl.edu.projekt.core.repository.CurrencyRepository;
import pl.edu.projekt.core.repository.FetchLogRepository;
import pl.edu.projekt.core.repository.RateRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NbpService {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;
    private final FetchLogRepository fetchLogRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public NbpService(CurrencyRepository currencyRepository, RateRepository rateRepository, FetchLogRepository fetchLogRepository) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
        this.fetchLogRepository = fetchLogRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void fetchAndSaveRates() {
        String url = "http://api.nbp.pl/api/exchangerates/tables/A?format=json";

        try {
            NbpTableDto[] responese = restTemplate.getForObject(url, NbpTableDto[].class);

            if (responese != null & responese.length > 0) {
                NbpTableDto table = responese[0];
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
                saveLog("SUCCESS", "Fetched and saved rates for date: " + rateDate);
                System.out.println("Fetched and saved rates for date: " + rateDate);
                }
            } catch (Exception e) {
            saveLog("ERROR", "Failed to fetch rates: " + e.getMessage());
            System.err.println("Failed to fetch rates: " + e.getMessage());
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
}
