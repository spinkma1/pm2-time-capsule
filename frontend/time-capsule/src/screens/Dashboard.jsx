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
} from 'lucide-react';
import React, { useState } from 'react';
import { Menu, MenuItem, Button } from '@mui/material';

const Dashboard = ({ setCurrentPage, user, setSelectedCapsule }) => {
    const [searchQuery, setSearchQuery] = useState('');
    const [filterStatus, setFilterStatus] = useState('all');
    const [anchorEl, setAnchorEl] = useState(null); // State for managing menu anchor

    console.log(user);

    // Mock data for demonstration
    const stats = {
        totalCapsules: 8,
        pendingOpen: 4,
        sharedWithMe: 2
    };

    const capsules = [
        {
            id: 1,
            title: "Maturitní vzpomínky 2024",
            openDate: "2025-06-30",
            status: "pending",
            contributors: 5,
            thumbnail: null,
            type: "own"
        },
        {
            id: 2,
            title: "Naše svatba",
            openDate: "2024-12-24",
            status: "pending",
            contributors: 8,
            thumbnail: "/api/placeholder/320/180",
            type: "own"
        },
        {
            id: 3,
            title: "Rodinná historie",
            openDate: "2024-11-15",
            status: "opened",
            contributors: 3,
            thumbnail: "/api/placeholder/320/180",
            type: "shared"
        },
        {
            id: 4,
            title: "Cestování po Evropě",
            openDate: "2025-05-01",
            status: "pending",
            contributors: 4,
            thumbnail: "/api/placeholder/320/180",
            type: "own"
        },
        {
            id: 5,
            title: "Vánoční oslavy 2024",
            openDate: "2024-12-25",
            status: "pending",
            contributors: 6,
            thumbnail: "/api/placeholder/320/180",
            type: "own"
        },
        {
            id: 6,
            title: "Dovolená na Bali",
            openDate: "2025-02-15",
            status: "opened",
            contributors: 7,
            thumbnail: "/api/placeholder/320/180",
            type: "shared"
        },
        {
            id: 7,
            title: "Historie rodiny Nováků",
            openDate: "2024-10-10",
            status: "opened",
            contributors: 2,
            thumbnail: "/api/placeholder/320/180",
            type: "shared"
        },
        {
            id: 8,
            title: "Naše první dítě",
            openDate: "2025-03-20",
            status: "pending",
            contributors: 1,
            thumbnail: null,
            type: "own"
        }
    ];

    // Function to filter capsules based on filterStatus and searchQuery
    const filteredCapsules = capsules.filter((capsule) => {
        const matchesStatus = filterStatus === 'all' || capsule.status === filterStatus;
        const matchesSearch = capsule.title.toLowerCase().includes(searchQuery.toLowerCase());
        return matchesStatus && matchesSearch;
    });

    // Menu items
    const menuItems = [
        { title: 'Profil', icon: <User size={16} /> },
        { title: 'Nastavení', icon: <Settings size={16} /> },
        { title: 'Odhlásit se', icon: <Lock size={16} /> }
    ];

    const handleClick = (event) => {
        if (anchorEl) {
            // If menu is open, close it
            setAnchorEl(null);
        } else {
            // If menu is closed, open it
            setAnchorEl(event.currentTarget);
        }
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    return (
        <div className="min-h-screen bg-gray-50" >
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <div className="flex justify-between items-center">
                        <div className="text-2xl font-bold text-blue-900">MemoryCapsule</div>
                        <div className="flex items-center space-x-4">
                            <button className="p-2 text-gray-600 hover:text-blue-900" aria-label="Notifications">
                                <CircleDollarSign size={24} />
                            </button>
                            <div className="flex items-center space-x-2 relative">
                                <div className="w-8 h-8 rounded-full bg-blue-900 text-white flex items-center justify-center">
                                    {user.initials}
                                </div>
                                <Button
                                    onClick={handleClick}
                                    aria-controls={Boolean(anchorEl) ? 'user-menu' : undefined}
                                    aria-haspopup="true"
                                    aria-expanded={Boolean(anchorEl) ? 'true' : undefined}
                                >
                                    <ChevronDown size={16} />
                                </Button>
                                <Menu
                                    anchorEl={anchorEl}
                                    open={Boolean(anchorEl)}
                                    onClose={handleClose}
                                    MenuListProps={{
                                        'aria-labelledby': 'basic-button',
                                    }}
                                    className="transform -translate-x-10" // Posun menu vlevo
                                >
                                    {menuItems.map((item, index) => (
                                        <MenuItem key={index} onClick={handleClose} className="flex items-center">
                                            {item.icon}
                                            <span className="ml-2">{item.title}</span>
                                        </MenuItem>
                                    ))}
                                </Menu>
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                {/* Welcome Section */}
                <div className="mb-8">
                    <h1 className="text-2xl font-bold text-gray-900">Vítejte zpět, {user.email}!</h1>
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
                                    setCurrentPage('createCapsule');
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
                                    <div className="absolute top-2 right-2 bg-white rounded-full p-2">
                                        {capsule.status === 'pending' ? <Lock size={16} /> : <Unlock size={16} />}
                                    </div>
                                </div>
                                <div className="p-4">
                                    <h3 className="font-semibold text-lg mb-2">{capsule.title}</h3>
                                    <div className="flex items-center text-gray-600 text-sm mb-3">
                                        <Clock size={16} className="mr-1" />
                                        <span>Otevření: {new Date(capsule.openDate).toLocaleDateString()}</span>
                                    </div>
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center text-gray-600 text-sm">
                                            <Share2 size={16} className="mr-1" />
                                            <span>{capsule.contributors} přispěvatelů</span>
                                        </div>
                                        <button 
                                            className="text-blue-900 hover:text-blue-700 font-medium"
                                            onClick={() => {
                                                setSelectedCapsule(capsule); 
                                                setCurrentPage('capsuleDetail');
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
                            <button className="bg-blue-900 text-white px-6 py-3 rounded-lg flex items-center space-x-2 mx-auto">
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






