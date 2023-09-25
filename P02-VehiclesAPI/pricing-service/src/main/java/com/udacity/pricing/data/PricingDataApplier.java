package com.udacity.pricing.data;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.domain.price.PriceRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Component
public class PricingDataApplier implements ApplicationRunner {
    private static final Map<Long, Price> PRICES = LongStream
            .range(1, 20)
            .mapToObj(i -> new Price("USD", randomPrice(), i))
            .collect(Collectors.toMap(Price::getVehicleId, p -> p));
    private final PriceRepository priceRepository;

    public PricingDataApplier(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * Gets a random price to fill in for a given vehicle ID.
     *
     * @return random price for a vehicle
     */
    private static BigDecimal randomPrice() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal("5000")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        priceRepository.saveAll(PRICES.values());
    }


}
