import React, { useState, useEffect } from 'react';
import { Eye, EyeOff, Mail, Lock, User, ArrowLeft } from 'lucide-react';
import { jwtDecode } from 'jwt-decode';
import { GoogleLogin } from '@react-oauth/google';


const AuthPages = ({ setCurrentPage, currentPage, setUser }) => {
    const [isLogin, setIsLogin] = useState(currentPage === 'login');
    const [showPassword, setShowPassword] = useState(false);

    useEffect(() => {
        setIsLogin(currentPage === 'login');
    }, [currentPage]);

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
    const [loginErrors, setLoginErrors] = useState({});
    const [registerErrors, setRegisterErrors] = useState({});

    const validateLoginForm = (formData) => {
        const newErrors = {};
        if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Zadejte platnou e-mailovou adresu';
        }
        if (!formData.password || formData.password.length < 8) {
            newErrors.password = 'Heslo musí mít alespoň 8 znaků';
        }
        setLoginErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const validateRegisterForm = (formData) => {
        const newErrors = {};
        if (!formData.name || formData.name.length < 2) {
            newErrors.name = 'Jméno musí mít alespoň 2 znaky';
        }
        if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Zadejte platnou e-mailovou adresu';
        }
        if (!formData.password || formData.password.length < 8) {
            newErrors.password = 'Heslo musí mít alespoň 8 znaků';
        }
        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Hesla se neshodují';
        }
        setRegisterErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const userEmail = isLogin ? loginForm.email : registerForm.email; // Get email based on login state
        const initials = userEmail.split('@')[0].slice(0, 2).toUpperCase(); // Create initials from email

        if (isLogin) {
            if (validateLoginForm(loginForm)) {
                setCurrentPage('dashboard');
                setUser({ email: userEmail, initials });
            }
        } else {
            if (validateRegisterForm(registerForm)) {
                setUser({ email: userEmail, initials });
                setCurrentPage('dashboard');
            }
        }
    };

    const handleGoogleSignIn = (credentialResponse) => {
        const decodedToken = jwtDecode(credentialResponse.credential); 
        const userEmail = decodedToken.email; 
        const initials = userEmail.split('@')[0].slice(0, 2).toUpperCase(); 
        setUser({ email: userEmail, initials });
        setCurrentPage('dashboard');
    };

    // Error handling for Google sign-in
    const handleGoogleError = (error) => {
        console.error('Google sign-in error:', error);
        // Zpracování chyby
    };

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col relative">
            <div className="bg-white shadow-sm py-4 px-6 flex flex-col md:flex-row md:justify-between relative">
                <button
                    onClick={() => setCurrentPage('landing')}
                    className="absolute top-4 left-6 flex items-center text-gray-600 hover:text-blue-900 md:relative md:top-0 md:left-0"
                >
                    <ArrowLeft size={20} className="mr-2" />
                    Zpět na hlavní stránku
                </button>
                <div className="flex justify-center md:justify-start mt-4 md:mt-0">
                    <div className="text-2xl font-bold text-blue-900">MemoryCapsule</div>
                </div>
            </div>

            <div className="flex-grow flex items-center justify-center p-4">
                <div className="bg-white rounded-lg shadow-lg p-8 w-full max-w-md">
                    <div className="flex mb-8 bg-gray-100 rounded-lg p-1">
                        <button
                            onClick={() => setIsLogin(true)}
                            className={`flex-1 py-2 rounded-md transition-colors ${isLogin ? 'bg-white shadow-sm text-blue-900 font-semibold' : 'text-gray-600'}`}
                        >
                            Přihlášení
                        </button>
                        <button
                            onClick={() => setIsLogin(false)}
                            className={`flex-1 py-2 rounded-md transition-colors ${!isLogin ? 'bg-white shadow-sm text-blue-900 font-semibold' : 'text-gray-600'}`}
                        >
                            Registrace
                        </button>
                    </div>

                    <h1 className="text-2xl font-bold text-gray-900 mb-6">
                        {isLogin ? 'Vítejte zpět!' : 'Vytvořit nový účet'}
                    </h1>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        {!isLogin && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Jméno</label>
                                <div className="relative">
                                    <User size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="text"
                                        value={registerForm.name}
                                        onChange={(e) => setRegisterForm({ ...registerForm, name: e.target.value })}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${registerErrors.name ? 'border-red-500' : 'border-gray-300'}`}
                                        placeholder="Zadejte své jméno"
                                    />
                                </div>
                                {registerErrors.name && <p className="text-red-500 text-sm mt-1">{registerErrors.name}</p>}
                            </div>
                        )}

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">E-mail</label>
                            <div className="relative">
                                <Mail size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                <input
                                    type="email"
                                    value={isLogin ? loginForm.email : registerForm.email}
                                    onChange={(e) => {
                                        if (isLogin) {
                                            setLoginForm({ ...loginForm, email: e.target.value });
                                        } else {
                                            setRegisterForm({ ...registerForm, email: e.target.value });
                                        }
                                    }}
                                    className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${isLogin ? (loginErrors.email ? 'border-red-500' : 'border-gray-300') : (registerErrors.email ? 'border-red-500' : 'border-gray-300')}`}
                                    placeholder="vase@email.cz"
                                />
                            </div>
                            {(isLogin ? loginErrors.email : registerErrors.email) && <p className="text-red-500 text-sm mt-1">{isLogin ? loginErrors.email : registerErrors.email}</p>}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Heslo</label>
                            <div className="relative">
                                <Lock size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    value={isLogin ? loginForm.password : registerForm.password}
                                    onChange={(e) => {
                                        if (isLogin) {
                                            setLoginForm({ ...loginForm, password: e.target.value });
                                        } else {
                                            setRegisterForm({ ...registerForm, password: e.target.value });
                                        }
                                    }}
                                    className={`w-full pl-10 pr-12 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${isLogin ? (loginErrors.password ? 'border-red-500' : 'border-gray-300') : (registerErrors.password ? 'border-red-500' : 'border-gray-300')}`}
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
                            {(isLogin ? loginErrors.password : registerErrors.password) && <p className="text-red-500 text-sm mt-1">{isLogin ? loginErrors.password : registerErrors.password}</p>}
                        </div>

                        {!isLogin && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Potvrzení hesla</label>
                                <div className="relative">
                                    <Lock size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type={showPassword ? 'text' : 'password'}
                                        value={registerForm.confirmPassword}
                                        onChange={(e) => setRegisterForm({ ...registerForm, confirmPassword: e.target.value })}
                                        className={`w-full pl-10 pr-12 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${registerErrors.confirmPassword ? 'border-red-500' : 'border-gray-300'}`}
                                        placeholder="••••••••"
                                    />
                                </div>
                                {registerErrors.confirmPassword && <p className="text-red-500 text-sm mt-1">{registerErrors.confirmPassword}</p>}
                            </div>
                        )}

                        {isLogin && (
                            <div className="flex justify-end">
                                <button
                                    type="button"
                                    className="text-sm text-blue-900 hover:underline"
                                    onClick={() => setCurrentPage('passwordRecovery')} 
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

                    {/*Google Sign-In button*/}
                    {isLogin && (
                        <div className="flex justify-center items-center mt-4">
                            <div className="max-w-lg w-full">
                                <GoogleLogin
                                    onSuccess={handleGoogleSignIn}
                                    onError={handleGoogleError}
                                />
                            </div>
                        </div>
                    )}



                    {/* Additional info */}
                    {!isLogin && (
                        <p className="mt-4 text-sm text-gray-600 text-center">
                            Registrací souhlasíte s našimi{' '}
                            <button className="text-blue-900 hover:underline" onClick={() => setCurrentPage('termsOfUse')} >
                                podmínkami použití
                            </button>{' '}
                            a{' '}
                            <button className="text-blue-900 hover:underline" onClick={() => setCurrentPage('privacyPolicy')} >
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
