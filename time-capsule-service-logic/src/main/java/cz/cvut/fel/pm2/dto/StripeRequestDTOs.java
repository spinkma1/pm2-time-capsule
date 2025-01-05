package cz.cvut.fel.pm2.dto;

import com.google.gson.annotations.SerializedName;

public class StripeRequestDTOs {

    /**
     * Request DTO for creating a subscription.
     */
    public class CreateSubscriptionRequest {
        @SerializedName("priceId")
        private String priceId;

        /**
         * Gets the price ID.
         *
         * @return the price ID
         */
        public String getPriceId() {
            return priceId;
        }
    }

    /**
     * Request DTO for updating a subscription.
     */
    public class UpdateSubscriptionRequest {
        @SerializedName("subscriptionId")
        private String subscriptionId;

        @SerializedName("newPriceLookupKey")
        private String newPriceLookupKey;

        /**
         * Gets the subscription ID.
         *
         * @return the subscription ID
         */
        public String getSubscriptionId() {
            return subscriptionId;
        }

        /**
         * Gets the new price lookup key.
         *
         * @return the new price lookup key
         */
        public String getNewPriceLookupKey() {
            return newPriceLookupKey;
        }
    }

    /**
     * Request DTO for canceling a subscription.
     */
    public class CancelSubscriptionRequest {
        @SerializedName("subscriptionId")
        private String subscriptionId;

        /**
         * Gets the subscription ID.
         *
         * @return the subscription ID
         */
        public String getSubscriptionId() {
            return subscriptionId;
        }
    }

}