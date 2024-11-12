import './App.css';
import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// Screens
import LandingPage from './screens/LandingPage';
import AuthPages from './screens/AuthPages';
import Dashboard from './screens/Dashboard';
import CapsuleDetail from './screens/CapsuleDetail';
import TermsOfUse from './screens/TermsOfUse';
import PrivacyPolicy from './screens/PrivacyPolicy';
import CreateCapsule from './screens/capsule_creation/CreateCapsule';
import PasswordRecovery from './screens/PasswordRecovery';

const App = () => {
    const [user, setUser] = useState(null); // Any type for user, can be replaced with more specific type
    const [selectedCapsule, setSelectedCapsule] = useState(null); // Any type for selectedCapsule

    return (
        <Router>
            <div className="min-h-screen bg-white text-gray-800">
                <Routes>
                    <Route path="/" element={<LandingPage setCurrentPage={() => {}} />} />
                    <Route path="/login" element={<AuthPages setCurrentPage={() => {}} currentPage="login" setUser={setUser} />} />
                    <Route path="/register" element={<AuthPages setCurrentPage={() => {}} currentPage="register" setUser={setUser} />} />
                    <Route path="/termsOfUse" element={<TermsOfUse setCurrentPage={() => {}} />} />
                    <Route path="/passwordRecovery" element={<PasswordRecovery setCurrentPage={() => {}} />} />
                    <Route path="/privacyPolicy" element={<PrivacyPolicy setCurrentPage={() => {}} />} />
                    <Route path="/createCapsule" element={<CreateCapsule setCurrentPage={() => {}} />} />
                    <Route path="/dashboard" element={<Dashboard setCurrentPage={() => {}} user={user} setSelectedCapsule={setSelectedCapsule} />} />
                    <Route path="/capsuleDetail" element={<CapsuleDetail setCurrentPage={() => {}} capsule={selectedCapsule} />} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;

