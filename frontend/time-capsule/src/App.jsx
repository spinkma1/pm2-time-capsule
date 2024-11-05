import './App.css'
import React, { useState } from 'react';
import LandingPage from './screens/LandingPage.jsx';
import AuthPages from './screens/AuthPages.jsx';
import Dashboard from './screens/Dashboard.jsx';
import CapsuleDetail from './screens/CapsuleDetail.jsx';
import TermsOfUse from './screens/TermsOfUse.jsx';
import PrivacyPolicy from './screens/PrivacyPolicy.jsx';
import CreateCapsule from './screens/CreateCapsule.jsx';
import CreateCapsuleSteps from './screens/CreateCapsuleSteps.jsx';
import PasswordRecovery from './screens/PasswordRecovery.jsx';



// Komponenty pro další "stránky" budou importovány zde

const App = () => {
    const [currentPage, setCurrentPage] = useState('landing');
    const [user, setUser] = useState(null);
    const [selectedCapsule, setSelectedCapsule] = useState(null);

    const renderPage = () => {
        switch (currentPage) {
            case 'landing':
                return <LandingPage setCurrentPage={setCurrentPage} />;
            // Další case statements pro další stránky budou přidány zde
            case 'login':
            case 'register':
                return <AuthPages setCurrentPage={setCurrentPage} currentPage={currentPage} setUser={setUser} />
            case 'termsOfUse':
                return <TermsOfUse setCurrentPage={setCurrentPage} />;
            case 'passwordRecovery':
                return <PasswordRecovery setCurrentPage={setCurrentPage} />;
            case 'privacyPolicy':
                return <PrivacyPolicy setCurrentPage={setCurrentPage} />;
            case 'createCapsule':
                return <CreateCapsule setCurrentPage={setCurrentPage} />;
            case 'dashboard':
                return (
                    <Dashboard
                        setCurrentPage={setCurrentPage}
                        user={user}
                        setSelectedCapsule={(capsule) => {
                            setSelectedCapsule(capsule);
                            setCurrentPage('capsuleDetail');
                        }}
                    />
                );
            case 'capsuleDetail':
                return (
                    <CapsuleDetail
                        setCurrentPage={() => {
                            setCurrentPage('dashboard');
                            setSelectedCapsule(null);
                        }}
                        capsule={selectedCapsule}
                    />
                );
            case 'createCapsuleSteps':
                return <CreateCapsuleSteps setCurrentPage={setCurrentPage} />;
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

export default App;
