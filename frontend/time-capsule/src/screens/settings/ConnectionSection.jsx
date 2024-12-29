import React, { useState, useEffect } from 'react';
import { User, UserMinus, Search, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { ApiService } from "../../api/api.js";
import { Alert, Snackbar } from '@mui/material';

const ConnectionsSection = () => {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [isSearching, setIsSearching] = useState(false);
    const [followers, setFollowers] = useState([]);
    const [following, setFollowing] = useState([]);
    const [error, setError] = useState(null);
    const [refreshTrigger, setRefreshTrigger] = useState(0);
    const [currentUserId, setCurrentUserId] = useState(null);


    // Notifikace
    const [notification, setNotification] = useState({
        open: false,
        message: '',
        severity: 'success'
    });

    useEffect(() => {
        const loadUserData = async () => {
            try {
                const userData = await ApiService.getUserProfile();
                setCurrentUserId(userData.id);
            } catch (error) {
                console.error('Failed to load user data:', error);
            }
        };
        loadUserData();
        loadConnections();
    },[refreshTrigger]); // Přidejte závislost na refreshTrigger

    const isFollowing = (userId) => {
        return following.some(user => user.id === userId);
    };

    const loadConnections = async () => {
        try {
            // Načtení sledujících a sledovaných paralelně
            const [followersData, followingData] = await Promise.all([
                ApiService.getFollowers(),
                ApiService.getFollowing()
            ]);

            setFollowers(followersData || []);
            setFollowing(followingData || []);
        } catch (error) {
            console.error('Failed to load connections:', error);
            setError('Nepodařilo se načíst data o sledování');
        }
    };

    const handleUnfollow = async (userId) => {
        try {
            const userData = await ApiService.getUserProfile();
            const followerId = userData.id;
            await ApiService.unfollowUser(userId, followerId);
            // Optimistická aktualizace UI
            setFollowing(prev => prev.filter(user => user.id !== userId));
            setRefreshTrigger(prev => prev + 1);
            setNotification({
                open: true,
                message: 'Sledování uživatele bylo úspěšně zrušeno',
                severity: 'success'
            });
        } catch (error) {
            setNotification({
                open: true,
                message: 'Nepodařilo se přestat sledovat uživatele',
                severity: 'error'
            });
        }
    };

    const handleFollow = async (userId) => {
        try {
            await ApiService.followUser(userId);
            // Aktualizace seznamu sledovaných
            const updatedFollowing = await ApiService.getFollowing();
            setFollowing(updatedFollowing);
            setRefreshTrigger(prev => prev + 1);
            setSearchQuery('');
            setSearchResults([]);
            setNotification({
                open: true,
                message: 'Začali jste sledovat uživatele',
                severity: 'success'
            });
        } catch (error) {
            setNotification({
                open: true,
                message: 'Nepodařilo se začít sledovat uživatele',
                severity: 'error'
            });
        }
    };

    const handleSearch = (e) => {
        const query = e.target.value;
        setSearchQuery(query);

        if (query.length >= 2) {
            // Odstraníme nastavování isSearching na true/false zde
            const timeoutId = setTimeout(async () => {
                try {
                    const results = await ApiService.searchUsers(query);
                    setSearchResults(results);
                } catch (error) {
                    console.error('Failed to search users:', error);
                    setNotification({
                        open: true,
                        message: 'Nepodařilo se vyhledat uživatele',
                        severity: 'error'
                    });
                }
            }, 300);

            return () => clearTimeout(timeoutId);
        } else {
            setSearchResults([]);
        }
    };

    const handleCloseNotification = () => {
        setNotification({ ...notification, open: false });
    };

    if (error) {
        return (
            <div className="p-4 text-red-600 bg-red-50 rounded-lg">
                {error}
            </div>
        );
    }

    const renderSearchResults = () => {
        if (searchQuery.length >= 2 && searchResults.length > 0) {
            return (
                <div className="mt-2 bg-white rounded-lg shadow-lg border border-gray-200 max-h-80 overflow-y-auto">
                    {searchResults.map((result) => {
                        const alreadyFollowing = isFollowing(result.id);
                        const isCurrentUser = result.id === currentUserId;

                        return (
                            <div key={result.id}
                                 className="p-4 hover:bg-gray-50 flex items-center justify-between border-b last:border-b-0">
                                <div className="flex items-center space-x-3">
                                    <div className="w-10 h-10 bg-blue-900 rounded-full flex items-center justify-center text-white">
                                        {result.email?.substring(0, 2).toUpperCase()}
                                    </div>
                                    <div>
                                        <div className="font-medium">{result.name || result.email}</div>
                                        <div className="text-sm text-gray-500">{result.email}</div>
                                    </div>
                                </div>
                                {!isCurrentUser && (
                                    <button
                                        onClick={() => alreadyFollowing ? handleUnfollow(result.id) : handleFollow(result.id)}
                                        className={`px-4 py-1 border rounded-full ${
                                            alreadyFollowing
                                                ? 'border-red-600 text-red-600 hover:bg-red-50'
                                                : 'border-blue-900 text-blue-900 hover:bg-blue-50'
                                        }`}
                                    >
                                        {alreadyFollowing ? 'Přestat sledovat' : 'Sledovat'}
                                    </button>
                                )}
                            </div>
                        );
                    })}
                </div>
            );
        }
        return null;
    };

    return (
        <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-6">Sledující</h2>

            {/* Notifikace */}
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

            {/* Vyhledávací pole */}
            <div className="mb-8">
                <div className="relative">
                    <Search size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                    <input
                        type="text"
                        placeholder="Najít nové uživatele ke sledování..."
                        value={searchQuery}
                        onChange={handleSearch}
                        className="pl-10 pr-4 py-3 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                    />
                </div>
                {renderSearchResults()}
            </div>

            {/* Seznam sledujících */}
            <div className="mb-8">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">
                    Sledující ({followers.length})
                </h3>
                <div className="space-y-4">
                    {followers.map((follower) => (
                        <div key={follower.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                            <div className="flex items-center space-x-4">
                                <div className="w-10 h-10 bg-blue-900 rounded-full flex items-center justify-center text-white">
                                    {follower.email.substring(0, 2).toUpperCase()}
                                </div>
                                <div>
                                    <div className="font-medium text-gray-900">{follower.name || follower.email}</div>
                                    <div className="text-sm text-gray-500">{follower.email}</div>
                                </div>
                            </div>
                            <button
                                className="text-gray-400 hover:text-gray-600"
                                onClick={() => navigate(`/user/${follower.id}`)}
                            >
                                <ChevronRight size={16} />
                            </button>
                        </div>
                    ))}
                </div>
            </div>

            {/* Seznam sledovaných */}
            <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">
                    Sleduji ({following.length})
                </h3>
                <div className="space-y-4">
                    {following.map((followedUser) => (
                        <div key={followedUser.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                            <div className="flex items-center space-x-4">
                                <div className="w-10 h-10 bg-blue-900 rounded-full flex items-center justify-center text-white">
                                    {followedUser.email.substring(0, 2).toUpperCase()}
                                </div>
                                <div>
                                    <div className="font-medium text-gray-900">{followedUser.name || followedUser.email}</div>
                                    <div className="text-sm text-gray-500">{followedUser.email}</div>
                                </div>
                            </div>
                            <button
                                onClick={() => handleUnfollow(followedUser.id)}
                                className="text-gray-600 hover:text-red-600"
                            >
                                <UserMinus size={20} />
                            </button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default ConnectionsSection;