import React, { useState, useEffect } from 'react';
import { ArrowLeft } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';
import { ApiService } from "../../api/api.js";

// Role mapping
const roleMap = {
    ROLE_ADMIN: 'Administrátor',
    ROLE_REGISTERED: 'Registrovaný',
    ROLE_BANNED: 'Zakázaný',
    ROLE_PREMIUM: 'Prémiový',
    ROLE_DELETED: 'Smazaný'
};

const AdminUserDetail = () => {
    const { userId: email } = useParams(); // Assume userId param is actually email
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [isBlocked, setIsBlocked] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [name, setName] = useState('');
    const [role, setRole] = useState('');
    const [bio, setBio] = useState('');
    const [capsules, setCapsules] = useState([]);  // New state for capsules
    const [followers, setFollowers] = useState([]); // New state for followers

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await ApiService.getUserByEmail(email);
                setUser(response);
                setName(response.name || '');
                setRole(response.role || ''); // Store role as the identifier
                setBio(response.bio || '');
                setCapsules(response.capsules || []); // Set capsules
                setFollowers(response.followers || []); // Set followers
            } catch (error) {
                console.error('Error fetching user data:', error);
            }
        };

        if (email) {
            fetchUserData();
        }
    }, [email]);

    const handleBlockUser = async () => {
        try {
            const updatedUser = { ...user, status: isBlocked ? 'active' : 'blocked' };
            await ApiService.updateUser(updatedUser);
            setIsBlocked(!isBlocked);
        } catch (error) {
            console.error('Error updating user status:', error);
        }
    };

    const handleUpdateUser = async (event) => {
        event.preventDefault();
        try {
            const updatedUser = { ...user, name, role, bio };
            await ApiService.updateUser(updatedUser);
            setUser(updatedUser);
            setIsEditMode(false);
        } catch (error) {
            console.error('Error updating user details:', error);
        }
    };

    return user ? (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex justify-between items-center">
                        <button onClick={() => navigate('/adminDashboard')} className="flex items-center text-gray-600 hover:text-blue-900">
                            <ArrowLeft size={20} className="mr-2" /> Zpět na vyhledávání
                        </button>
                        <div className="text-2xl font-bold text-blue-900">Detail uživatele</div>
                    </div>
                </div>
            </header>
            <main className="container mx-auto px-4 py-8">
                <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                    <h2 className="text-2xl font-bold text-gray-900 mb-4">{user.email}</h2>
                    {isEditMode ? (
                        <form onSubmit={handleUpdateUser} className="grid grid-cols-1 gap-4 text-sm">
                            <div>
                                <label className="text-gray-500">Jméno: </label>
                                <input
                                    type="text"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    className="border border-gray-300 rounded p-2 w-full"
                                />
                            </div>
                            <div>
                                <label className="text-gray-500">Role: </label>
                                <select
                                    value={role}
                                    onChange={(e) => setRole(e.target.value)}
                                    className="border border-gray-300 rounded p-2 w-full"
                                >
                                    {Object.entries(roleMap).map(([key, value]) => (
                                        <option key={key} value={key}>
                                            {value}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <label className="text-gray-500">Bio: </label>
                                <textarea
                                    value={bio}
                                    onChange={(e) => setBio(e.target.value)}
                                    className="border border-gray-300 rounded p-2 w-full"
                                />
                            </div>
                            <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded-lg">
                                Aktualizovat
                            </button>
                        </form>
                    ) : (
                        <div className="grid grid-cols-2 gap-4 text-sm">
                            <div>
                                <span className="text-gray-500">Jméno: </span>
                                <span className="text-gray-800">{name || 'Nevyplněno'}</span>
                            </div>
                            <div>
                                <span className="text-gray-500">Role: </span>
                                <span className="text-gray-800 font-bold">{roleMap[role] || 'Neznámá role'}</span>
                            </div>
                            <div>
                                <span className="text-gray-500">Bio: </span>
                                <span className="text-gray-800">{bio || 'Nevyplněno'}</span>
                            </div>
                        </div>
                    )}
                    <div className="flex space-x-4 mt-6">
                        <button onClick={() => setIsEditMode(!isEditMode)}
                                className="bg-gray-800 text-white px-4 py-2 rounded-lg">
                            {isEditMode ? 'Zrušit úpravy' : 'Editovat'}
                        </button>
                    </div>
                    <div className="border-t pt-6">
                        <h3 className="text-xl font-bold text-gray-900 mb-2">Capsules</h3>
                        <ul className="list-disc list-inside">
                            {capsules.length > 0 ? capsules.map((capsule, index) => (
                                <li key={index} className="text-gray-800">{capsule}</li>
                            )) : <li className="text-gray-800">None</li>}
                        </ul>
                        <h3 className="text-xl font-bold text-gray-900 mt-4 mb-2">Followers</h3>
                        <ul className="list-disc list-inside">
                            {followers.length > 0 ? followers.map((follower, index) => (
                                <li key={index} className="text-gray-800">{follower}</li>
                            )) : <li className="text-gray-800">None</li>}
                        </ul>
                    </div>
                </div>
            </main>
        </div>
    ) : (
        <div>Načítání...</div>
    );
};

export default AdminUserDetail;