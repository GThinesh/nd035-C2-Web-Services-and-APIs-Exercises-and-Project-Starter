package com.udacity.vehicles;

import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.domain.manufacturer.ManufacturerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Launches a Spring Boot application for the Vehicles API,
 * initializes the car manufacturers in the database,
 * and launches web clients to communicate with maps and pricing.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableEurekaClient
public class VehiclesApiApplication {
    //https://dzone.com/articles/the-road-to-reactive-spring-cloud


    public static void main(String[] args) {
        SpringApplication.run(VehiclesApiApplication.class, args);
    }


    /**
     * Initializes the car manufacturers available to the Vehicle API.
     *
     * @param repository where the manufacturer information persists.
     * @return the car manufacturers to add to the related repository
     */
    @Bean
    CommandLineRunner initDatabase(ManufacturerRepository repository, CarRepository carRepository) {
        return args -> {
            repository.save(new Manufacturer(100, "Audi"));
            repository.save(new Manufacturer(101, "Chevrolet"));
            repository.save(new Manufacturer(102, "Ford"));
            repository.save(new Manufacturer(103, "BMW"));
            repository.save(new Manufacturer(104, "Dodge"));

            Car car = new Car();
            car.setCondition(Condition.NEW);
            Details details = new Details();
            details.setBody("Body");
            details.setModel("AB");
            details.setManufacturer(repository.findByCode(100));
            car.setDetails(details);
            carRepository.save(car);

        };
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Web Client for the maps (location) API
     *
     * @param endpoint where to communicate for the maps API
     * @return created maps endpoint
     */
    @Bean(name = "maps")
    public WebClient webClientMaps(@Value("${maps.endpoint}") String endpoint) {
        return WebClient.create(endpoint);
    }

    /**
     * Web Client for the pricing API
     *
     * @param endpoint where to communicate for the pricing API
     * @return created pricing endpoint
     */
    @Bean(name = "pricing")
    public WebClient webClientPricing(@Value("${pricing.endpoint}") String endpoint,LoadBalancerExchangeFilterFunction lbFilter) {
        return WebClient.builder().filter(lbFilter).baseUrl(endpoint).build();
    }

}
