import React, { useState } from 'react';
import { ChevronDown } from 'lucide-react';

// Komponenty pro další "stránky" budou importovány zde

const App = () => {
    const [currentPage, setCurrentPage] = useState('landing');

    const renderPage = () => {
        switch (currentPage) {
            case 'landing':
                return <LandingPage setCurrentPage={setCurrentPage} />;
            // Další case statements pro další stránky budou přidány zde
            default:
                return <LandingPage setCurrentPage={setCurrentPage} />;
        }
    };

    return (
        <div className="min-h-screen bg-white text-gray-800">
            {renderPage()}
            </div>
    );
};

const LandingPage = ({ setCurrentPage }) => {
    return (
        <div className="min-h-screen flex flex-col">
        <header className="bg-white shadow-sm">
        <nav className="container mx-auto px-4 py-4 flex justify-between items-center">
        <div className="text-2xl font-bold text-blue-900">MemoryCapsule</div>
        <div>
        <button onClick={() => setCurrentPage('login')} className="bg-blue-900 text-white px-4 py-2 rounded-lg mr-2">Přihlásit se</button>
    <button onClick={() => setCurrentPage('register')} className="border border-blue-900 text-blue-900 px-4 py-2 rounded-lg">Vytvořit účet</button>
    </div>
    </nav>
    </header>

    <main className="flex-grow">
    <section className="bg-blue-50 py-20">
    <div className="container mx-auto px-4 text-center">
    <h1 className="text-4xl md:text-5xl font-bold text-blue-900 mb-6">Uchovejte své vzpomínky v čase</h1>
    <p className="text-xl mb-8">Vytvořte digitální časové kapsle a otevřete je, když nastane ten pravý okamžik.</p>
    <button onClick={() => setCurrentPage('register')} className="bg-blue-900 text-white px-6 py-3 rounded-lg text-lg">Začít zdarma</button>
    </div>
    </section>

    <section className="py-16">
    <div className="container mx-auto px-4">
    <h2 className="text-3xl font-bold text-center mb-12">Jak to funguje</h2>
    <div className="grid md:grid-cols-3 gap-8">
        {[
                { title: 'Vytvořte', description: 'Nahrajte fotky, videa nebo zprávy do vaší kapsle.' },
    { title: 'Nastavte', description: 'Zvolte datum, kdy se kapsle otevře.' },
    { title: 'Sdílejte', description: 'Pozvěte přátele a rodinu, aby přispěli do vaší kapsle.' }
].map((step, index) => (
        <div key={index} className="text-center">
    <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
    <span className="text-2xl font-bold text-blue-900">{index + 1}</span>
        </div>
        <h3 className="text-xl font-semibold mb-2">{step.title}</h3>
        <p>{step.description}</p>
        </div>
))}
    </div>
    </div>
    </section>

    <section className="bg-blue-900 text-white py-16">
    <div className="container mx-auto px-4 text-center">
    <h2 className="text-3xl font-bold mb-8">Připraveni začít svou cestu časem?</h2>
        <button onClick={() => setCurrentPage('create-capsule')} className="bg-white text-blue-900 px-6 py-3 rounded-lg text-lg">Vytvořit kapsli nyní</button>
    </div>
    </section>
    </main>

    <footer className="bg-gray-100 py-8">
    <div className="container mx-auto px-4 text-center">
        <p>&copy; 2024 MemoryCapsule. Všechna práva vyhrazena.</p>
    </div>
    </footer>
    </div>
);
};

export default App;