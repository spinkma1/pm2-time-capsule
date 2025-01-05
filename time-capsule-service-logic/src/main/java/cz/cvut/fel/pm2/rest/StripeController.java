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

    /**
     * Constructs a StripeController with the specified StripeService.
     *
     * @param subscriptionService the service for handling Stripe subscriptions
     */
    public StripeController(StripeService subscriptionService) {
        this.stripeService = subscriptionService;
        this.gson = new Gson();
    }

    /**
     * Creates a new subscription for a customer.
     *
     * @param request the HTTP request containing the subscription details
     * @param response the HTTP response
     * @return a JSON string containing the subscription ID and client secret
     * @throws Exception if an error occurs while creating the subscription
     */
    public String createSubscription(Request request, Response response) throws Exception {
        String customerId = request.cookie("customer");
        StripeRequestDTOs.CreateSubscriptionRequest postBody = gson.fromJson(request.body(), StripeRequestDTOs.CreateSubscriptionRequest.class);

        Subscription subscription = stripeService.createSubscription(customerId, postBody.getPriceId());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("subscriptionId", subscription.getId());
        responseData.put("clientSecret", subscription.getLatestInvoiceObject().getPaymentIntentObject().getClientSecret());
        return gson.toJson(responseData);
    }

    /**
     * Updates an existing subscription.
     *
     * @param request the HTTP request containing the subscription update details
     * @param response the HTTP response
     * @return a JSON string containing the updated subscription
     * @throws Exception if an error occurs while updating the subscription
     */
    public String updateSubscription(Request request, Response response) throws Exception {
        StripeRequestDTOs.UpdateSubscriptionRequest postBody = gson.fromJson(request.body(), StripeRequestDTOs.UpdateSubscriptionRequest.class);
        Subscription subscription = stripeService.updateSubscription(postBody);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("subscription", subscription);
        return gson.toJson(responseData);
    }

    /**
     * Cancels an existing subscription immediately.
     *
     * @param request the HTTP request containing the subscription ID
     * @param response the HTTP response
     * @return a JSON string containing the canceled subscription
     * @throws Exception if an error occurs while canceling the subscription
     */
    public String cancelSubscription(Request request, Response response) throws Exception {
        String subscriptionId = request.params(":subscriptionId");
        Subscription subscription = stripeService.cancelSubscriptionImmediately(subscriptionId);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("subscription", subscription);
        return gson.toJson(responseData);
    }
}

