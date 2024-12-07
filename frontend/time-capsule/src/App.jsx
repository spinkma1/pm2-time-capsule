import './App.css';
import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// Screens
import LandingPage from './screens/LandingPage';
import AuthPages from './screens/authentication/AuthPages';
import Dashboard from './screens/Dashboard';
import CapsuleDetail from './screens/capsuleDetail/CapsuleDetail';
import TermsOfUse from './screens/gdpr/TermsOfUse';
import PrivacyPolicy from './screens/gdpr/PrivacyPolicy';
import CreateCapsule from './screens/capsuleCreation/CreateCapsule';
import PasswordRecovery from './screens/authentication/PasswordRecovery';
import QRGenerator from './screens/capsuleCreation/QRGenerator';
import FilesInput from './screens/capsuleDetail/FilesInput';
import Settings from './screens/settings/Settings';
import Payment from './screens/stripe/Payment';
import AddContributors from './screens/capsuleCreation/AddContributors';
import User from './screens/authentication/User';
import OpenCapsule from './screens/capsuleCreation/OpenCapsule';

import AdminDashboard from './components/admin/AdminDashboard.jsx';
import AdminUserDetail from './screens/admin/AdminUserDetail.jsx';
import AdminCapsuleDetail from './screens/admin/AdminCapsuleDetail.jsx';

import { GoogleMapsProvider } from './components/context/GoogleProvider';
import HealthCheck from './components/HealthCheck';

const App = () => {
    const [user, setUser] = useState(null);
    const [selectedCapsule, setSelectedCapsule] = useState(null);

    return (
        <GoogleMapsProvider>
            <Router>
                <div className="min-h-screen bg-white text-gray-800">
                    <Routes>
                        <Route path="/" element={<LandingPage />} />
                        <Route path="/health" element={<HealthCheck />} />
                        <Route path="/login" element={<AuthPages currentPage="login" setUser={setUser} />} />
                        <Route path="/register" element={<AuthPages currentPage="register" setUser={setUser} />} />
                        <Route path="/termsOfUse" element={<TermsOfUse />} />
                        <Route path="/passwordRecovery" element={<PasswordRecovery />} />
                        <Route path="/privacyPolicy" element={<PrivacyPolicy />} />
                        <Route path="/createCapsule" element={<CreateCapsule />} />
                        <Route path="/dashboard" element={<Dashboard user={user} setSelectedCapsule={setSelectedCapsule} />} />
                        <Route path="/settings" element={<Settings user={user} setUser={setUser} />} />
                        <Route path="/capsuleDetail/:id" element={<CapsuleDetail capsule={selectedCapsule} />} />
                        <Route path="/qrcode" element={<QRGenerator />} />
                        <Route path="/addFiles" element={<FilesInput capsule={selectedCapsule} setSelectedCapsule={setSelectedCapsule} />} />
                        <Route path="/payment" element={<Payment />} />
                        <Route path="/adminDashboard" element={<AdminDashboard />} />
                        <Route path="/admin/user/:userId" element={<AdminUserDetail />} />
                        <Route path="/admin/capsule/:capsuleId" element={<AdminCapsuleDetail />} />
                        <Route path="/addContributors" element={<AddContributors capsule={selectedCapsule} />} />
                        <Route path="/user/:id" element={<User />} />
                        <Route path="/capsule/open/:id" element={<OpenCapsule />} />
                    </Routes>
                </div>
            </Router>
        </GoogleMapsProvider>
    );
};

export default App;

