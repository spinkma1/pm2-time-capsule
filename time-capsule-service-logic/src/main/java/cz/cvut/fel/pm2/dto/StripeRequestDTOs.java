package cz.cvut.fel.pm2.dto;

import com.google.gson.annotations.SerializedName;

public class StripeRequestDTOs {


    public class CreateSubscriptionRequest {
        @SerializedName("priceId")
        private String priceId;

        public String getPriceId() {
            return priceId;
        }
    }

    public class UpdateSubscriptionRequest {
        @SerializedName("subscriptionId")
        private String subscriptionId;

        @SerializedName("newPriceLookupKey")
        private String newPriceLookupKey;

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public String getNewPriceLookupKey() {
            return newPriceLookupKey;
        }
    }

    public class CancelSubscriptionRequest {
        @SerializedName("subscriptionId")
        private String subscriptionId;

        public String getSubscriptionId() {
            return subscriptionId;
        }
    }

}
