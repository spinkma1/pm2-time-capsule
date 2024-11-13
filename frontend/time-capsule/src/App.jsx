import './App.css';
import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// Screens
import LandingPage from './screens/LandingPage';
import AuthPages from './screens/AuthPages';
import Dashboard from './screens/Dashboard';
import CapsuleDetail from './screens/capsuleDetail/CapsuleDetail';
import TermsOfUse from './screens/TermsOfUse';
import PrivacyPolicy from './screens/PrivacyPolicy';
import CreateCapsule from './screens/capsuleCreation/CreateCapsule';
import PasswordRecovery from './screens/PasswordRecovery';
import QRGenerator from './screens/capsuleCreation/QRGenerator';
import FilesInput from './screens/capsuleDetail/FilesInput';

const App = () => {
    const [user, setUser] = useState(null); // Any type for user, can be replaced with more specific type
    const [selectedCapsule, setSelectedCapsule] = useState(null); // Any type for selectedCapsule

    return (
        <Router>
            <div className="min-h-screen bg-white text-gray-800">
                <Routes>
                    <Route path="/" element={<LandingPage />} />
                    <Route path="/login" element={<AuthPages currentPage="login" setUser={setUser} />} />
                    <Route path="/register" element={<AuthPages currentPage="register" setUser={setUser} />} />
                    <Route path="/termsOfUse" element={<TermsOfUse  />} />
                    <Route path="/passwordRecovery" element={<PasswordRecovery />} />
                    <Route path="/privacyPolicy" element={<PrivacyPolicy  />} />
                    <Route path="/createCapsule" element={<CreateCapsule  />} />
                    <Route path="/dashboard" element={<Dashboard  user={user} setSelectedCapsule={setSelectedCapsule} />} />
                    <Route path="/capsuleDetail" element={<CapsuleDetail  capsule={selectedCapsule} />} />
                    <Route path="/qrcode" element={<QRGenerator />} />
                    <Route path="/addFiles" element={<FilesInput capsule={selectedCapsule} setSelectedCapsule={setSelectedCapsule}/>} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;

