import React, { useState, useEffect } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
import { CardElement } from '@stripe/react-stripe-js';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const stripePromise = loadStripe("pk_test_51QBYEjLFUGYLkGp3LhYWs61r3Zf4aEhPxVbmOYOrOMAf5x5HRwNII7UH2syHlAV6OCk28NOm5UYz0u7L9chrGjYz00hjNE2fQa");

const SubscriptionOptions = () => {
    const navigate = useNavigate();
    const options = [
        { 
            name: "Basic", 
            priceId: "prod_RFTPfu19cr4cXz", 
            description: "10 Kč měsíčně", 
            benefits: [
                "Přístup k základním funkcím",
                "Omezený čas na používání",
                "Podpora přes e-mail"
            ] 
        },
        { 
            name: "Standard", 
            priceId: "prod_RFTPMsIOSCcRED", 
            description: "50 Kč měsíčně", 
            benefits: [
                "Přístup k většině funkcí",
                "Neomezený čas na používání",
                "Prioritní podpora přes e-mail",
                "Přístup k beta funkcím"
            ] 
        },
        { 
            name: "Premium", 
            priceId: "prod_RFTPWhStF5ueEj", 
            description: "100 Kč měsíčně", 
            benefits: [
                "Přístup ke všem funkcím",
                "Neomezený čas na používání",
                "Exkluzivní podpora přes telefon",
                "Prioritní přístup k beta funkcím",
                "Přístup k prémiovému obsahu"
            ] 
        },
    ];

    const [selectedPlan, setSelectedPlan] = useState(null);
    const [clientSecret, setClientSecret] = useState('');
    const [paymentStatus, setPaymentStatus] = useState('');
    const [loading, setLoading] = useState(false);

    // Funkce pro získání clientSecret z backendu
    const fetchClientSecret = async () => {
        setLoading(true);
        /* // TODO
        const response = await fetch('/create-checkout-session', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ priceId: selectedPlan?.priceId }),
        });
        const { clientSecret } = await response.json(); */
        setClientSecret(
            "pk_test_51QBYEjLFUGYLkGp3LhYWs61r3Zf4aEhPxVbmOYOrOMAf5x5HRwNII7UH2syHlAV6OCk28NOm5UYz0u7L9chrGjYz00hjNE2fQa"
        );
        setLoading(false);
    };

    const handleSubscription = async () => {
        if (!selectedPlan) {
            alert("Please select a plan.");
            return;
        }
        fetchClientSecret();
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <button
                        onClick={() => navigate('/dashboard')}
                        className="flex items-center text-gray-600 hover:text-blue-900"
                    >
                        <ArrowLeft size={20} className="mr-2" />
                        Zpět na přehled
                    </button>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                <div className="max-w-3xl mx-auto">
                    {/* Form content */}
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <div className="space-y-6">
                            <h2 className="text-2xl font-bold text-gray-900">Výběr předplatného</h2>
                            <div>
                                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
                                    {options.map((plan) => (
                                        <div
                                            key={plan.priceId}
                                            className={`p-6 border rounded-lg cursor-pointer transition duration-300 
                                                ${selectedPlan === plan ? "border-blue-900 bg-blue-50" : "border-gray-300"} 
                                                hover:border-blue-800 hover:bg-blue-50`}
                                            onClick={() => setSelectedPlan(plan)}
                                        >
                                            <h2 className="text-2xl font-semibold text-center">
                                                {plan.name}
                                            </h2>
                                            <p className="text-center text-lg">{plan.description}</p>

                                            {/* Displaying benefits for each plan */}
                                            <ul className="mt-4 space-y-2">
                                                {plan.benefits.map((benefit, index) => (
                                                    <li key={index} className="text-sm text-gray-600">
                                                        - {benefit}
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>
                                    ))}
                                </div>
                                <button
                                    onClick={handleSubscription}
                                    disabled={!selectedPlan || loading}
                                    className={`mt-6 w-full py-3 text-white font-semibold rounded-lg transition duration-300
                                        ${selectedPlan ? "bg-blue-600 hover:bg-blue-700" : "bg-gray-400 cursor-not-allowed"}`}
                                >
                                    {loading ? 'Loading...' : `Přihlásit se k odběru ${selectedPlan?.name || "plánu"} plánu`}
                                </button>
                            </div>

                            {clientSecret && (
                                <Elements stripe={stripePromise}>
                                    <h2 className="text-xl font-semibold mb-4">Dokončete nákup</h2>
                                    <form
                                        onSubmit={async (e) => {
                                            e.preventDefault();
                                            if (!clientSecret) return;

                                            const stripe = window.Stripe(
                                                'pk_test_51QBYEjLFUGYLkGp3LhYWs61r3Zf4aEhPxVbmOYOrOMAf5x5HRwNII7UH2syHlAV6OCk28NOm5UYz0u7L9chrGjYz00hjNE2fQa'
                                            );
                                            const { error } = await stripe.confirmCardPayment(clientSecret, {
                                                payment_method: {
                                                    card: window.elements.getElement(CardElement),
                                                }
                                            });

                                            if (error) {
                                                setPaymentStatus(`Error: ${error.message}`);
                                            } else {
                                                setPaymentStatus('Payment successful!');
                                            }
                                        }}
                                    >
                                        <CardElement className="border p-2 rounded-md" />
                                        <button
                                            type="submit"
                                            disabled={loading || !clientSecret}
                                            className="w-full bg-blue-600 text-white py-3 mt-4 rounded-md"
                                        >
                                            Dokončit objednávku
                                        </button>
                                    </form>
                                </Elements>
                            )}

                            {paymentStatus && (
                                <p className="text-center mt-4">{paymentStatus}</p>
                            )}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

const Payment = () => (
    <Elements stripe={stripePromise}>
        <SubscriptionOptions />
    </Elements>
);

export default Payment;
