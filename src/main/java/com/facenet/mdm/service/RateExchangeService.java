package com.facenet.mdm.service;

import com.facenet.mdm.service.dto.Currency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RateExchangeService {
    private final static Logger logger = LoggerFactory.getLogger(RateExchangeService.class);

    @Value("${exchange-rate.api}")
    private String rateExchangeUri;

    public Map<String, Float> getRateExchange() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, Float> rateExchange = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        String responseBody = restTemplate.exchange(rateExchangeUri, HttpMethod.GET, httpEntity, String.class).getBody();
        ObjectMapper mapper = new ObjectMapper();
        Currency map = mapper.readValue(responseBody, Currency.class);
        Map<String, Float> rate = map.getRates();
        // USD -> VND
        Float vnd = rate.get("VND");
        rateExchange.put("VND", 1f);
        rateExchange.put("USD", vnd);
        // (USD/VND) / (USD/CNY) -> CNY/VND
        rateExchange.put("CNY", vnd / rate.get("CNY"));
        rateExchange.put("EUR", vnd / rate.get("EUR"));
        rateExchange.put("KRW", vnd / rate.get("KRW"));
        rateExchange.put("JPY", vnd / rate.get("JPY"));
        rateExchange.forEach((k, v) -> logger.info("Currency {} to VND: {}", k, v));

        return rateExchange;
    }

}
