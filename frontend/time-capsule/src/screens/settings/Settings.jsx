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

const Settings = ({ user, setUser }) => {
    const location = useLocation();
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState(
        location.state?.activeTab || 'profile'
    );

    useEffect(() => {
        if (location.state?.activeTab) {
            setActiveTab(location.state.activeTab);
        }
    }, [location.state]);

    const handleNavigateBack = () => {
        navigate('/dashboard');
    };

    const menuItems = [
        { id: 'profile', label: 'Profil', icon: <User size={20} /> },
        { id: 'security', label: 'Zabezpečení', icon: <Lock size={20} /> },
        { id: 'connections', label: 'Sledující', icon: <Users size={20} /> },
    ];

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
                                    onClick={() => navigate('/')}
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
                                        user={user}
                                        onUpdate={(updatedData) => {
                                            console.log('Updating profile:', updatedData);
                                            // TODO: Implement profile update logic
                                        }}
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