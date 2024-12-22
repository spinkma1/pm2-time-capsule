import React, {useEffect, useState} from 'react';
import ProfileSection from './ProfileSection';
import {useLocation, useNavigate} from 'react-router-dom';
import {
    User,
    Mail,
    Lock,
    Bell,
    Users,
    Shield,
    LogOut,
    ArrowLeft,
    Trash2
} from 'lucide-react';
import SecuritySection from "./SecuritySection.jsx";
import ConnectionsSection from "./ConnectionSection.jsx";
import {ApiService} from "../../api/api.js";

const Settings = ({ user, setUser }) => {
    const location = useLocation();
    const navigate = useNavigate();
    const [userData, setUserData] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState(
        location.state?.activeTab || 'profile'
    );

    useEffect(() => {
        if (location.state?.activeTab) {
            setActiveTab(location.state.activeTab);
        }
    }, [location.state]);

    // Načtení dat uživatele
    useEffect(() => {
        const fetchUserData = async () => {
            setIsLoading(true);
            try {
                const data = await ApiService.getUserProfile();
                setUserData(data);
                setError(null);
            } catch (error) {
                console.error('Failed to fetch user data:', error);
                setError('Nepodařilo se načíst data uživatele');
            } finally {
                setIsLoading(false);
            }
        };

        fetchUserData();
    }, []);

    const handleNavigateBack = () => {
        navigate('/dashboard');
    };

    /*
    const handleProfileUpdate = async (updatedData) => {
        try {
            const updatedUser = await ApiService.updateProfile(updatedData);
            setUserData(prev => ({
                ...prev,
                ...updatedUser
            }));
            // Můžete přidat notifikaci o úspěšné aktualizaci
        } catch (error) {
            console.error('Failed to update profile:', error);
            // Můžete přidat notifikaci o chybě
        }
    };

     */

    const menuItems = [
        { id: 'profile', label: 'Profil', icon: <User size={20} /> },
        { id: 'security', label: 'Zabezpečení', icon: <Lock size={20} /> },
        { id: 'connections', label: 'Sledující', icon: <Users size={20} /> },
    ];

    if (isLoading) {
        return <div className="flex justify-center items-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-900"></div>
        </div>;
    }

    /*
    if (error) {
        return <div className="flex justify-center items-center min-h-screen text-red-600">
            {error}
        </div>;
    }

     */

    const handleLogout = async () => {
        // Zavolat BE endpoint pro vyčištění session
        await fetch('/api/user/logout', {
            method: 'POST',
            credentials: 'include'
        });

        // Vyčistit lokální storage/cookies
        localStorage.clear();

        // Přesměrovat na login stránku
        navigate('/');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <button
                        onClick={handleNavigateBack}
                        className="flex items-center text-gray-600 hover:text-blue-900"
                    >
                        <ArrowLeft size={20} className="mr-2" />
                        Zpět na přehled
                    </button>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                <div className="max-w-6xl mx-auto">
                    <div className="flex flex-col md:flex-row gap-6">
                        {/* Sidebar */}
                        <div className="w-full md:w-64 bg-white rounded-lg shadow-sm p-4">
                            <nav>
                                {menuItems.map((item) => (
                                    <button
                                        key={item.id}
                                        onClick={() => setActiveTab(item.id)}
                                        className={`w-full flex items-center space-x-2 px-4 py-2 rounded-lg mb-1
                                            ${activeTab === item.id
                                            ? 'bg-blue-50 text-blue-900'
                                            : 'text-gray-600 hover:bg-gray-50'}`}
                                    >
                                        {item.icon}
                                        <span>{item.label}</span>
                                    </button>
                                ))}
                                <div className="border-t border-gray-200 my-4"></div>
                                <button
                                    onClick={() => setActiveTab('delete-account')}
                                    className="w-full flex items-center space-x-2 px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg"
                                >
                                    <Trash2 size={20} />
                                    <span>Smazat účet</span>
                                </button>
                                <button
                                    onClick={() => handleLogout()}
                                    className="w-full flex items-center space-x-2 px-4 py-2 text-gray-600 hover:bg-gray-50 rounded-lg"
                                >
                                    <LogOut size={20} />
                                    <span>Odhlásit se</span>
                                </button>
                            </nav>
                        </div>

                        {/* Main Content */}
                        <div className="flex-1 bg-white rounded-lg shadow-sm p-6">
                            <div className="max-w-2xl mx-auto">
                                {activeTab === 'profile' && (
                                    <ProfileSection
                                        user={userData}
                                        //onUpdate={handleProfileUpdate}
                                    />
                                )}
                                {activeTab === 'security' && (
                                    <SecuritySection

                                    />
                                )}
                                {activeTab === 'connections' && (
                                    <ConnectionsSection

                                    />
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default Settings;