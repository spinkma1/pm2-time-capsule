import React, { useState } from 'react';
import { User, UserMinus, Search, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';


const ConnectionsSection = ({ user }) => {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [isSearching, setIsSearching] = useState(false);

    // Mock data pro sledující (v reálné aplikaci by byla data načtena z API)
    const [followers] = useState([
        { id: 1, name: 'Jan Novák', email: 'jan.novak@email.cz', avatar: 'JN' },
        { id: 2, name: 'Marie Svobodová', email: 'marie.s@email.cz', avatar: 'MS' },
        { id: 3, name: 'Petr Dvořák', email: 'petr.dvorak@email.cz', avatar: 'PD' },
    ]);

    const [following] = useState([
        { id: 4, name: 'Jana Malá', email: 'jana.mala@email.cz', avatar: 'JM' },
        { id: 5, name: 'Tomáš Veselý', email: 'tomas.v@email.cz', avatar: 'TV' },
    ]);

    const handleUnfollow = (userId) => {
        // TODO: Implementovat unfollow logiku
        console.log('Unfollow user:', userId);
    };

    const filteredFollowers = followers.filter(
        f => f.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            f.email.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const filteredFollowing = following.filter(
        f => f.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            f.email.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const searchUsers = (query) => {
        // Mock data pro vyhledávání
        const allUsers = [
            { id: 1, name: 'Jana Nováková', email: 'jana@email.cz', avatar: 'JN', capsules: 5 },
            { id: 2, name: 'Petr Svoboda', email: 'petr@email.cz', avatar: 'PS', capsules: 3 },
            // ... další uživatelé
        ];

        return allUsers.filter(user =>
            user.name.toLowerCase().includes(query.toLowerCase()) ||
            user.email.toLowerCase().includes(query.toLowerCase())
        );
    };

    const handleSearch = (e) => {
        const query = e.target.value;
        setSearchQuery(query);

        if (query.length >= 2) {
            setIsSearching(true);
            // V reálné aplikaci by zde bylo volání API s debounce
            const results = searchUsers(query);
            setSearchResults(results);
        } else {
            setIsSearching(false);
            setSearchResults([]);
        }
    };

    const handleNavigateToFollower = (id) => {
        navigate(`/user/${id}`);
    };


    return (
        <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-6">Sledující</h2>

            {/* Search Box with Results */}
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

                {/* Search Results */}
                {isSearching && searchResults.length > 0 && (
                    <div className="mt-2 bg-white rounded-lg shadow-lg border border-gray-200 max-h-80 overflow-y-auto">
                        {searchResults.map((result) => (
                            <div
                                key={result.id}
                                className="p-4 hover:bg-gray-50 flex items-center justify-between border-b last:border-b-0"
                            >
                                <div className="flex items-center space-x-3">
                                    <div className="w-10 h-10 bg-blue-900 rounded-full flex items-center justify-center text-white">
                                        {result.avatar}
                                    </div>
                                    <div>
                                        <div className="font-medium">{result.name}</div>
                                        <div className="text-sm text-gray-500">
                                            {result.capsules} kapslí
                                        </div>
                                    </div>
                                </div>
                                <button
                                    onClick={() => handleFollow(result.id)}
                                    className="px-4 py-1 border border-blue-900 text-blue-900 rounded-full hover:bg-blue-50"
                                >
                                    Sledovat
                                </button>
                            </div>
                        ))}
                    </div>
                )}

                {isSearching && searchResults.length === 0 && (
                    <div className="mt-2 p-4 bg-gray-50 rounded-lg text-center text-gray-500">
                        Žádní uživatelé nenalezeni
                    </div>
                )}
            </div>

            {/* Followers Section */}
            <div className="mb-8">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">
                    Sledující ({followers.length})
                </h3>
                <div className="space-y-4">
                    {filteredFollowers.map((follower) => (
                        <div key={follower.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                            <div className="flex items-center space-x-4">
                                <div className="w-10 h-10 bg-blue-900 rounded-full flex items-center justify-center text-white">
                                    {follower.avatar}
                                </div>
                                <div>
                                    <div className="font-medium text-gray-900">{follower.name}</div>
                                    <div className="text-sm text-gray-500">{follower.email}</div>
                                </div>
                            </div>
                            <button className="text-gray-400 hover:text-gray-600"
                                onClick={() => handleNavigateToFollower(follower.id)} >
                                <ChevronRight size={16} />
                            </button>
                        </div>
                    ))}
                </div>
            </div>

            {/* Following Section */}
            <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">
                    Sleduji ({following.length})
                </h3>
                <div className="space-y-4">
                    {filteredFollowing.map((followedUser) => (
                        <div key={followedUser.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                            <div className="flex items-center space-x-4">
                                <div className="w-10 h-10 bg-blue-900 rounded-full flex items-center justify-center text-white">
                                    {followedUser.avatar}
                                </div>
                                <div>
                                    <div className="font-medium text-gray-900">{followedUser.name}</div>
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