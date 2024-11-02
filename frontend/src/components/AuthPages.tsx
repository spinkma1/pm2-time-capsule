import React, { useState } from 'react';
import { Eye, EyeOff, Mail, Lock, User, ArrowLeft } from 'lucide-react';

const AuthPages = ({ setCurrentPage }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [showPassword, setShowPassword] = useState(false);

    // Form states
    const [loginForm, setLoginForm] = useState({
        email: '',
        password: '',
    });

    const [registerForm, setRegisterForm] = useState({
        name: '',
        email: '',
        password: '',
        confirmPassword: '',
    });

    // Error states
    const [errors, setErrors] = useState({});

    const validateForm = (formData, isLoginForm = true) => {
        const newErrors = {};

        if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Zadejte platnou e-mailovou adresu';
        }

        if (!formData.password || formData.password.length < 8) {
            newErrors.password = 'Heslo musí mít alespoň 8 znaků';
        }

        if (!isLoginForm) {
            if (!formData.name || formData.name.length < 2) {
                newErrors.name = 'Jméno musí mít alespoň 2 znaky';
            }

            if (formData.password !== formData.confirmPassword) {
                newErrors.confirmPassword = 'Hesla se neshodují';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = isLogin ? loginForm : registerForm;

        if (validateForm(formData, isLogin)) {
            console.log('Form is valid', formData);
            // Zde by byla implementace přihlášení/registrace
            setCurrentPage('dashboard');
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col">
            {/* Header with logo */}
            <div className="bg-white shadow-sm py-4 px-6">
                <div className="max-w-md mx-auto flex items-center justify-between">
                    <div className="text-2xl font-bold text-blue-900">MemoryCapsule</div>
                    <button
                        onClick={() => setCurrentPage('landing')}
                        className="flex items-center text-gray-600 hover:text-blue-900"
                    >
                        <ArrowLeft size={20} className="mr-2" />
                        Zpět na hlavní stránku
                    </button>
                </div>
            </div>

            {/* Main content */}
            <div className="flex-grow flex items-center justify-center p-4">
                <div className="bg-white rounded-lg shadow-lg p-8 w-full max-w-md">
                    {/* Toggle buttons */}
                    <div className="flex mb-8 bg-gray-100 rounded-lg p-1">
                        <button
                            onClick={() => setIsLogin(true)}
                            className={`flex-1 py-2 rounded-md transition-colors ${
                                isLogin
                                    ? 'bg-white shadow-sm text-blue-900 font-semibold'
                                    : 'text-gray-600'
                            }`}
                        >
                            Přihlášení
                        </button>
                        <button
                            onClick={() => setIsLogin(false)}
                            className={`flex-1 py-2 rounded-md transition-colors ${
                                !isLogin
                                    ? 'bg-white shadow-sm text-blue-900 font-semibold'
                                    : 'text-gray-600'
                            }`}
                        >
                            Registrace
                        </button>
                    </div>

                    {/* Form title */}
                    <h1 className="text-2xl font-bold text-gray-900 mb-6">
                        {isLogin ? 'Vítejte zpět!' : 'Vytvořit nový účet'}
                    </h1>

                    {/* Form */}
                    <form onSubmit={handleSubmit} className="space-y-4">
                        {!isLogin && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Jméno
                                </label>
                                <div className="relative">
                                    <User size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="text"
                                        value={registerForm.name}
                                        onChange={(e) => setRegisterForm({...registerForm, name: e.target.value})}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${
                                            errors.name ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                        placeholder="Zadejte své jméno"
                                    />
                                </div>
                                {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name}</p>}
                            </div>
                        )}

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                E-mail
                            </label>
                            <div className="relative">
                                <Mail size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                <input
                                    type="email"
                                    value={isLogin ? loginForm.email : registerForm.email}
                                    onChange={(e) => {
                                        if (isLogin) {
                                            setLoginForm({...loginForm, email: e.target.value});
                                        } else {
                                            setRegisterForm({...registerForm, email: e.target.value});
                                        }
                                    }}
                                    className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${
                                        errors.email ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                    placeholder="vase@email.cz"
                                />
                            </div>
                            {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email}</p>}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Heslo
                            </label>
                            <div className="relative">
                                <Lock size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    value={isLogin ? loginForm.password : registerForm.password}
                                    onChange={(e) => {
                                        if (isLogin) {
                                            setLoginForm({...loginForm, password: e.target.value});
                                        } else {
                                            setRegisterForm({...registerForm, password: e.target.value});
                                        }
                                    }}
                                    className={`w-full pl-10 pr-12 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${
                                        errors.password ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                    placeholder="••••••••"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                >
                                    {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                                </button>
                            </div>
                            {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password}</p>}
                        </div>

                        {!isLogin && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Potvrzení hesla
                                </label>
                                <div className="relative">
                                    <Lock size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type={showPassword ? 'text' : 'password'}
                                        value={registerForm.confirmPassword}
                                        onChange={(e) => setRegisterForm({...registerForm, confirmPassword: e.target.value})}
                                        className={`w-full pl-10 pr-12 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${
                                            errors.confirmPassword ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                        placeholder="••••••••"
                                    />
                                </div>
                                {errors.confirmPassword && <p className="text-red-500 text-sm mt-1">{errors.confirmPassword}</p>}
                            </div>
                        )}

                        {isLogin && (
                            <div className="flex justify-end">
                                <button
                                    type="button"
                                    className="text-sm text-blue-900 hover:underline"
                                >
                                    Zapomněli jste heslo?
                                </button>
                            </div>
                        )}

                        <button
                            type="submit"
                            className="w-full bg-blue-900 text-white py-2 rounded-lg hover:bg-blue-800 transition-colors"
                        >
                            {isLogin ? 'Přihlásit se' : 'Vytvořit účet'}
                        </button>
                    </form>

                    {/* Additional info */}
                    {!isLogin && (
                        <p className="mt-4 text-sm text-gray-600 text-center">
                            Registrací souhlasíte s našimi{' '}
                            <button className="text-blue-900 hover:underline">
                                podmínkami použití
                            </button>{' '}
                            a{' '}
                            <button className="text-blue-900 hover:underline">
                                zásadami ochrany soukromí
                            </button>
                        </p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AuthPages;