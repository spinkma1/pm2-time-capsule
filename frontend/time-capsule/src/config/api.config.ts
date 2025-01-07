const API_CONFIG = {

    BASE_URL: 'http://130.61.191.237:8080',
    // BASE_URL: 'https://localhost:8080',

    ENDPOINTS: {
        HEALTH: '/health',
        AUTH: {
            LOGIN: '/auth/login',
            REGISTER: '/auth/register',
            REFRESH: '/auth/refresh',
            LOGINSSO: '/auth/login/sso',
            REGISTERSSO: '/auth/register/sso',
        },
        USER: {
            PROFILE: '/user/profile',
            UPDATE: '/user/update'
        },
        CREATE: {
            CREATE: '/capsules/create',
        }
    },

    TIMEOUT: 5000,

    HEADERS: {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
};

export default API_CONFIG;
