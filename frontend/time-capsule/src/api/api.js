import API_CONFIG from "../config/api.config";

const fetchWithConfig = async (endpoint, options = {}, noBody = false) => {
  const url = `${API_CONFIG.BASE_URL}${endpoint}`;
  const accessToken = localStorage.getItem("access_token");

  const headers = {
    ...API_CONFIG.HEADERS,
    ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
    ...(options.headers || {}),
  };

  const defaultOptions = { ...options, headers };

  try {
    const response = await fetch(url, defaultOptions);
    if (!response.ok) {
      if (response.status === 401) {
        const refreshToken = localStorage.getItem("refresh_token");
        if (refreshToken && endpoint !== "/user/refresh") {
          const authResponse = await refreshTokenApi(refreshToken);
          if (authResponse) {
            localStorage.setItem("access_token", authResponse.accessToken);
            localStorage.setItem("refresh_token", authResponse.refreshToken);
            return fetchWithConfig(endpoint, options, noBody);
          } else {
            handleLogout();
            throw new Error("Session expired");
          }
        } else {
          handleLogout();
          throw new Error("Unauthorized access");
        }
      }
      if (response.status === 403) {
        console.error("Forbidden access. Token:", accessToken);
        throw new Error("Forbidden access - check permissions");
      }
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    if (response.status === 204) return true;
    return noBody ? undefined : await response.json();
  } catch (error) {
    console.error("API call failed:", error);
    throw error;
  }
};

const handleLogout = () => {
  localStorage.removeItem("access_token");
  localStorage.removeItem("refresh_token");
  window.location.href = "/login";
};

const refreshTokenApi = async (refreshToken) => {
  try {
    const response = await fetchWithConfig(`/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    });
    return response;
  } catch (error) {
    console.error("Failed to refresh token:", error);
    return null;
  }
};

// API functions
export const ApiService = {
  login: async (email, password) => {
    try {
      const response = await fetchWithConfig("/user/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({email, password}),
      });
      if (response) {
        localStorage.setItem("access_token", response.accessToken);
        localStorage.setItem("refresh_token", response.refreshToken);
        console.log("Login successful:", response)
        console.log("Access token:", response.accessToken)
        console.log("Refresh token:", response.refreshToken)
      }
      return response;
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  },

  register: async (email, password) => {
    try {
      const response = await fetchWithConfig("/user/register", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({email, password}),
      });
      return response;
    } catch (error) {
      console.error("Registration failed:", error);
      throw error;
    }
  },

  createCapsule: async (capsuleData) => {
    try {
      const response = await fetchWithConfig("/capsules/create", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(capsuleData),
      });

      if (!response.ok) {
        const error = await response.json();
        console.error("Failed to create capsule:", error);
        throw new Error(error.message || "Unknown error occurred");
      }

      return await response.json();
    } catch (error) {
      console.error("Capsule creation failed:", error);
      throw error;
    }
  },
  loginWithGoogle: async  (token) => {
    // Input validation
    console.log("Attempting login with token:", token);

    if (typeof token !== 'string') {
      console.error("Error: The token is not a valid string:", token);
      throw new Error("Invalid token format");
    }

    try {
      const response = await fetchWithConfig("/user/login/sso", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      // Your fetchWithConfig already returns the parsed JSON
      console.log("Login response:", response);

      if (response.email) {
        // Successful login - response contains user data
        return response;
      } else {
        throw new Error(response.message || "Authentication failed");
      }

    } catch (error) {
      console.error("Google login failed:", error);
      throw error;
    }
  }
};
