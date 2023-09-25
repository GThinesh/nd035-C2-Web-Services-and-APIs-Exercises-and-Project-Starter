package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testGetPrice() {
        ResponseEntity<Price> price = restTemplate.getForEntity(getUrl(1), Price.class);
        Assertions.assertThat(price.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(price.getBody()).isNotNull();
        Assertions.assertThat(price.getBody().getVehicleId()).isEqualTo(1);
    }

    private String getUrl(int id) {
        return "http://localhost:" + port + "/prices/" + id;
    }

}
