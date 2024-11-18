import React, { useState } from 'react';
import { Lock, Shield, Eye, EyeOff } from 'lucide-react';

const SecuritySection = ({ user, onUpdate }) => {
    const [showCurrentPassword, setShowCurrentPassword] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [formData, setFormData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmNewPassword: ''
    });
    const [errors, setErrors] = useState({});

    const validatePasswords = () => {
        const newErrors = {};
        if (!formData.currentPassword) {
            newErrors.currentPassword = 'Zadejte současné heslo';
        }
        if (!formData.newPassword) {
            newErrors.newPassword = 'Zadejte nové heslo';
        } else if (formData.newPassword.length < 8) {
            newErrors.newPassword = 'Heslo musí mít alespoň 8 znaků';
        }
        if (formData.newPassword !== formData.confirmNewPassword) {
            newErrors.confirmNewPassword = 'Hesla se neshodují';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (validatePasswords()) {
            onUpdate(formData);
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-6">Zabezpečení</h2>

            {/* Password Change Form */}
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* Current Password */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Současné heslo
                    </label>
                    <div className="relative">
                        <Lock size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                        <input
                            type={showCurrentPassword ? "text" : "password"}
                            value={formData.currentPassword}
                            onChange={(e) => setFormData({ ...formData, currentPassword: e.target.value })}
                            className={`pl-10 pr-12 py-2 w-full border ${errors.currentPassword ? 'border-red-500' : 'border-gray-300'} rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900`}
                        />
                        <button
                            type="button"
                            onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                        >
                            {showCurrentPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                        </button>
                    </div>
                    {errors.currentPassword && <p className="text-red-500 text-sm mt-1">{errors.currentPassword}</p>}
                </div>

                {/* New Password */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Nové heslo
                    </label>
                    <div className="relative">
                        <Lock size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                        <input
                            type={showNewPassword ? "text" : "password"}
                            value={formData.newPassword}
                            onChange={(e) => setFormData({ ...formData, newPassword: e.target.value })}
                            className={`pl-10 pr-12 py-2 w-full border ${errors.newPassword ? 'border-red-500' : 'border-gray-300'} rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900`}
                        />
                        <button
                            type="button"
                            onClick={() => setShowNewPassword(!showNewPassword)}
                            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                        >
                            {showNewPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                        </button>
                    </div>
                    {errors.newPassword && <p className="text-red-500 text-sm mt-1">{errors.newPassword}</p>}
                </div>

                {/* Confirm New Password */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Potvrdit nové heslo
                    </label>
                    <div className="relative">
                        <Lock size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                        <input
                            type={showNewPassword ? "text" : "password"}
                            value={formData.confirmNewPassword}
                            onChange={(e) => setFormData({ ...formData, confirmNewPassword: e.target.value })}
                            className={`pl-10 pr-12 py-2 w-full border ${errors.confirmNewPassword ? 'border-red-500' : 'border-gray-300'} rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900`}
                        />
                    </div>
                    {errors.confirmNewPassword && <p className="text-red-500 text-sm mt-1">{errors.confirmNewPassword}</p>}
                </div>

                {/* Submit Button */}
                <div className="flex justify-end">
                    <button
                        type="submit"
                        className="px-6 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                    >
                        Změnit heslo
                    </button>
                </div>
            </form>
        </div>
    );
};

export default SecuritySection;