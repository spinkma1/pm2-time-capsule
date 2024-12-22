import React, {useEffect, useState} from 'react';
import { User, Mail, Camera, Lock } from 'lucide-react';
import {ApiService} from "../../api/api.js";
import { Alert, Snackbar } from '@mui/material';

const ProfileSection = ({ user, onUpdate }) => {
    const [showEmailChange, setShowEmailChange] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        currentEmail: '',
        newEmail: '',
        emailPassword: '',
        bio: ''
    });
    const [notification, setNotification] = useState({
        open: false,
        message: '',
        severity: 'success' // 'error', 'warning', 'info', 'success'
    });

    useEffect(() => {
        const loadUserProfile = async () => {
            try {
                const userData = await ApiService.getUserProfile();
                console.log('Received user data:', userData); // Ponechme pro debug

                // Přidejme log jednotlivých vlastností
                console.log('Name:', userData.name);
                console.log('Email:', userData.email);
                console.log('Bio:', userData.bio);

                // Zkontrolujme, zda jsou data v nějakém vnořeném objektu
                console.log('Full userData structure:', JSON.stringify(userData, null, 2));

                // Upravme nastavení formData podle skutečné struktury dat
                setFormData(prevData => ({
                    ...prevData,
                    name: userData?.name ?? '',
                    currentEmail: userData?.email ?? '',
                    bio: userData?.bio ?? ''
                }));
            } catch (error) {
                console.error('Failed to fetch user data:', error);
                setNotification({
                    open: true,
                    message: 'Nepodařilo se načíst data uživatele',
                    severity: 'error'
                });
            }
        };

        loadUserProfile();
    }, []);

    const [errors, setErrors] = useState({});

    const validateEmailChange = () => {
        const newErrors = {};
        if (!formData.emailPassword) {
            newErrors.emailPassword = 'Pro změnu e-mailu zadejte heslo';
        }
        if (!formData.newEmail) {
            newErrors.newEmail = 'Zadejte nový e-mail';
        } else if (!/\S+@\S+\.\S+/.test(formData.newEmail)) {
            newErrors.newEmail = 'Zadejte platný e-mail';
        }
        if (formData.newEmail === formData.currentEmail) {
            newErrors.newEmail = 'Nový e-mail musí být jiný než současný';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleEmailChange = async () => {
        if (validateEmailChange()) {
            try {
                await ApiService.changeEmail({
                    newEmail: formData.newEmail,
                    password: formData.emailPassword
                });

                setFormData(prev => ({
                    ...prev,
                    currentEmail: formData.newEmail,
                    emailPassword: '',
                    newEmail: ''
                }));

                setShowEmailChange(false);
                setNotification({
                    open: true,
                    message: 'E-mail byl úspěšně změněn',
                    severity: 'success'
                });

                // Zde můžete přidat logiku pro přesměrování na login, pokud je potřeba

                // eslint-disable-next-line no-unused-vars
            } catch (error) {
                setNotification({
                    open: true,
                    message: 'Došlo k chybě při změně emailu. Zkontrolujte heslo.',
                    severity: 'error'
                });
            }
        }
    };

    const handleProfileUpdate = async () => {
        try {
            await ApiService.changeProfile({
                name: formData.name,
                bio: formData.bio
            });

            setNotification({
                open: true,
                message: 'Profil byl úspěšně aktualizován',
                severity: 'success'
            });
        } catch (error) {
            setNotification({
                open: true,
                message: 'Došlo k chybě při aktualizaci profilu',
                severity: 'error'
            });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault(); // Zabrání refresh stránky

        if (showEmailChange) {
            await handleEmailChange();
            await handleProfileUpdate();
        } else {
            await handleProfileUpdate();
        }
    };

    const handleCloseNotification = () => {
        setNotification({ ...notification, open: false });
    };

    return (
        <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-6">Nastavení profilu</h2>
            {/* Snackbar pro notifikace */}
            <Snackbar
                open={notification.open}
                autoHideDuration={6000}
                onClose={handleCloseNotification}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            >
                <Alert
                    onClose={handleCloseNotification}
                    severity={notification.severity}
                    sx={{ width: '100%' }}
                >
                    {notification.message}
                </Alert>
            </Snackbar>

            {/* Profile Picture Section
            <div className="flex items-center mb-8">
                <div className="relative">
                    <div className="w-24 h-24 bg-blue-900 rounded-full flex items-center justify-center text-white text-2xl">
                        {user?.initials || 'JD'}
                    </div>
                    <button className="absolute bottom-0 right-0 bg-white rounded-full p-2 shadow-lg hover:bg-gray-50">
                        <Camera size={20} className="text-gray-600" />
                    </button>
                </div>
                <div className="ml-6">
                    <h3 className="font-medium text-gray-900">Profilový obrázek</h3>
                    <p className="text-sm text-gray-500">
                        Nahrajte svůj profilový obrázek nebo použijte výchozí iniciály
                    </p>
                </div>
            </div>
            */}
            {/* Profile Form */}
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* Name */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Jméno
                    </label>
                    <div className="relative">
                        <User size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"/>
                        <input
                            type="text"
                            value={formData.name}
                            onChange={(e) => setFormData({...formData, name: e.target.value})}
                            className="pl-10 pr-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                            placeholder="Vaše jméno"
                        />
                    </div>
                </div>

                {/* Current Email */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        E-mailová adresa
                    </label>
                    <div className="relative">
                        <Mail size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"/>
                        <input
                            type="email"
                            value={formData.currentEmail}
                            disabled
                            className="pl-10 pr-28 py-2 w-full border border-gray-300 rounded-lg bg-gray-50"
                        />
                        <button
                            type="button"
                            onClick={() => setShowEmailChange(!showEmailChange)}
                            className="absolute right-2 top-1/2 transform -translate-y-1/2 px-4 py-1 text-sm text-blue-900 hover:text-blue-700"
                        >
                            {showEmailChange ? 'Zrušit' : 'Změnit'}
                        </button>
                    </div>
                </div>

                {/* Email Change Form */}
                {showEmailChange && (
                    <div className="space-y-4 bg-gray-50 p-4 rounded-lg">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Nový e-mail
                            </label>
                            <div className="relative">
                                <Mail size={20}
                                      className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"/>
                                <input
                                    type="email"
                                    value={formData.newEmail}
                                    onChange={(e) => setFormData({...formData, newEmail: e.target.value})}
                                    className={`pl-10 pr-4 py-2 w-full border ${errors.newEmail ? 'border-red-500' : 'border-gray-300'} rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900`}
                                    placeholder="novy@email.cz"
                                />
                            </div>
                            {errors.newEmail && <p className="text-red-500 text-sm mt-1">{errors.newEmail}</p>}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Pro potvrzení zadejte heslo
                            </label>
                            <div className="relative">
                                <Lock size={20}
                                      className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"/>
                                <input
                                    type="password"
                                    value={formData.emailPassword}
                                    onChange={(e) => setFormData({...formData, emailPassword: e.target.value})}
                                    className={`pl-10 pr-4 py-2 w-full border ${errors.emailPassword ? 'border-red-500' : 'border-gray-300'} rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900`}
                                    placeholder="••••••••"
                                />
                            </div>
                            {errors.emailPassword &&
                                <p className="text-red-500 text-sm mt-1">{errors.emailPassword}</p>}
                        </div>
                    </div>
                )}

                {/* Bio */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        O mně
                    </label>
                    <textarea
                        value={formData.bio}
                        onChange={(e) => setFormData({...formData, bio: e.target.value})}
                        rows={4}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                        placeholder="Napište něco o sobě..."
                    />
                </div>

                {/* Submit Button */}
                <div className="flex justify-end">
                    <button
                        type="submit"
                        className="px-6 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                    >
                        {showEmailChange ? 'Uložit změny včetně e-mailu' : 'Uložit změny'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ProfileSection;