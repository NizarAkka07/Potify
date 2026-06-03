package com.alphateckplus.potify.payment.infrastructure.secondary.payment.gateway;

import com.alphateckplus.potify.payment.application_service.secondary.payment.PayPalGatewayPort;
import com.alphateckplus.potify.payment.domain.model.Contribution;
import com.alphateckplus.potify.payment.infrastructure.primary.payment.dto.CheckoutResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PayPalPaymentAdapter implements PayPalGatewayPort {

    @Value("${paypal.client-id}")
    private String paypalClientId;

    @Value("${paypal.client-secret}")
    private String paypalClientSecret;

    @Value("${paypal.api-url}")
    private String paypalApiUrl;

    @Value("${stripe.frontend-url}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public CheckoutResponse initiatePayPalPayment(Contribution contribution, String cancelUrl) throws Exception {
        log.info("Initiating PayPal payment for contribution: {} with amount: {}", contribution.getId(), contribution.getAmount());

        String accessToken = getPayPalAccessToken();
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("intent", "CAPTURE");

        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("reference_id", contribution.getId());
        
        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("currency_code", "EUR");
        amountMap.put("value", contribution.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        purchaseUnit.put("amount", amountMap);
        purchaseUnit.put("description", "Contribution à la cagnotte");

        orderRequest.put("purchase_units", Collections.singletonList(purchaseUnit));

        Map<String, Object> appContext = new HashMap<>();
        appContext.put("return_url", frontendUrl + "/#/payment/success?method=paypal");
        appContext.put("cancel_url", cancelUrl);
        orderRequest.put("application_context", appContext);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(orderRequest, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(paypalApiUrl + "/v2/checkout/orders", entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Erreur de création de commande PayPal");
        }

        Map<String, Object> responseBody = response.getBody();
        String orderId = (String) responseBody.get("id");
        
        List<Map<String, String>> links = (List<Map<String, String>>) responseBody.get("links");
        String approvalUrl = links.stream()
                .filter(link -> "approve".equals(link.get("rel")))
                .map(link -> link.get("href"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lien d'approbation PayPal manquant"));

        return new CheckoutResponse(orderId, approvalUrl);
    }

    @Override
    public void confirmPayPalPayment(String orderId) throws Exception {
        log.info("Capturing PayPal order: {}", orderId);

        String accessToken = getPayPalAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = paypalApiUrl + "/v2/checkout/orders/" + orderId + "/capture";
        
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Erreur de capture PayPal");
        }

        Map<String, Object> responseBody = response.getBody();
        String status = (String) responseBody.get("status");

        if (!"COMPLETED".equalsIgnoreCase(status)) {
            throw new IllegalStateException("Le paiement PayPal n'a pas le statut COMPLETED");
        }
    }

    private String getPayPalAccessToken() {
        String auth = paypalClientId + ":" + paypalClientSecret;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authHeader);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(paypalApiUrl + "/v1/oauth2/token", entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Erreur de connexion OAuth2 auprès de PayPal (401 Unauthorized)");
        }

        return (String) response.getBody().get("access_token");
    }
}
