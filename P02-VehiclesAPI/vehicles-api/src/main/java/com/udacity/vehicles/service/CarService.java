package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final MapsClient mapsClient;
    private final PriceClient pricingClient;

    public CarService(CarRepository repository, MapsClient mapsClient, PriceClient pricingClient) {
        this.repository = repository;
        this.mapsClient = mapsClient;
        this.pricingClient = pricingClient;
    }

    /**
     * Gathers a list of all vehicles
     *
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll()
                .stream()
                .map(this::applyPriceAndAddress)
                .collect(Collectors.toList());
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     *
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car car = getPresentCar(id);
        applyPriceAndAddress(car);

        return car;
    }

    private Car applyPriceAndAddress(Car car) {
        Long id = car.getId();
        String price = pricingClient.getPrice(id);
        car.setPrice(price);
        Location address = mapsClient.getAddress(car.getLocation());
        car.getLocation().setAddress(address.getAddress());
        return car;
    }

    private Car getPresentCar(Long id) {
        return repository.findById(id).orElseThrow(() -> new CarNotFoundException(String.format("Car with id %s is not found", id)));
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     *
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId()).map(carToBeUpdated -> {
                carToBeUpdated.setDetails(car.getDetails());
                carToBeUpdated.setLocation(car.getLocation());
                carToBeUpdated.setCondition(car.getCondition());
                return repository.save(carToBeUpdated);
            }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     *
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        repository.delete(getPresentCar(id));
    }
}
