package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveClient {

    private final RestTemplate restTemplate;
    private final String incentiveUrl;

    public IncentiveClient(RestTemplate restTemplate,
            @Value("${general.incentive-url:http://localhost:8080/incentive}") String incentiveUrl) {
        this.restTemplate = restTemplate;
        this.incentiveUrl = incentiveUrl;
    }

    public float fetchIncentiveAmount(Transaction tx) {
        Incentive response = restTemplate.postForObject(incentiveUrl, tx, Incentive.class);
        if (response == null)
            return 0.0f;
        return Math.max(0.0f, response.getAmount());
    }
}
