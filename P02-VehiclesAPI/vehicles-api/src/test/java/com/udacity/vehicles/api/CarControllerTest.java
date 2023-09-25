package com.udacity.vehicles.api;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;
    @Captor
    private ArgumentCaptor<Car> carCaptor;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                        post(new URI("/cars"))
                                .content(json.write(car).getJson())
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {

        ResultActions actions = mvc.perform(get(new URI("/cars"))).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._embedded.carList", hasSize(1)));
        assertCar(actions, "$._embedded.carList[0]");


    }

    private void assertCar(ResultActions actions, String pathToCar) throws Exception {
        Car car = getCar();
        Details detail = car.getDetails();
        actions.andExpect(jsonPath(pathToCar + ".id", is(1)))
                .andExpect(jsonPath(pathToCar + ".createdAt", nullValue()))
                .andExpect(jsonPath(pathToCar + ".modifiedAt", nullValue()))
                .andExpect(jsonPath(pathToCar + ".details.body", is(detail.getBody())))
                .andExpect(jsonPath(pathToCar + ".details.model", is(detail.getModel())))
                .andExpect(jsonPath(pathToCar + ".details.manufacturer.code", is(detail.getManufacturer().getCode())))
                .andExpect(jsonPath(pathToCar + ".details.numberOfDoors", is(detail.getNumberOfDoors())))
                .andExpect(jsonPath(pathToCar + ".details.fuelType", is(detail.getFuelType())))
                .andExpect(jsonPath(pathToCar + ".details.engine", is(detail.getEngine())))
                .andExpect(jsonPath(pathToCar + ".details.mileage", is(detail.getMileage())))
                .andExpect(jsonPath(pathToCar + ".details.modelYear", is(detail.getModelYear())))
                .andExpect(jsonPath(pathToCar + ".details.productionYear", is(detail.getProductionYear())))
                .andExpect(jsonPath(pathToCar + ".details.externalColor", is(detail.getExternalColor())))
                .andExpect(jsonPath(pathToCar + ".location.lat", is(car.getLocation().getLat())));
    }

    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        ResultActions actions = mvc.perform(get(new URI("/cars/1"))).andExpect(status().isOk());
        assertCar(actions, "$");
    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        mvc.perform(delete(new URI("/cars/1"))).andDo(print());
        verify(carService, times(1)).delete(1L);

    }

    @Test
    public void updateCar() throws Exception {
        Car car = getCar();
        mvc.perform(post(new URI("/cars"))
                                .content(json.write(car).getJson())
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());

        int newMileage = car.getDetails().getMileage() + 1000;
        car.getDetails().setMileage(newMileage);
        car.setLocation(new Location(40.730615, -73.935242));

        mvc.perform(put(new URI("/cars/1"))
                .content(json.write(car).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk());

        verify(carService,times(2)).save(carCaptor.capture());

        Car putCar = carCaptor.getValue();
        Assertions.assertThat(putCar.getDetails().getMileage()).isEqualTo(newMileage);
        Assertions.assertThat(putCar.getLocation().getLat()).isEqualTo(40.730615);
    }

    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}