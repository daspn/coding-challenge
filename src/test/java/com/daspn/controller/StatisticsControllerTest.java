package com.daspn.controller;

import com.daspn.service.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = StatisticsController.class)
@ComponentScan({"com.daspn"})
public class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StatisticsService statisticsService;

    private static final String TRANSACTIONS_ENDPOINT = "/transactions";
    private static final String STATISTICS_ENDPOINT = "/statistics";

    @Test
    public void getStatistics() throws Exception {

        //"old" transaction submited
        mockMvc.perform(post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"amount\": 579.3,\"timestamp\": \"%s\"}", Instant.now().minusSeconds(120).toEpochMilli())))
                .andExpect(status().isNoContent());

        //old transaction stored, but should not be included in statistics
        mockMvc.perform(get(STATISTICS_ENDPOINT))
                .andExpect(status().isNoContent());

        //new transaction submitted
        mockMvc.perform(post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"amount\": 579.3,\"timestamp\": \"%s\"}", System.currentTimeMillis())))
                .andExpect(status().isCreated());

        //new transaction should be returned in statistics
        ensureStatisticsAreCorrect(579.3, 579.3, 579.3, 579.3, 1);

        //a future transaction is submited
        mockMvc.perform(post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"amount\": 420.7,\"timestamp\": \"%s\"}", Instant.now().plusSeconds(120).toEpochMilli())))
                .andExpect(status().isCreated());

        ensureStatisticsAreCorrect(579.3, 579.3, 579.3, 579.3, 1);

        //another transaction within the 60 seconds time window is submited
        mockMvc.perform(post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"amount\": 420.7,\"timestamp\": \"%s\"}", Instant.now().minusSeconds(55).toEpochMilli())))
                .andExpect(status().isCreated());

        ensureStatisticsAreCorrect(1000.0, 500.0, 579.3, 420.7, 2);
    }

    private void ensureStatisticsAreCorrect(Double sum, Double avg, Double max, Double min, Integer count) throws Exception {
        mockMvc.perform(get(STATISTICS_ENDPOINT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.sum", is(sum)))
                .andExpect(jsonPath("$.avg", is(avg)))
                .andExpect(jsonPath("$.max", is(max)))
                .andExpect(jsonPath("$.min", is(min)))
                .andExpect(jsonPath("$.count", is(count)))
                .andExpect(status().isOk());
    }
}
