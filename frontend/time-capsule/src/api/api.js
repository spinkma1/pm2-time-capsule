import API_CONFIG from "../config/api.config";

const fetchWithConfig = async (endpoint, options = {}, noBody = false) => {
  const url = `${API_CONFIG.BASE_URL}${endpoint}`;
  const accessToken = localStorage.getItem("access_token");
  console.log(accessToken)
  const headers = {
    ...API_CONFIG.HEADERS,
    ...(accessToken && accessToken !== "undefined" ? { Authorization: `Bearer ${accessToken}` } : {}),
    ...(options.headers || {}),
  };
  console.log("Headers:", headers)
  const defaultOptions = { ...options, headers };

  try {
    const response = await fetch(url, defaultOptions);
    if (response.status === 202) { // Accepted status pro smazaný účet
      const text = await response.text();
      if (text.includes("Tento účet byl smazán")) {
        throw new Error("ACCOUNT_DELETED");
      }
    }
    if (!response.ok) {
      if (response.status === 401) {
        const refreshToken = localStorage.getItem("refresh_token");
        if (refreshToken && endpoint !== "/user/refresh") {
          const authResponse = await refreshTokenApi(refreshToken);
          if (authResponse) {
            localStorage.setItem("access_token", authResponse.accessToken);
            localStorage.setItem("refresh_token", authResponse.refreshToken);
            localStorage.setItem("userId", authResponse.id);
            localStorage.setItem("email", email);
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
    if (response.status === 403 && response.statusText === "Tento účet byl smazán. Pro obnovení kontaktujte podporu.") {
        console.error("Account deleted. Token:", accessToken);
        return new Error("Account deleted");
    }
    if (response.status === 204 || response.headers.get("content-length") === "0") {
      return null; // nebo prázdný objekt {}
    }
    return noBody ? undefined : await response.json();
  } catch (error) {
    console.error("API call failed:", error);
    if (error.message === "ACCOUNT_DELETED") {
      throw new Error("ACCOUNT_DELETED");
    }
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
export const  ApiService = {
  login: async (email, password) => {
    try {
      const response = await fetchWithConfig("/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });
      if (response) {
        localStorage.setItem("access_token", response.accessToken);
        localStorage.setItem("refresh_token", response.refreshToken);
        localStorage.setItem("userId", response.id);
        localStorage.setItem("email", email);
        console.log("Login successful:", response)
        console.log("Access token:", response.accessToken)
        console.log("Refresh token:", response.refreshToken)
      }
      return response;
    } catch (error) {
      if (error.message === "ACCOUNT_DELETED") {
        throw new Error("ACCOUNT_DELETED");
      }
      console.error("Login failed:", error);
      throw error;
    }
  },

  register: async (email, password) => {
    try {
      const response = await fetchWithConfig("/user/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });
      if (response) {
        localStorage.setItem("access_token", response.accessToken);
        localStorage.setItem("refresh_token", response.refreshToken);
        localStorage.setItem("userId", response.id);
        localStorage.setItem("email", email);
        console.log("Login successful:", response)
        console.log("Access token:", response.accessToken)
        console.log("Refresh token:", response.refreshToken)
      }
      return response;
    } catch (error) {
      throw new Error("REGISTRATION_FAILED");
    }
  },

  createCapsule: async (capsuleData) => {
    try {
      const response = await fetchWithConfig("/capsules/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(capsuleData),
      });

      if (!response) {
        throw new Error("CAPSULE_CREATION_FAILED");
      }
    } catch (error) {
      console.error("Capsule creation failed:", error);
      throw new Error("CAPSULE_CREATION_FAILED");
    }
  },
      getContributorCapsules: async () => {
        try {
          return await fetchWithConfig("/capsules/contributor-capsules", {
            method: "GET",
            headers: {
              'Content-Type': 'application/json'
            }
          });
        } catch (error) {
          throw new Error('Failed to fetch contributor capsules');
        }
      },
  loginWithGoogle: async (token) => {
    try {
      const response = await fetchWithConfig("/user/login/sso", {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
        if (response) {
            localStorage.setItem("access_token", response.accessToken);
            localStorage.setItem("refresh_token", response.refreshToken);
            localStorage.setItem("email", response.email);
            localStorage.setItem("role", response.role);
            console.log("Login successful:", response)
            console.log("Access token:", response.accessToken)
            console.log("Refresh token:", response.refreshToken)
        }
      return response;
    } catch (error) {
      console.error("Google login failed:", error);
      throw error;
    }
  },

  newPassword: async (formData) => {
    try {
      await fetchWithConfig('/user/password', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          currentPassword: formData.currentPassword,
          newPassword: formData.newPassword,
        }),
        credentials: 'include'
      });

      return true;
    } catch (error) {
      console.error("Password change failed:", error);
      throw error;
    }
  },

  changeEmail: async (formData) => {
    try {
      console.log("formData", formData);
      console.log("formData", formData.password);
      await fetchWithConfig('/user/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: formData.newEmail,
          password: formData.password,
        }),
        credentials: 'include'
      });

      return true;
    } catch (error) {
      console.error("Email change failed:", error);
      throw error;
    }
  },

  changeProfile: async (formData) => {
    try {
      await fetchWithConfig('/user/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: formData.name,
          bio: formData.bio,
        }),
        credentials: 'include'
      });

      return true;
    } catch (error) {
      console.error("Profile change failed:", error);
      throw error;
    }
  },

  getUserProfile: async () => {
    try {
      // Přidáme správný base URL pro backend
      return await fetchWithConfig('/user/profile', {  // Upravte port podle vašeho BE
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include'
      });
    } catch (error) {
      console.error('Error fetching user profile:', error);
      throw error;
    }
  },

  getCapsules: async () => {
    try {
      return await fetchWithConfig('/capsules/user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include'
      });
    } catch (error) {
      console.error('Error fetching capsules:', error);
      throw error;
    }
  },

  deleteAccount: async () => {
    try {
      return await fetchWithConfig('/user/delete', {
        method: 'DELETE',
        credentials: 'include'
      });
    } catch (error) {
      console.error("Account deletion failed:", error);
      throw error;
    }
  },

  followUser: async (userId) => {
    try {
      await fetchWithConfig(`/follow/${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include'
      });
    } catch (error) {
      console.error("Failed to follow user:", error);
      throw error;
    }
  },

  unfollowUser: async (userId, followerId) => {
    try {
      await fetchWithConfig(`/follow/${userId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          followerId: followerId,
        }),
        credentials: 'include'
      });
    } catch (error) {
      console.error("Failed to unfollow user:", error);
      throw error;
    }
  },

  getFollowers: async () => {
    try {
      return await fetchWithConfig('/follow/followers', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include'
      });
    } catch (error) {
        console.error("Failed to get followers:", error);
        throw error;
    }
  },

  getFollowing: async () => {
    try {
        return await fetchWithConfig('/follow/following', {
            method: 'GET',
            headers: {
            'Content-Type': 'application/json',
            },
            credentials: 'include'
        });
    } catch (error) {
        console.error("Failed to get following:", error);
        throw error;
    }
  },

  searchUsers: async (query) => {
    try {
      return await fetchWithConfig(`/user/search?query=${encodeURIComponent(query)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include'

      });
    } catch (error) {
      console.error('Failed to search users:', error);
      throw error;
    }
  },

  // Admin functions
  findEmails: async (query) => {
    try {
      const response = await fetchWithConfig(`/admin/findEmails/${encodeURIComponent(query)}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
      });
      return response; // Response is a List<String>
    } catch (error) {
      console.error('Failed to find emails:', error);
      throw error;
    }
  },

  getUserByEmail: async (email) => {
    try {
      const response = await fetchWithConfig(`/admin/getUserByEmail/${encodeURIComponent(email)}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
      });
      return response; // Response is a UserDto
    } catch (error) {
      console.error('Failed to get user by email:', error);
      throw error;
    }
  },

  updateUser: async (userDto) => {
    try {
      const response = await fetchWithConfig('/admin/updateUser', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
        name: userDto.name,
        bio: userDto.bio,
        role: userDto.role,
        email: userDto.email,
      }),
        credentials: 'include',
      });
      return response; // Response is a Boolean
    } catch (error) {
      console.error('Failed to update user:', error);
      throw error;
    }
  },

  deleteCapsule: async (capsuleId) => {
    try {
      const response = await fetchWithConfig(`/admin/deleteCapsule/${capsuleId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
      });
      return response; // Response is a Boolean
    } catch (error) {
      console.error('Failed to delete capsule:', error);
      throw error;
    }
  }
};
