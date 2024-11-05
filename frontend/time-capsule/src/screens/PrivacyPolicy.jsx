import React from 'react';
import { Container, Typography } from '@mui/material';
import { ArrowLeft } from 'lucide-react';

const PrivacyPolicy = ({ setCurrentPage }) => {
    return (
        <div className="min-h-screen flex flex-col bg-white text-gray-800">
            {/* Header */}
            <div className="bg-white shadow-sm py-4 px-6 flex flex-col md:flex-row md:justify-between relative">
                <button
                    onClick={() => setCurrentPage('register')}
                    className="absolute top-4 left-6 flex items-center text-gray-600 hover:text-blue-900 md:relative md:top-0 md:left-0"
                >
                    <ArrowLeft size={20} className="mr-2" />
                    Zpět
                </button>
                <div className="flex justify-center md:justify-start mt-4 md:mt-0">
                    <div className="text-2xl font-bold text-blue-900">MemoryCapsule</div>
                </div>
            </div>

            {/* Main Content */}
            <main className="flex-grow p-6">
                <section className="max-w-3xl mx-auto">
                    <h2 className="text-xl font-semibold mb-4">Zásady ochrany soukromí</h2>
                    
                    <h3 className="text-lg font-semibold mb-2">1. Úvod</h3>
                    <p className="mb-4">
                        Tyto zásady ochrany soukromí popisují, jakým způsobem shromažďujeme, používáme a chráníme vaše osobní údaje 
                        při používání aplikace MemoryCapsule. Používáním naší aplikace souhlasíte s tímto zpracováním.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">2. Jaké osobní údaje shromažďujeme</h3>
                    <p className="mb-4">
                        Shromažďujeme osobní údaje, které nám poskytujete při registraci a používání aplikace, včetně:
                        <ul className="list-disc list-inside mb-4">
                            <li>Jméno a příjmení</li>
                            <li>Emailová adresa</li>
                            <li>Heslo</li>
                            <li>Další údaje, které nám dobrovolně poskytnete</li>
                        </ul>
                    </p>

                    <h3 className="text-lg font-semibold mb-2">3. Jak používáme vaše údaje</h3>
                    <p className="mb-4">
                        Vaše osobní údaje používáme k:
                        <ul className="list-disc list-inside mb-4">
                            <li>Poskytování a zlepšování našich služeb</li>
                            <li>Komunikaci s vámi</li>
                            <li>Správě vašeho účtu</li>
                            <li>Odesílání upozornění a marketingových materiálů</li>
                        </ul>
                    </p>

                    <h3 className="text-lg font-semibold mb-2">4. Ochrana vašich údajů</h3>
                    <p className="mb-4">
                        Přijímáme vhodná bezpečnostní opatření, abychom chránili vaše osobní údaje před ztrátou, zneužitím nebo 
                        neoprávněným přístupem. Vaše heslo je šifrováno a uchováváme ho v bezpečí.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">5. Sdílení údajů s třetími stranami</h3>
                    <p className="mb-4">
                        Vaše osobní údaje nebudeme sdílet s třetími stranami, pokud k tomu nebudeme mít váš souhlas, 
                        nebo pokud to nebude nezbytné pro poskytování našich služeb.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">6. Vaše práva</h3>
                    <p className="mb-4">
                        Máte právo požadovat přístup k vašim osobním údajům, jejich opravu, vymazání nebo omezení zpracování. 
                        Můžete také kdykoli odvolat svůj souhlas se zpracováním údajů.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">7. Změny zásad ochrany soukromí</h3>
                    <p className="mb-4">
                        Tyto zásady můžeme čas od času aktualizovat. O případných změnách vás budeme informovat 
                        prostřednictvím e-mailu nebo oznámení v aplikaci.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">8. Kontaktní informace</h3>
                    <p className="mb-4">
                        Pokud máte jakékoli dotazy ohledně těchto zásad ochrany soukromí, neváhejte nás kontaktovat na e-mailu: 
                        support@memorycapsule.com.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">9. Platnost</h3>
                    <p className="mb-4">
                        Tyto zásady ochrany soukromí nabývají účinnosti dnem, kdy je začnete používat.
                    </p>
                </section>
            </main>

            {/* Footer */}
            <footer className="bg-gray-100 py-8">
                <Container>
                    <Typography align="center">
                        &copy; 2024 MemoryCapsule. Všechna práva vyhrazena.
                    </Typography>
                </Container>
            </footer>
        </div>
    );
};

export default PrivacyPolicy;
