# Frontend Integration Guide

## 🚀 Quick Start for Frontend Developers

This guide helps you integrate your frontend application with the Meal Planner Backend API.

---

## 📡 API Base URL

```
Development: http://localhost:8080
Production: [YOUR_PRODUCTION_URL]
```

---

## 🔐 Authentication Flow

### Overview
1. User registers or logs in → Backend returns JWT token
2. Store token in localStorage/sessionStorage
3. Include token in Authorization header for all protected requests
4. Handle token expiration (401 errors)

---

## 📋 API Endpoints

### 1. User Registration

**Endpoint:** `POST /api/v1/users/register`

**Request:**
```javascript
const response = await fetch('http://localhost:8080/api/v1/users/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    fullName: 'John Doe',
    email: 'john@example.com',
    password: 'securePassword123'
  })
});

const data = await response.json();
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "CLIENT"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists."
}
```

---

### 2. User Login

**Endpoint:** `POST /api/v1/users/login`

**Request:**
```javascript
const response = await fetch('http://localhost:8080/api/v1/users/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    email: 'john@example.com',
    password: 'securePassword123'
  })
});

const data = await response.json();
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "CLIENT"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Bad credentials"
}
```

---

### 3. Get User Profile (Protected)

**Endpoint:** `GET /api/v1/users/{userId}`

**Request:**
```javascript
const token = localStorage.getItem('authToken');

const response = await fetch(`http://localhost:8080/api/v1/users/${userId}`, {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
  }
});

const data = await response.json();
```

**Response (200 OK):**
```json
{
  "userId": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "CLIENT"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

---

## 💾 Token Management

### Store Token After Login/Registration
```javascript
// After successful login or registration
const { token, userId, email, fullName, role } = response.data;

// Store token
localStorage.setItem('authToken', token);

// Store user info (optional)
localStorage.setItem('user', JSON.stringify({
  userId,
  email,
  fullName,
  role
}));
```

### Retrieve Token for API Calls
```javascript
const token = localStorage.getItem('authToken');
```

### Clear Token on Logout
```javascript
localStorage.removeItem('authToken');
localStorage.removeItem('user');
// Redirect to login page
```

---

## 🛠️ Helper Functions

### API Service Class (Recommended)

```javascript
class ApiService {
  constructor() {
    this.baseURL = 'http://localhost:8080/api/v1';
  }

  // Get auth token
  getToken() {
    return localStorage.getItem('authToken');
  }

  // Set auth token
  setToken(token) {
    localStorage.setItem('authToken', token);
  }

  // Remove auth token
  removeToken() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }

  // Register user
  async register(fullName, email, password) {
    const response = await fetch(`${this.baseURL}/users/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ fullName, email, password })
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Registration failed');
    }

    const data = await response.json();
    this.setToken(data.token);
    localStorage.setItem('user', JSON.stringify({
      userId: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role
    }));

    return data;
  }

  // Login user
  async login(email, password) {
    const response = await fetch(`${this.baseURL}/users/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Login failed');
    }

    const data = await response.json();
    this.setToken(data.token);
    localStorage.setItem('user', JSON.stringify({
      userId: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role
    }));

    return data;
  }

  // Get user profile
  async getUserProfile(userId) {
    const token = this.getToken();
    
    const response = await fetch(`${this.baseURL}/users/${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      }
    });

    if (!response.ok) {
      if (response.status === 401) {
        this.removeToken();
        throw new Error('Session expired. Please login again.');
      }
      throw new Error('Failed to fetch user profile');
    }

    return await response.json();
  }

  // Logout
  logout() {
    this.removeToken();
    // Redirect to login page
    window.location.href = '/login';
  }

  // Generic authenticated request
  async authenticatedRequest(endpoint, options = {}) {
    const token = this.getToken();
    
    const response = await fetch(`${this.baseURL}${endpoint}`, {
      ...options,
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        ...options.headers,
      }
    });

    if (!response.ok) {
      if (response.status === 401) {
        this.removeToken();
        throw new Error('Session expired. Please login again.');
      }
      const error = await response.json();
      throw new Error(error.message || 'Request failed');
    }

    return await response.json();
  }
}

// Export singleton instance
export const apiService = new ApiService();
```

---

## 📱 Usage Examples

### React Example

```jsx
import { useState } from 'react';
import { apiService } from './services/apiService';

function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const data = await apiService.login(email, password);
      console.log('Login successful:', data);
      // Redirect to dashboard
      window.location.href = '/dashboard';
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <form onSubmit={handleLogin}>
      {error && <div className="error">{error}</div>}
      
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        required
      />
      
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        required
      />
      
      <button type="submit">Login</button>
    </form>
  );
}
```

### Vue Example

```vue
<template>
  <form @submit.prevent="handleLogin">
    <div v-if="error" class="error">{{ error }}</div>
    
    <input
      v-model="email"
      type="email"
      placeholder="Email"
      required
    />
    
    <input
      v-model="password"
      type="password"
      placeholder="Password"
      required
    />
    
    <button type="submit">Login</button>
  </form>
</template>

<script>
import { apiService } from '@/services/apiService';

export default {
  data() {
    return {
      email: '',
      password: '',
      error: ''
    };
  },
  methods: {
    async handleLogin() {
      this.error = '';
      
      try {
        const data = await apiService.login(this.email, this.password);
        console.log('Login successful:', data);
        this.$router.push('/dashboard');
      } catch (err) {
        this.error = err.message;
      }
    }
  }
};
</script>
```

### Angular Example

```typescript
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  async handleLogin() {
    this.error = '';

    try {
      const data = await this.apiService.login(this.email, this.password);
      console.log('Login successful:', data);
      this.router.navigate(['/dashboard']);
    } catch (err: any) {
      this.error = err.message;
    }
  }
}
```

---

## 🔒 Protected Routes

### React Router Example

```jsx
import { Navigate } from 'react-router-dom';
import { apiService } from './services/apiService';

function ProtectedRoute({ children }) {
  const token = apiService.getToken();
  
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  
  return children;
}

// Usage in routes
<Route
  path="/dashboard"
  element={
    <ProtectedRoute>
      <Dashboard />
    </ProtectedRoute>
  }
/>
```

---

## ⚠️ Error Handling

### Common HTTP Status Codes

| Status Code | Meaning | Action |
|-------------|---------|--------|
| 200 | Success | Process response data |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Show validation errors to user |
| 401 | Unauthorized | Clear token, redirect to login |
| 403 | Forbidden | User doesn't have permission |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Show generic error message |

### Error Handling Example

```javascript
async function makeAuthenticatedRequest(endpoint, options) {
  try {
    const response = await fetch(endpoint, options);
    
    if (response.status === 401) {
      // Token expired or invalid
      apiService.logout();
      throw new Error('Session expired. Please login again.');
    }
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Request failed');
    }
    
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
}
```

---

## 🧪 Testing with Postman/Insomnia

### 1. Register a User
```
POST http://localhost:8080/api/v1/users/register
Content-Type: application/json

{
  "fullName": "Test User",
  "email": "test@example.com",
  "password": "password123"
}
```

### 2. Login
```
POST http://localhost:8080/api/v1/users/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

### 3. Get Profile (use token from login response)
```
GET http://localhost:8080/api/v1/users/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🔧 CORS Configuration

If you encounter CORS errors, the backend needs to allow your frontend origin.

**Backend Configuration Needed:**
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

## 📝 Token Expiration

- **Default Expiration:** 24 hours
- **What happens:** After 24 hours, the token becomes invalid
- **Frontend Action:** Catch 401 errors, clear token, redirect to login
- **Future Enhancement:** Implement refresh token mechanism

---

## ✅ Checklist for Frontend Integration

- [ ] Create API service class
- [ ] Implement register function
- [ ] Implement login function
- [ ] Store JWT token in localStorage
- [ ] Add Authorization header to protected requests
- [ ] Handle 401 errors (token expiration)
- [ ] Implement logout functionality
- [ ] Create protected route wrapper
- [ ] Add loading states
- [ ] Add error handling
- [ ] Test all authentication flows

---

## 🆘 Troubleshooting

### Issue: CORS Error
**Solution:** Backend needs to configure CORS to allow your frontend origin

### Issue: 401 Unauthorized on protected routes
**Solution:** Check if token is being sent in Authorization header correctly

### Issue: Token not persisting after page refresh
**Solution:** Ensure token is stored in localStorage, not just in component state

### Issue: "Bad credentials" error on login
**Solution:** Verify email and password are correct, check database has user

### Issue: "Email already exists" on registration
**Solution:** User with that email already registered, try different email or login

---

## 📞 Need Help?

- Check backend logs for detailed error messages
- Verify backend is running on `http://localhost:8080`
- Ensure database is running and has "CLIENT" role in `role` table
- Check network tab in browser DevTools for request/response details
