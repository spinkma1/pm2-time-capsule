package cz.cvut.fel.pm2.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.net.Webhook;

public class StripeWebhookService {


    public Event verifyAndParseEvent(String payload, String sigHeader, String endpointSecret) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, endpointSecret);
    }

    public void handleEvent(Event event) {
        switch (event.getType()) {
            case "invoice.payment_succeeded":
                Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
                if (invoice != null && "subscription_create".equals(invoice.getBillingReason())) {
                    handleSubscriptionCreation(invoice);
                }
                break;
            case "invoice.payment_failed":
                // Handle other events
                break;
            // Add other cases
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
    }

    private void handleSubscriptionCreation(Invoice invoice) {
        // Implementation for handling subscription creation
    }
}

