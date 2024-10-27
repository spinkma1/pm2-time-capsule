package cz.cvut.fel.pm2.rest;


import com.google.gson.Gson;
import com.stripe.model.Subscription;
import cz.cvut.fel.pm2.dto.StripeRequestDTOs;
import cz.cvut.fel.pm2.service.StripeService;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class StripeController {

    private final StripeService stripeService;
    private final Gson gson;

    public StripeController(StripeService subscriptionService) {
        this.stripeService = subscriptionService;
        this.gson = new Gson();
    }

    public String createSubscription(Request request, Response response) throws Exception {
        String customerId = request.cookie("customer");
        StripeRequestDTOs.CreateSubscriptionRequest postBody = gson.fromJson(request.body(), StripeRequestDTOs.CreateSubscriptionRequest.class);

        Subscription subscription = stripeService.createSubscription(customerId, postBody.getPriceId());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("subscriptionId", subscription.getId());
        responseData.put("clientSecret", subscription.getLatestInvoiceObject().getPaymentIntentObject().getClientSecret());
        return gson.toJson(responseData);
    }

    public String updateSubscription(Request request, Response response) throws Exception {
        StripeRequestDTOs.UpdateSubscriptionRequest postBody = gson.fromJson(request.body(), StripeRequestDTOs.UpdateSubscriptionRequest.class);
        Subscription subscription = stripeService.updateSubscription(postBody);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("subscription", subscription);
        return gson.toJson(responseData);
    }

    // Add methods for other endpoints, like cancelSubscription, previewInvoice, etc.
}

