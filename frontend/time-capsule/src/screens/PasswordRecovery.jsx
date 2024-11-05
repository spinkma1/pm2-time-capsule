import React, { useState } from 'react';
import { Mail, ArrowLeft } from 'lucide-react';

const PasswordRecovery = ({ setCurrentPage }) => {
    const [email, setEmail] = useState('');
    const [emailError, setEmailError] = useState('');
    
    const handleSubmit = (e) => {
        e.preventDefault();
        if (!email || !/\S+@\S+\.\S+/.test(email)) {
            setEmailError('Zadejte platnou e-mailovou adresu');
            return;
        }
        setEmailError('');
        // handle the password recovery logic
        console.log('Password recovery email sent to:', email);
        // redirect the user
    };

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col relative">
            <div className="bg-white shadow-sm py-4 px-6 flex flex-col md:flex-row md:justify-between relative">
                <button
                    onClick={() => setCurrentPage('login')}
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
                    <h1 className="text-2xl font-bold text-gray-900 mb-6">
                        Obnova hesla
                    </h1>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">E-mail</label>
                            <div className="relative">
                                <Mail size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900 ${emailError ? 'border-red-500' : 'border-gray-300'}`}
                                    placeholder="vase@email.cz"
                                />
                            </div>
                            {emailError && <p className="text-red-500 text-sm mt-1">{emailError}</p>}
                        </div>

                        <button
                            type="submit"
                            className="w-full bg-blue-900 text-white py-2 rounded-lg hover:bg-blue-800 transition-colors"
                        >
                            Potvrdit
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default PasswordRecovery;
