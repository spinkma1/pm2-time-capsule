const API_CONFIG = {
    /*
    BASE_URL: import.meta.env.MODE === 'development'
        ? '/api'  // Bude použito s proxy v development módu
        : 'http://actual-production-url.com/api',  // Produkční URL

     */
    BASE_URL: '/api',

    ENDPOINTS: {
        HEALTH: '/health',
        AUTH: {
            LOGIN: '/auth/login',
            REGISTER: '/auth/register',
            REFRESH: '/auth/refresh'
        },
        USER: {
            PROFILE: '/user/profile',
            UPDATE: '/user/update'
        }
    },

    TIMEOUT: 5000,

    HEADERS: {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
};

export default API_CONFIG;