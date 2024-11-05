import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { GoogleOAuthProvider } from '@react-oauth/google';

createRoot(document.getElementById('root')).render(
    <GoogleOAuthProvider clientId="1030521267361-f9p8o80fumi8dbkr6kd63tv57uq5frf0.apps.googleusercontent.com">
        <StrictMode>
            <App />
        </StrictMode>
    </GoogleOAuthProvider>
)
