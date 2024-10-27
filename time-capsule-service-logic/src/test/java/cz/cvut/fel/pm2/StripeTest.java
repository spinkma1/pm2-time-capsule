package cz.cvut.fel.pm2;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import cz.cvut.fel.pm2.service.StripeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class StripeTest {

    @Autowired
    private StripeService stripeService;

    private static final long DESIRED_BALANCE_CENTS = 1000000; // $10,000 in cents


    @BeforeEach
        public void setUp() {
        try {
            //doesn't work - pending payments are too slow
//            ResetTestBalance();


        } catch (Exception e) {
            e.printStackTrace();
        }
        }



        //balance in test mode takes too long to update to available from pending
    @Test
    public void ResetTestBalance() throws Exception {
        // Get the current balance
        Balance balance = stripeService.getBalance();
        long availableBalanceCents = balance.getAvailable().stream().mapToLong(b -> b.getAmount()).sum();
        long pendingBalanceCents = balance.getPending().stream().mapToLong(b -> b.getAmount()).sum();
        long totalBalanceCents = availableBalanceCents + pendingBalanceCents;

        System.out.println("Total balance: " + totalBalanceCents);
        System.out.println("Available balance: " + availableBalanceCents);
        System.out.println("Pending balance: " + pendingBalanceCents);

        // Calculate the amount needed to reach the desired balance
        long currentBalanceCents = totalBalanceCents;
        long paymentAmountCents = DESIRED_BALANCE_CENTS - currentBalanceCents;

        // If the balance is already at the desired amount, return
        if (paymentAmountCents == 0) {
            System.out.println("Balance is already at the desired amount.");
            return;
        }


        // doesn't REALLY do anything - stripe doens't let me remove money from the account
        if (paymentAmountCents < 0) {
            {
                // Create a payment intent for the excess amount to pay out
                long excessAmountCents = -paymentAmountCents; // Convert to positive
                long maxPaymentAmountCents = 99999999; // Maximum allowed payment amount

                while (excessAmountCents > 0) {
                    long paymentChunkCents = Math.min(excessAmountCents, maxPaymentAmountCents);
                    Payout payoutIntent = stripeService.createPayout(paymentChunkCents, "CZK");

                    System.out.println("Created Payout: " + payoutIntent.getId() + " for amount: " + payoutIntent);

                    excessAmountCents -= paymentChunkCents;

                    // Check the payment intent status immediately after creation
                    if ("succeeded".equals(payoutIntent.getStatus())) {
                        System.out.println("Payout intent was already succeeded.");
                    } else if ("requires_confirmation".equals(payoutIntent.getStatus())) {
                        System.out.println("Payout intent requires confirmation.");
                    } else {
                        System.out.println("Payout intent status: " + payoutIntent.getStatus());
                    }

                    excessAmountCents -= paymentChunkCents;
                }

                boolean balanceUpdated = false;
                long updatedAvailableBalanceCents = 0;
                long updatedPendingBalanceCents = 0;
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(2000); // Wait for 2 seconds before retrying
                    Balance updatedBalance = stripeService.getBalance();
                    updatedAvailableBalanceCents = updatedBalance.getAvailable().stream().mapToLong(b -> b.getAmount()).sum();
                    updatedPendingBalanceCents = updatedBalance.getPending().stream().mapToLong(b -> b.getAmount()).sum();
                    long updatedTotalBalanceCents = updatedAvailableBalanceCents + updatedPendingBalanceCents;

                    System.out.println("Updated total balance after payout: " + updatedTotalBalanceCents);
                    if (updatedTotalBalanceCents >= DESIRED_BALANCE_CENTS) {
                        balanceUpdated = true;
                        break;
                    }
                }
                Assertions.assertTrue(balanceUpdated, "Balance did not update to the desired amount.");
                System.out.println("Balance successfully updated to: " + (updatedAvailableBalanceCents + updatedPendingBalanceCents));
                return;

            }
        }

        // Create a payment intent for the calculated amount
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(paymentAmountCents, "czk", "https://example.com/return");
        System.out.println("Created Payment Intent: " + paymentIntent.getId() + " for amount: " + paymentAmountCents);

        // Check the payment intent status immediately after creation
        if ("succeeded".equals(paymentIntent.getStatus())) {
            System.out.println("Payment intent was already succeeded.");
        } else if ("requires_confirmation".equals(paymentIntent.getStatus())) {
            System.out.println("Payment intent requires confirmation.");
        } else {
            System.out.println("Payment intent status: " + paymentIntent.getStatus());
        }

        // Retry checking the balance to ensure it reflects the new payment
        Balance updatedBalance = stripeService.getBalance();
        boolean balanceUpdated = false;
        // Wait a moment for the payment to process and balance to update
        Thread.sleep(2000); // Wait for 2 seconds before retrying
        updatedBalance = stripeService.getBalance();
        long updatedAvailableBalanceCents = updatedBalance.getAvailable().stream().mapToLong(b -> b.getAmount()).sum();
        long updatedPendingBalanceCents = updatedBalance.getPending().stream().mapToLong(b -> b.getAmount()).sum();
        long updatedTotalBalanceCents = updatedAvailableBalanceCents + updatedPendingBalanceCents;

        System.out.println("Updated total balance after payment: " + updatedTotalBalanceCents);

        Assertions.assertTrue(balanceUpdated, "Balance did not update to the desired amount.");
        System.out.println("Balance successfully updated to: " + (updatedAvailableBalanceCents + updatedPendingBalanceCents));
    }

    // Placeholder method for retrieving the last Payment Intent ID
    private String retrieveLastPaymentIntentId() {
        // Implement your logic to fetch the last Payment Intent ID here
        return "pi_last_payment_intent_id"; // Example return
    }


    @Test
    public void testCreateCustomer() throws StripeException {
        String email = "test@example.com";
        String name = "Franta Omacka";
        Customer customer = stripeService.createCustomer(name,email);

        Assertions.assertNotNull(customer);
        Assertions.assertEquals(email, customer.getEmail());
        System.out.println("Created Customer: " + customer.getId());
    }
    @Test
    public void testAddPaymentMethodToCustomer() throws StripeException {
        String email = "test@example.com";
        String name = "Franta Omacka";

        // Create a customer
        Customer customer = stripeService.createCustomer(name, email);
        System.out.println("Created Customer: " + customer.getId());
        String customerId = customer.getId();
        String paymentMethodId = "pm_card_visa"; // Predefined test payment method

        // CAREFUL! even once you input the Id as something, it will be transformed
//        paymentMethod.getId() != paymentMethodId;
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        // Attach the payment method to the customer
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();
        PaymentMethod attachedPaymentMethod = paymentMethod.attach(attachParams);
        Assertions.assertNotNull(attachedPaymentMethod, "Attached Payment Method should not be null");

        // Retrieve the updated customer to verify the payment method attachment
        Customer updatedCustomer = Customer.retrieve(customerId);
        Assertions.assertNotNull(updatedCustomer, "Updated Customer should not be null");

        // List payment methods
        CustomerListPaymentMethodsParams params =
                CustomerListPaymentMethodsParams.builder().setLimit(3L).build();

        PaymentMethodCollection paymentMethods = updatedCustomer.listPaymentMethods(params);
        Assertions.assertTrue(paymentMethods.getData().stream().anyMatch(pm -> pm.getId().equals(paymentMethod.getId())), "Payment Method should be in the list");

        // Optional: Print the details for debugging
        System.out.println("Payment Method attached to Customer: " + attachedPaymentMethod.getId());
    }


    @Test
    public void testCreateSubscription() throws StripeException {
        String email = "test@example.com";
        String name = "Franta Omacka";

        // Create a customer
        Customer customer = stripeService.createCustomer(name, email);
        System.out.println("Created Customer: " + customer.getId());
        String customerId = customer.getId();
        String paymentMethodId = "pm_card_visa"; // Predefined test payment method

        // CAREFUL! even once you input the Id as something, it will be transformed
//        paymentMethod.getId() != paymentMethodId;
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        // Attach the payment method to the customer
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();
        PaymentMethod attachedPaymentMethod = paymentMethod.attach(attachParams);
        Assertions.assertNotNull(attachedPaymentMethod, "Attached Payment Method should not be null");




        Product product = stripeService.createProduct("Test Product", "Test Product Description");

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Test Product", product.getName());
        Assertions.assertEquals("Test Product Description", product.getDescription());


        Price price = stripeService.createPrice(product.getId(), 1000, "czk");
        product.setDefaultPrice(price.getId());

        Assertions.assertNotNull(price);
        Assertions.assertEquals(1000, price.getUnitAmount());
        Assertions.assertEquals("czk", price.getCurrency());
        Assertions.assertEquals(product.getId(), price.getProduct());


        Subscription subscription = stripeService.createSubscription(customerId, price.getId());

        Assertions.assertNotNull(subscription);
        Assertions.assertEquals("active", subscription.getStatus());
        Assertions.assertEquals(customerId, subscription.getCustomer());
        System.out.println("Created Subscription: " + subscription.getId());

    }

    @Test
    public void testCreatePayout() throws StripeException {
        long payoutAmount = 3000; // Amount in cents
        String currency = "czk";

        Payout payout = stripeService.createPayout(payoutAmount, currency);

        Assertions.assertNotNull(payout);
        Assertions.assertEquals(payoutAmount, payout.getAmount());
        System.out.println("Created Payout: " + payout.getId());
    }

    @Test
    public void testCancelSubscription() throws StripeException {
        String email = "test@example.com";
        String name = "Franta Omacka";

        // Create a customer
        Customer customer = stripeService.createCustomer(name, email);
        String customerId = customer.getId();

        // Create a product and price for the subscription
        Product product = stripeService.createProduct("Test Product", "Test Product Description");
        Price price = stripeService.createPrice(product.getId(), 1000, "czk");

        // Create a subscription
        Subscription subscription = stripeService.createSubscription(customerId, price.getId());
        Assertions.assertNotNull(subscription);
        Assertions.assertEquals("active", subscription.getStatus());
        System.out.println("Created Subscription: " + subscription.getId());

        // Now cancel the subscription
        Subscription cancelledSubscription = stripeService.cancelSubscriptionImmediately(subscription.getId());
        Assertions.assertNotNull(cancelledSubscription);
        Assertions.assertEquals("canceled", cancelledSubscription.getStatus());
        System.out.println("Cancelled Subscription: " + cancelledSubscription.getId());
    }
    @Test
    public void testSubscriptionRefund() throws Exception {
        // Step 1: Create a customer
        String email = "test@example.com";
        String name = "Franta Omacka";
        Customer customer = stripeService.createCustomer(name, email);
        Assertions.assertNotNull(customer);
        Assertions.assertEquals(email, customer.getEmail());
        System.out.println("Created Customer: " + customer.getId());
        String customerId = customer.getId();

        // Attach a payment method (using pm_card_visa for testing)
        PaymentMethod paymentMethod = PaymentMethod.retrieve("pm_card_visa");
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();
        paymentMethod.attach(attachParams);

        // Create a product and price
        Product product = stripeService.createProduct("Test Product", "Test Product Description");
        Price price = stripeService.createPrice(product.getId(), 1000, "czk");

        // Step 2: Create a subscription
        Map<String, Object> items = new HashMap<>();
        items.put("price", price.getId());

        long billingCycleAnchor = System.currentTimeMillis() / 1000 + 60; // Current time plus 60 seconds

        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("items", Arrays.asList(items));
        params.put("payment_behavior", "default_incomplete");
        params.put("billing_cycle_anchor", billingCycleAnchor);
        params.put("expand", Arrays.asList("latest_invoice.payment_intent"));

        Subscription subscription = Subscription.create(params);
        System.out.println("Created Subscription: " + subscription.getId());

        // Retrieve the invoices for the subscription
        Map<String, Object> invoiceParams = new HashMap<>();
        invoiceParams.put("subscription", subscription.getId());

        InvoiceCollection invoices = Invoice.list(invoiceParams);
        Invoice latestInvoice = invoices.getData().get(0); // Assuming the first invoice is the latest

        System.out.println("Retrieved Invoice: " + latestInvoice.getId());

        // Ensure the latest invoice has a payment intent
        String paymentIntentId = latestInvoice.getPaymentIntent();
        if (paymentIntentId == null) {
            System.out.println("No payment intent found for the invoice.");
            Assertions.fail("Payment Intent should not be null");
        } else {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            if (!"succeeded".equals(paymentIntent.getStatus())) {
                paymentIntent = paymentIntent.confirm();
            }
            System.out.println("Confirmed Payment Intent: " + paymentIntent.getId());

            // Ensure the invoice has a charge
            Assertions.assertNotNull(latestInvoice.getCharge(), "Invoice should have a charge");

            Charge charge = Charge.retrieve(latestInvoice.getCharge());

            // Assert that the charge was created successfully
            Assertions.assertNotNull(charge);
            Assertions.assertEquals("succeeded", charge.getStatus(), "Charge should be successful");

            // Step 4: Refund the charge
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setCharge(charge.getId())
                    .build();
            Refund refund = Refund.create(refundParams);

            // Assert the refund was successful
            Assertions.assertNotNull(refund);
            Assertions.assertEquals("succeeded", refund.getStatus(), "Charge should be refunded");

            // Optional: Print the details for debugging
            System.out.println("Refunded Charge ID: " + refund.getCharge());
        }
    }

}
