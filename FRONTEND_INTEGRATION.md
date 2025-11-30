# Frontend Integration Guide

## 🚀 Quick Start for Frontend Developers

This guide helps you integrate your frontend application with the Meal Planner Backend API.

---

## 📡 API Base URL

```
Development: http://localhost:8080
Production: [YOUR_PRODUCTION_URL]
```

## 📚 API Endpoints Overview

### Authentication (`/api/v1/users`)
- Register, Login, Logout
- Profile management
- Password management
- Email verification

### Meals (`/api/meals`)
- Browse meals (public)
- Rate meals (authenticated)
- CRUD operations (admin)

### Custom Plans (`/api/plans`)
- Browse plans (public)
- Create/manage plans (authenticated)
- Add/remove meals

### Subscriptions (`/api/subscriptions`)
- Create subscriptions
- Manage subscription status
- View scheduled meals

### Deliveries (`/api/deliveries`)
- Track current delivery
- View delivery history
- Update delivery preferences
- Confirm delivery receipt

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
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "CLIENT"
}
```

**Note:** A verification email is sent automatically. User can still use the app but some features may require email verification.

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
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
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

### 4. Verify Email

**Endpoint:** `GET /api/v1/users/verify-email?token={token}`

**Request:**
```javascript
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get('token');

const response = await fetch(`http://localhost:8080/api/v1/users/verify-email?token=${token}`, {
  method: 'GET'
});

const data = await response.json();
```

**Response (200 OK):**
```json
{
  "message": "Email verified successfully"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid or expired verification token"
}
```

---

### 5. Resend Verification Email

**Endpoint:** `POST /api/v1/users/resend-verification`

**Request:**
```javascript
const response = await fetch('http://localhost:8080/api/v1/users/resend-verification', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    email: 'john@example.com'
  })
});

const data = await response.json();
```

**Response (200 OK):**
```json
{
  "message": "Verification email sent"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email is already verified"
}
```

---

## 💾 Token Management

### Store Tokens After Login/Registration
```javascript
// After successful login or registration
const { token, refreshToken, userId, email, fullName, role } = response.data;

// Store tokens
localStorage.setItem('authToken', token);
localStorage.setItem('refreshToken', refreshToken);

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

### Clear Tokens on Logout
```javascript
localStorage.removeItem('authToken');
localStorage.removeItem('refreshToken');
localStorage.removeItem('user');
// Redirect to login page
```

### Refresh Access Token
```javascript
async function refreshAccessToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch('http://localhost:8080/api/v1/users/refresh-token', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ refreshToken })
  });

  if (response.ok) {
    const { token, refreshToken: newRefreshToken } = await response.json();
    localStorage.setItem('authToken', token);
    localStorage.setItem('refreshToken', newRefreshToken);
    return token;
  } else {
    // Refresh token expired, redirect to login
    localStorage.clear();
    window.location.href = '/login';
    throw new Error('Session expired');
  }
}
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

  // Remove auth tokens
  removeToken() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }

  // Get refresh token
  getRefreshToken() {
    return localStorage.getItem('refreshToken');
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
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('user', JSON.stringify({
      userId: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role
    }));

    return data;
  }

  // Verify email
  async verifyEmail(token) {
    const response = await fetch(`${this.baseURL}/users/verify-email?token=${token}`, {
      method: 'GET'
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Email verification failed');
    }

    return await response.json();
  }

  // Resend verification email
  async resendVerification(email) {
    const response = await fetch(`${this.baseURL}/users/resend-verification`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email })
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to resend verification email');
    }

    return await response.json();
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
        // Try to refresh token
        try {
          const newToken = await this.refreshAccessToken();
          // Retry the request with new token
          const retryResponse = await fetch(`${this.baseURL}/users/${userId}`, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${newToken}`,
              'Content-Type': 'application/json',
            }
          });
          
          if (retryResponse.ok) {
            return await retryResponse.json();
          }
        } catch (refreshError) {
          this.removeToken();
          throw new Error('Session expired. Please login again.');
        }
      }
      throw new Error('Failed to fetch user profile');
    }

    return await response.json();
  }

  // Logout
  async logout() {
    const token = this.getToken();
    
    if (token) {
      try {
        await fetch(`${this.baseURL}/users/logout`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
      } catch (error) {
        console.error('Logout error:', error);
      }
    }
    
    this.removeToken();
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
        // Try to refresh token
        try {
          const newToken = await this.refreshAccessToken();
          // Retry the request with new token
          const retryResponse = await fetch(`${this.baseURL}${endpoint}`, {
            ...options,
            headers: {
              'Authorization': `Bearer ${newToken}`,
              'Content-Type': 'application/json',
              ...options.headers,
            }
          });
          
          if (retryResponse.ok) {
            return await retryResponse.json();
          }
        } catch (refreshError) {
          this.removeToken();
          throw new Error('Session expired. Please login again.');
        }
      }
      const error = await response.json();
      throw new Error(error.message || 'Request failed');
    }

    return await response.json();
  }

  // Browse meals (public)
  async getMeals(search = '', minRating = null, excludeAllergens = [], page = 0, size = 20) {
    let url = `${this.baseURL.replace('/v1', '')}/meals?page=${page}&size=${size}`;
    if (search) url += `&search=${encodeURIComponent(search)}`;
    if (minRating) url += `&minRating=${minRating}`;
    if (excludeAllergens.length > 0) url += `&excludeAllergens=${excludeAllergens.join(',')}`;

    const response = await fetch(url);
    if (!response.ok) throw new Error('Failed to fetch meals');
    return await response.json();
  }

  // Get meal details (public)
  async getMealById(mealId) {
    const response = await fetch(`${this.baseURL.replace('/v1', '')}/meals/${mealId}`);
    if (!response.ok) throw new Error('Failed to fetch meal');
    return await response.json();
  }

  // Rate a meal (authenticated)
  async rateMeal(mealId, rating) {
    return await this.authenticatedRequest(`/meals/${mealId}/rate`.replace('/v1', ''), {
      method: 'POST',
      body: JSON.stringify({ rating })
    });
  }

  // Browse custom plans (public)
  async getPlans(categoryId = null, page = 0, size = 20) {
    let url = `${this.baseURL.replace('/v1', '')}/plans?page=${page}&size=${size}`;
    if (categoryId) url += `&categoryId=${categoryId}`;

    const response = await fetch(url);
    if (!response.ok) throw new Error('Failed to fetch plans');
    return await response.json();
  }

  // Get plan categories (public)
  async getPlanCategories() {
    const response = await fetch(`${this.baseURL.replace('/v1', '')}/plans/categories`);
    if (!response.ok) throw new Error('Failed to fetch categories');
    return await response.json();
  }

  // Create custom plan (authenticated)
  async createPlan(name, description, categoryId, mealIds) {
    return await this.authenticatedRequest('/plans'.replace('/v1', ''), {
      method: 'POST',
      body: JSON.stringify({ name, description, categoryId, mealIds })
    });
  }

  // Create subscription (authenticated)
  async createSubscription(planId, startDate, frequency, deliveryAddress) {
    return await this.authenticatedRequest('/subscriptions', {
      method: 'POST',
      body: JSON.stringify({ planId, startDate, frequency, deliveryAddress })
    });
  }

  // Get user subscriptions (authenticated)
  async getUserSubscriptions(status = null, page = 0, size = 20) {
    let url = `/subscriptions?page=${page}&size=${size}`;
    if (status) url += `&status=${status}`;
    return await this.authenticatedRequest(url);
  }

  // Pause subscription (authenticated)
  async pauseSubscription(subscriptionId) {
    return await this.authenticatedRequest(`/subscriptions/${subscriptionId}/pause`, {
      method: 'PATCH'
    });
  }

  // Resume subscription (authenticated)
  async resumeSubscription(subscriptionId) {
    return await this.authenticatedRequest(`/subscriptions/${subscriptionId}/resume`, {
      method: 'PATCH'
    });
  }

  // Cancel subscription (authenticated)
  async cancelSubscription(subscriptionId) {
    return await this.authenticatedRequest(`/subscriptions/${subscriptionId}/cancel`, {
      method: 'PATCH'
    });
  }

  // Get current delivery (authenticated)
  async getCurrentDelivery() {
    return await this.authenticatedRequest('/deliveries/current');
  }

  // Get delivery by ID (authenticated)
  async getDeliveryById(deliveryId) {
    return await this.authenticatedRequest(`/deliveries/${deliveryId}`);
  }

  // Get delivery history (authenticated)
  async getDeliveryHistory(filters = {}, page = 0, size = 20) {
    let url = `/deliveries/history?page=${page}&size=${size}`;
    if (filters.startDate) url += `&startDate=${filters.startDate}`;
    if (filters.endDate) url += `&endDate=${filters.endDate}`;
    if (filters.status) url += `&status=${filters.status}`;
    return await this.authenticatedRequest(url);
  }

  // Update delivery preferences (authenticated)
  async updateDeliveryPreferences(deliveryId, updates) {
    return await this.authenticatedRequest(`/deliveries/${deliveryId}`, {
      method: 'PATCH',
      body: JSON.stringify(updates)
    });
  }

  // Confirm delivery (authenticated)
  async confirmDelivery(deliveryId) {
    return await this.authenticatedRequest(`/deliveries/${deliveryId}/confirm`, {
      method: 'POST'
    });
  }

  // Admin: Get all deliveries (admin only)
  async adminGetAllDeliveries(filters = {}, page = 0, size = 20) {
    let url = `/admin/deliveries?page=${page}&size=${size}`;
    if (filters.status) url += `&status=${filters.status}`;
    if (filters.date) url += `&date=${filters.date}`;
    if (filters.userId) url += `&userId=${filters.userId}`;
    if (filters.userEmail) url += `&userEmail=${filters.userEmail}`;
    return await this.authenticatedRequest(url);
  }

  // Admin: Get delivery details (admin only)
  async adminGetDeliveryById(deliveryId) {
    return await this.authenticatedRequest(`/admin/deliveries/${deliveryId}`);
  }

  // Admin: Update delivery status (admin only)
  async adminUpdateDeliveryStatus(deliveryId, status) {
    return await this.authenticatedRequest(`/admin/deliveries/${deliveryId}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status })
    });
  }
}

// Export singleton instance
export const apiService = new ApiService();
```

---

## 📦 Delivery Tracking Examples

### Get Current Delivery

```javascript
const getCurrentDelivery = async () => {
  try {
    const delivery = await apiService.getCurrentDelivery();
    console.log('Today\'s delivery:', delivery);
    // Display delivery status, time, address, and meals
  } catch (err) {
    if (err.message.includes('No active delivery')) {
      console.log('No delivery scheduled for today');
    }
  }
};
```

### Update Delivery Preferences

```javascript
// Update delivery time
await apiService.updateDeliveryPreferences(123, { 
  deliveryTime: '19:30' 
});

// Update address
await apiService.updateDeliveryPreferences(123, { 
  address: '456 Oak Avenue, City, Country' 
});

// Update both
await apiService.updateDeliveryPreferences(123, { 
  deliveryTime: '19:30',
  address: '456 Oak Avenue, City, Country'
});
```

### Confirm Delivery

```javascript
const confirmDelivery = async (deliveryId) => {
  try {
    const confirmed = await apiService.confirmDelivery(deliveryId);
    console.log('Delivery confirmed:', confirmed);
  } catch (err) {
    console.error('Cannot confirm:', err.message);
  }
};
```

### Get Delivery History

```javascript
// Get all deliveries
const history = await apiService.getDeliveryHistory();

// Get deliveries for January 2024
const januaryDeliveries = await apiService.getDeliveryHistory({
  startDate: '2024-01-01',
  endDate: '2024-01-31'
});

// Get only confirmed deliveries
const confirmedDeliveries = await apiService.getDeliveryHistory({
  status: 'CONFIRMED'
});
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
