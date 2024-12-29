import {
    Clock,
    Plus,
    Lock,
    Unlock,
    Share2,
    Search,
    ChevronDown,
    Settings,
    User,
    CircleDollarSign,
    Ban,
    Pencil, UserSearch
} from 'lucide-react';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Dashboard = ({ user }) => {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const [filterStatus, setFilterStatus] = useState('all');
    const [anchorEl] = useState(null);
    if (!user) {
        if(localStorage.getItem("userId") != null && localStorage.getItem("email") != null) {
            user = {
                email: localStorage.getItem("email"),
                initials: localStorage.getItem("email").charAt(0).toUpperCase(),
            }
        }
    }
    console.log(user)

    // Mock data for demonstration
    const stats = {
        totalCapsules: 0,
        pendingOpen: 0,
        sharedWithMe: 0,
        subscribed: 0,
        subscribing: 0
    };

    const capsules = [

    ];

    // Function to filter capsules based on filterStatus and searchQuery
    const filteredCapsules = capsules.filter((capsule) => {
        const matchesStatus = filterStatus === 'all' || capsule.status === filterStatus;
        const matchesSearch = capsule.title.toLowerCase().includes(searchQuery.toLowerCase());
        return matchesStatus && matchesSearch;
    });


    return (
        <div className="min-h-screen bg-gray-50 overflow-x-hidden max-w-full" >
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex justify-between items-center">
                        <div className="text-2xl font-bold text-blue-900">MemoryCapsule</div>
                        <div className="flex items-center space-x-4">
                            <div className="relative">
                                <button
                                    onClick={() => navigate('/settings', { state: { activeTab: 'connections' } })}
                                    className="p-1 hover:bg-gray-100 rounded-full transition-colors  mx-1"
                                    title="Spravovat sledující"
                                >
                                    <UserSearch size={24} className="text-blue-900" />
                                </button>
                                <button className="p-1 hover:bg-gray-100 rounded-full transition-colors  mx-1" title="Předplatné"
                                    onClick={() => { navigate("/payment") }}>
                                    <CircleDollarSign size={24} className="text-blue-900" />
                                </button>
                            </div>
                            <div className="relative">
                                <button
                                    onClick={() => navigate('/settings')}
                                    className="flex items-center space-x-2 p-1 rounded-full hover:bg-gray-100 transition-colors"
                                    aria-controls={anchorEl ? 'user-menu' : undefined}
                                    aria-haspopup="true"
                                    aria-expanded={anchorEl ? 'true' : undefined}
                                >
                                    <div
                                        className="w-8 h-8 rounded-full bg-blue-900 text-white flex items-center justify-center">
                                        {user.initials}
                                    </div>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            <main className="container px-4 py-8 overflow-x-hidden max-w-full" >
                {/* Welcome Section */}
                <div className="mb-8">
                    <div className="text-2xl font-bold text-grey-900 break-all">
                        {user.email}
                    </div>

                    <p className="text-gray-600">Máte {stats.pendingOpen} kapslí čekajících na otevření</p>
                </div>

                {/* Statistics */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
                    <div className="bg-white p-6 rounded-lg shadow-sm">
                        <div className="text-gray-600 mb-2">Celkem kapslí</div>
                        <div className="text-3xl font-bold text-blue-900">{stats.totalCapsules}</div>
                    </div>
                    <div className="bg-white p-6 rounded-lg shadow-sm">
                        <div className="text-gray-600 mb-2">Čeká na otevření</div>
                        <div className="text-3xl font-bold text-blue-900">{stats.pendingOpen}</div>
                    </div>
                    <div className="bg-white p-6 rounded-lg shadow-sm">
                        <div className="text-gray-600 mb-2">Sdíleno se mnou</div>
                        <div className="text-3xl font-bold text-blue-900">{stats.sharedWithMe}</div>
                    </div>
                </div>

                {/* Search and Filters */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 space-y-4 md:space-y-0">
                    <div className="relative flex-grow max-w-md">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                        <input
                            type="text"
                            placeholder="Hledat kapsle..."
                            className="pl-10 pr-4 py-2 w-full border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-900"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                        />
                    </div>
                    <div className="flex space-x-4">
                        <select
                            className="border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-900"
                            value={filterStatus}
                            onChange={(e) => setFilterStatus(e.target.value)}
                        >
                            <option value="all">Všechny kapsle</option>
                            <option value="pending">Čekající</option>
                            <option value="opened">Otevřené</option>
                        </select>
                        <button className="bg-blue-900 text-white px-4 py-2 rounded-lg flex items-center space-x-2"
                            onClick={() => {
                                navigate('/createCapsule');
                            }}>
                            <Plus size={20} />
                            <span>Nová kapsle</span>
                        </button>
                    </div>
                </div>

                {/* Capsules List */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredCapsules.length > 0 ? (
                        filteredCapsules.map((capsule) => (
                            <div key={capsule.id} className="bg-white rounded-lg shadow-sm overflow-hidden">
                                <div className="relative">
                                    <img
                                        src={capsule.thumbnail ? capsule.thumbnail : "/placeholder.png"}
                                        alt={capsule.title}
                                        className="w-full h-48 object-cover"
                                    />
                                    <div className="absolute top-2 right-2 bg-blue-900 rounded-full p-2">
                                        {(() => {
                                            switch (capsule.status) {
                                                case 'opened':
                                                    return <Unlock size={16} color='white' />;
                                                case 'closed':
                                                    return <Lock size={16}  color='white'/>;
                                                case 'editing':
                                                    return <Pencil size={16} color='white'/>;
                                                default:
                                                    return <Ban size={16} color='white'/>;
                                            }
                                        })()}
                                    </div>
                                </div>
                                <div className="p-4">
                                    <h3 className="font-semibold text-lg mb-2">{capsule.title}</h3>
                                    <div className="flex items-center text-gray-600 text-sm mb-3">
                                        {capsule.openDate !== null ? (
                                            <>
                                                <Clock size={16} className="mr-1" />
                                                <span>Otevření: {new Date(capsule.openDate).toLocaleDateString()}</span>
                                            </>
                                        ) : <></>}
                                    </div>
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center text-gray-600 text-sm">
                                            <Share2 size={16} className="mr-1" />
                                            <span>{capsule.contributorsAmount} přispěvatelů</span>
                                        </div>
                                        <button
                                            className="text-blue-900 hover:text-blue-700 font-medium"
                                            onClick={() => {
                                                setSelectedCapsule(capsule);
                                                navigate(`/capsuleDetail/${capsule.id}`);
                                            }}
                                            aria-label={`Zobrazit detail kapsle ${capsule.title}`}
                                        >
                                            Zobrazit detail
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="text-center py-12">
                            <div className="text-gray-400 mb-4">Zatím nemáte žádné kapsle :(</div>
                            <button className="bg-blue-900 text-white px-6 py-3 rounded-lg flex items-center space-x-2 mx-auto" onClick={() => {
                                navigate('/createCapsule');
                            }}>
                                <Plus size={20} />
                                <span>Vytvořit první kapsli</span>
                            </button>
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
};

export default Dashboard;






