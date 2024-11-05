import React from 'react';
import { Container, Typography } from '@mui/material';
import { ArrowLeft } from 'lucide-react';

const TermsOfUse = ({ setCurrentPage }) => {
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
                    <h2 className="text-xl font-semibold mb-4">Podmínky užití</h2>
                    
                    <h3 className="text-lg font-semibold mb-2">1. Přijetí podmínek</h3>
                    <p className="mb-4">
                        Používáním aplikace MemoryCapsule souhlasíte s těmito podmínkami. Pokud s nimi nesouhlasíte, 
                        nesmíte aplikaci používat.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">2. Popis služby</h3>
                    <p className="mb-4">
                        MemoryCapsule je webová aplikace, která umožňuje uživatelům vytvářet a ukládat digitální časové kapsle. 
                        Uživatelé mohou ukládat zprávy, fotografie, videa a další digitální soubory, které lze otevřít po 
                        uplynutí stanoveného časového období.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">3. Registrace a účet</h3>
                    <p className="mb-4">
                        Pro používání některých funkcí aplikace je nutné se zaregistrovat. Uživatelé se zavazují 
                        poskytnout pravdivé a aktuální informace a chránit své přihlašovací údaje.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">4. Odpovědnost uživatelů</h3>
                    <p className="mb-4">
                        Uživatelé jsou odpovědní za obsah, který do aplikace nahrávají, a musí zajistit, že mají právo 
                        tento obsah sdílet a uchovávat. MemoryCapsule nenese odpovědnost za jakékoli ztráty nebo škody 
                        vyplývající z obsahu nahraného uživateli.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">5. Ochrana soukromí</h3>
                    <p className="mb-4">
                        Vaše soukromí je pro nás důležité. Informace o tom, jak shromažďujeme a používáme vaše osobní údaje, 
                        naleznete v naší <a href="/privacy-policy" className="text-blue-600 hover:underline">Zásadách ochrany soukromí</a>.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">6. Změny podmínek</h3>
                    <p className="mb-4">
                        MemoryCapsule si vyhrazuje právo tyto podmínky kdykoli změnit. O změnách budete informováni prostřednictvím 
                        e-mailu nebo oznámení v aplikaci.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">7. Kontaktní informace</h3>
                    <p className="mb-4">
                        Pokud máte jakékoli dotazy týkající se těchto podmínek, kontaktujte nás prosím na e-mailu: 
                        support@memorycapsule.com.
                    </p>

                    <h3 className="text-lg font-semibold mb-2">8. Platnost</h3>
                    <p className="mb-4">
                        Tyto podmínky nabývají účinnosti dnem, kdy je začnete používat.
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

export default TermsOfUse;

