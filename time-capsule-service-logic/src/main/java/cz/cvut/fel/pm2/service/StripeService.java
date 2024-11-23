package cz.cvut.fel.pm2.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import cz.cvut.fel.pm2.dto.StripeRequestDTOs;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    public StripeService() {
        Stripe.apiKey = "sk_test_51QBYEjLFUGYLkGp3vZAXtts3h3xJEKqA8SWpL8BueEFOJIxK6nZA137Zh2lZk65Td2rINkRdiK9xXXvfO6TH2oDG00pFCPEh3o";
        // secret key = pk_test_51QBYEjLFUGYLkGp3LhYWs61r3Zf4aEhPxVbmOYOrOMAf5x5HRwNII7UH2syHlAV6OCk28NOm5UYz0u7L9chrGjYz00hjNE2fQa
    }


    public Customer createCustomer(String name, String email) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(name)
                .setEmail(email)
                .build();
        Customer customer = Customer.create(params);
        return customer;
    }
    public PaymentMethod attachPaymentMethodToCustomer(String paymentMethodId, String customerId) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);

        return paymentMethod.attach(params);
    }



    public Product createProduct(String productName, String productDescription) throws StripeException {
        ProductCreateParams productParams = ProductCreateParams.builder()
                .setName(productName)
                .setDescription(productDescription)
                .setActive(true) // Set to false to create an inactive product
                .build();

        return Product.create(productParams);
    }

    // Create a price for the product
    public Price createPrice(String productId, long unitAmount, String currency) throws StripeException {
        PriceCreateParams priceParams = PriceCreateParams.builder()
                .setUnitAmount(unitAmount) // amount in cents
                .setCurrency(currency)
                .setProduct(productId) // Associate with the product
                .setRecurring(PriceCreateParams.Recurring.builder().setInterval(PriceCreateParams.Recurring.Interval.MONTH).build()) // Monthly subscription
                .build();

        return Price.create(priceParams);
    }






    public Subscription createSubscription(String customerId, String priceId) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("items", Arrays.asList(Map.of("price", priceId)));

        return Subscription.create(params);
    }

    public Subscription updateSubscription(StripeRequestDTOs.UpdateSubscriptionRequest request) throws Exception {
        Subscription subscription = Subscription.retrieve(request.getSubscriptionId());
        String priceId = retrievePriceId(request.getNewPriceLookupKey());

        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .addItem(
                        SubscriptionUpdateParams.Item.builder()
                                .setId(subscription.getItems().getData().get(0).getId())
                                .setPrice(priceId)
                                .build())
                .setCancelAtPeriodEnd(false)
                .build();

        return subscription.update(params);
    }



    //todo check if this is the same thing as cancelSubscriptionImmediately
    public Subscription cancelSubscriptionAtPeriodEnd(String subscriptionId) throws Exception {
        Subscription subscription = Subscription.retrieve(subscriptionId);
        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(true)
                .build();

        return subscription.update(params);
    }

    public Subscription cancelSubscriptionImmediately(String subscriptionId) throws StripeException {
        SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();
        return Subscription.retrieve(subscriptionId).cancel(params);
    }








    public String retrievePriceId(String priceLookupKey) throws Exception {
        PriceListParams params = PriceListParams.builder()
                .addAllLookupKey(List.of(priceLookupKey))
                .build();

        List<Price> prices = Price.list(params).getData();
        if (prices.isEmpty()) {
            throw new Exception("Price not found for lookup key: " + priceLookupKey);
        }
        return prices.get(0).getId();
    }








    public Charge createTestCharge(String token) throws Exception {
        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(1000L) // 10.00 USD
                .setCurrency("usd")
                .setSource(token) // použití tokenu získaného z front-endu
                .setDescription("Test Charge")
                .build();

        return Charge.create(params);
    }


    public Balance getBalance() throws Exception {
        return Balance.retrieve();
    }

    // Method to create a refund
    public Refund createRefund(String paymentIntentId, long amount) throws Exception {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount(amount) // Amount to refund in cents
                .build();

        return Refund.create(params);
    }

    /**
     * Create a PaymentIntent for the given amount and currency - (payment intent == payment attempt)
     *
     * @param amount
     * @param currency
     * @return
     * @throws StripeException
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency, String returnUrl) throws StripeException {
        //returnUrl = The URL to redirect your customer to after they authenticate or cancel their payment
        // only required if you didn't specify the return_url when creating the Checkout Session.
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
//                        .setAutomaticPaymentMethods(
//                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//                                        .setEnabled(true)
//                                        .build()
//                        )
                        .setPaymentMethod("pm_card_visa")
                        .setReturnUrl(returnUrl)
                        .setConfirm(true)
                        .build();


        return PaymentIntent.create(params);
    }

    public Payout createPayout(long paymentChunkCents, String currency) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentChunkCents);
        params.put("currency", currency);
        params.put("method", "standard");

        return Payout.create(params);

//        // Mock payout object
        //        Payout payout = new Payout();
        //        payout.setAmount(paymentChunkCents);
        //        payout.setCurrency(currency);
        //        payout.setStatus("succeeded"); // Assuming the payout is successful
        //        payout.setId("po_mock_payout_id"); // Mock ID
        //        return payout;
    }


    //used when payment intent is not confirmed instantly as i do in the method above
//    public void confirmPaymentIntent(String id) {
//        try {
//            PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
//            paymentIntent.confirm();
//        } catch (StripeException e) {
//            e.printStackTrace();
//        }
//    }
    //com.stripe.exception.InvalidRequestException: You cannot confirm this PaymentIntent because it's missing a payment method. You can either update the PaymentIntent with a payment method and then confirm it again, or confirm it again directly with a payment method or ConfirmationToken.; code: payment_intent_unexpected_state; request-id: req_2N0iYGbqcqq2lA
}
