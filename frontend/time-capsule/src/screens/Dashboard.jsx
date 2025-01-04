import {
    Clock,
    Plus,
    Lock,
    Unlock,
    Share2,
    Search,
    Star,
    CircleDollarSign,
    Ban,
    Pencil, UserSearch
} from 'lucide-react';
import React, {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import {ApiService} from "../api/api.js";

const Dashboard = ({ user }) => {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const [filterStatus, setFilterStatus] = useState('ALL');
    const [anchorEl] = useState(null);
    const [userRole, setUserRole] = useState(null); // New state for user role
    const [capsules, setCapsules] = useState([]);
    const [pendingOpen,setPendingOpen] = useState([]);
    const [contributorCapsules, setContributorCapsules] = useState([]);
    const [combinedCapsules, setCombinedCapsules] = useState([]);
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({
        totalCapsules: 0,
        pendingOpen: 0,
        sharedWithMe: 0,
        subscribed: 0,
        subscribing: 0
    });
    if (!user) {
        const storedUserEmail = localStorage.getItem("email");
        if (localStorage.getItem("userId") && storedUserEmail) {
            user = {
                email: storedUserEmail,
                initials: storedUserEmail.charAt(0).toUpperCase(),
            };
        }
    }





    // Fetch capsules and contributor capsules
    useEffect(() => {
        const fetchUserRole = async () => {
            setLoading(true);
            try {
                const response = await ApiService.getUserProfile();
                setUserRole(response.role);
            } catch (error) {
                console.error('Error fetching user profile:', error);
            }
        };

        const fetchCapsules = async () => {
            try {
                const [ownedCapsules, contributorCapsules] = await Promise.all([
                    ApiService.getCapsules(),
                    ApiService.getContributorCapsules(),
                ]);

                console.log("Owned Capsules:", ownedCapsules);
                console.log("Contributor Capsules:", contributorCapsules);

                setCapsules(ownedCapsules);
                setContributorCapsules(contributorCapsules);
                setCombinedCapsules([...ownedCapsules, ...contributorCapsules])
            } catch (error) {
                console.error('Error fetching capsules:', error);
            } finally {
                setLoading(false);
            }

        };

        fetchUserRole();
        fetchCapsules();
    }, []);

    // Calculate stats whenever capsules or contributorCapsules change
    useEffect(() => {
        const combinedCapsules = [...capsules, ...contributorCapsules];
        console.log("Combined Capsules:", combinedCapsules);

        const pendingOpen = combinedCapsules.filter(capsule => capsule.state === 'WAIT').length;
        const sharedWithMe = contributorCapsules.length;

        setStats({
            totalCapsules: combinedCapsules.length,
            pendingOpen: pendingOpen,
            sharedWithMe: sharedWithMe,
            subscribed: 0, // Placeholder
            subscribing: 0, // Placeholder
        });

        console.log("Stats Updated:", {
            totalCapsules: combinedCapsules.length,
            pendingOpen: pendingOpen,
            sharedWithMe: sharedWithMe,
        });
    }, [capsules, contributorCapsules]);

    // Function to filter capsules based on filterStatus and searchQuery
    const filteredCapsules = combinedCapsules.filter((capsule) => {
        const matchesStatus = filterStatus === 'ALL' || capsule.state === filterStatus;
        const matchesSearch = capsule.name.toLowerCase().includes(searchQuery.toLowerCase());
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
                                    onClick={() => navigate('/settings', {state: {activeTab: 'connections'}})}
                                    className="p-1 hover:bg-gray-100 rounded-full transition-colors  mx-1"
                                    title="Spravovat sledující"
                                >
                                    <UserSearch size={24} className="text-blue-900"/>
                                </button>
                                <button className="p-1 hover:bg-gray-100 rounded-full transition-colors  mx-1"
                                        title="Předplatné"
                                        onClick={() => {
                                            navigate("/payment")
                                        }}>
                                    <CircleDollarSign size={24} className="text-blue-900"/>
                                </button>
                                {userRole === 'ROLE_ADMIN' && ( // Render based on userRole state
                                    <button
                                        className="p-1 hover:bg-gray-100 rounded-full transition-colors mx-1"
                                        title="Administrace"
                                        onClick={() => navigate('/adminDashboard')}
                                    >
                                        <Star size={24} className="text-blue-900" />
                                    </button>
                                )}
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
                            <option value="ALL">Všechny kapsle</option>
                            <option value="EDIT">Čekající</option>
                            <option value="WAIT">Uzamčené</option>
                            <option value="OPEN">Otevřené</option>
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
                    {loading ? (
                        <div className="flex justify-center items-center h-64">
                            <div className="w-10 h-10 border-4 border-t-blue-900 border-gray-200 rounded-full animate-spin"></div>
                        </div>
                    ) : filteredCapsules.length > 0 ? (
                        filteredCapsules.map((capsule) => (
                            <div key={capsule.id} className="bg-white rounded-lg shadow-sm overflow-hidden">
                                <div className="relative">
                                    <img
                                        src={capsule.thumbnail ? capsule.thumbnail : "/placeholder.png"}
                                        alt={capsule.name}
                                        className="w-full h-48 object-cover"
                                    />
                                    <div className="absolute top-2 right-2 bg-blue-900 rounded-full p-2">
                                        {(() => {
                                            switch (capsule.state) {
                                                case 'OPEN':
                                                    return <Unlock size={16} color='white' />;
                                                case 'WAIT':
                                                    return <Lock size={16}  color='white'/>;
                                                case 'EDIT':
                                                    return <Pencil size={16} color='white'/>;
                                                default:
                                                    return <Ban size={16} color='white'/>;
                                            }
                                        })()}
                                    </div>
                                </div>
                                <div className="p-4">
                                    <h3 className="font-semibold text-lg mb-2">{capsule.name}</h3>
                                    <div className="flex items-center text-gray-600 text-sm mb-3">
                                        {capsule.unlockTime !== null ? (
                                            <>
                                                <Clock size={16} className="mr-1" />
                                                <span>
  Otevření: {new Date(capsule.unlockTime).toLocaleDateString()}
</span>

                                            </>
                                        ) : <></>}
                                    </div>
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center text-gray-600 text-sm">
                                            <Share2 size={16} className="mr-1" />
                                            <span>{(capsule.users && capsule.users.length > 0) ? capsule.users.length : 0} přispěvatelů</span>

                                        </div>
                                        <button
                                            className="text-blue-900 hover:text-blue-700 font-medium"
                                            onClick={() => {
                                                navigate(`/capsuleDetail/${capsule.id}`);
                                            }}
                                            aria-label={`Zobrazit detail kapsle ${capsule.name}`}
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






