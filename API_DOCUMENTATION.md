# Meal Planner API - Complete Authentication Documentation

## Base URL
```
Development: http://localhost:8080/api/v1
```

---

## 🔐 Authentication Endpoints

### 1. Register User
Create a new user account.

**Endpoint:** `POST /users/register`

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
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

**Error Responses:**
- `400 Bad Request` - Email already exists

---

### 2. Login
Authenticate and receive access tokens.

**Endpoint:** `POST /users/login`

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
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

**Error Responses:**
- `401 Unauthorized` - Invalid credentials

---

### 3. Refresh Token
Get a new access token using refresh token.

**Endpoint:** `POST /users/refresh-token`

**Request Body:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
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

**Error Responses:**
- `401 Unauthorized` - Invalid, expired, or revoked refresh token

---

### 4. Logout
Invalidate the current access token.

**Endpoint:** `POST /users/logout`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "message": "Logged out successfully"
}
```

---

## 👤 User Profile Endpoints

### 5. Get User Profile
Retrieve user profile information.

**Endpoint:** `GET /users/{userId}`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
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

**Error Responses:**
- `401 Unauthorized` - Missing or invalid token
- `404 Not Found` - User not found

---

### 6. Update User Profile
Update user profile information.

**Endpoint:** `PUT /users/{userId}`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body (all fields optional):**
```json
{
  "fullName": "John Updated",
  "phoneNumber": "+1234567890",
  "address": "123 Main St, City, Country",
  "photoUrl": "https://example.com/photo.jpg",
  "dob": "1990-01-15",
  "weight": 75.5,
  "height": 180.0,
  "gymDays": 4,
  "weightGoal": "lose",
  "weeklyDuration": 300,
  "caloriesPerDay": 2000
}
```

**Response (200 OK):**
```json
{
  "userId": 1,
  "fullName": "John Updated",
  "email": "john@example.com",
  "role": "CLIENT"
}
```

**Error Responses:**
- `401 Unauthorized` - Missing or invalid token
- `404 Not Found` - User not found

---

## 🔑 Password Management Endpoints

### 7. Change Password
Change password for authenticated user.

**Endpoint:** `PUT /users/{userId}/password`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body:**
```json
{
  "currentPassword": "oldPassword123",
  "newPassword": "newSecurePassword456"
}
```

**Response (200 OK):**
```json
{
  "message": "Password changed successfully"
}
```

**Error Responses:**
- `400 Bad Request` - Current password is incorrect
- `401 Unauthorized` - Missing or invalid token
- `404 Not Found` - User not found

---

### 8. Forgot Password
Request a password reset token.

**Endpoint:** `POST /users/forgot-password`

**Request Body:**
```json
{
  "email": "john@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset email sent"
}
```

**Note:** Reset token is sent to console in development. In production, it will be emailed.

**Error Responses:**
- `404 Not Found` - User with email not found

---

### 9. Reset Password
Reset password using the token from forgot password.

**Endpoint:** `POST /users/reset-password`

**Request Body:**
```json
{
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "newPassword": "newSecurePassword789"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset successfully"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid, expired, or already used token

---

## 📊 Token Information

### Access Token (JWT)
- **Type:** Bearer Token
- **Expiration:** 24 hours (86400000 ms)
- **Usage:** Include in Authorization header for all protected endpoints
- **Format:** `Authorization: Bearer <token>`

### Refresh Token
- **Type:** UUID
- **Expiration:** 7 days (604800000 ms)
- **Storage:** Database
- **Usage:** Use to get new access token when it expires

### Password Reset Token
- **Type:** UUID
- **Expiration:** 1 hour
- **Storage:** Database
- **Usage:** One-time use for password reset

---

## 🔒 Security Features

### Implemented
- ✅ JWT-based authentication
- ✅ BCrypt password hashing
- ✅ Refresh token mechanism
- ✅ Token blacklist for logout
- ✅ Password reset with expiring tokens
- ✅ Stateless session management
- ✅ Role-based access control foundation

### Pending
- ⏳ Email verification
- ⏳ Authorization checks (user can only update own data)
- ⏳ Rate limiting
- ⏳ Password strength validation
- ⏳ Production email service

---

## 🚀 Quick Start Examples

### JavaScript/TypeScript

```javascript
// Register
const registerResponse = await fetch('http://localhost:8080/api/v1/users/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    fullName: 'John Doe',
    email: 'john@example.com',
    password: 'securePassword123'
  })
});
const { token, refreshToken } = await registerResponse.json();

// Store tokens
localStorage.setItem('accessToken', token);
localStorage.setItem('refreshToken', refreshToken);

// Make authenticated request
const profileResponse = await fetch('http://localhost:8080/api/v1/users/1', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
  }
});

// Refresh token when access token expires
const refreshResponse = await fetch('http://localhost:8080/api/v1/users/refresh-token', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    refreshToken: localStorage.getItem('refreshToken')
  })
});
const { token: newToken } = await refreshResponse.json();
localStorage.setItem('accessToken', newToken);

// Logout
await fetch('http://localhost:8080/api/v1/users/logout', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
  }
});
localStorage.clear();
```

---

## 📝 Database Tables

### Users Table
- `user_id` (PK)
- `full_name`
- `email` (unique)
- `password_hash`
- `email_verified` (boolean)
- `dob`, `phone_number`, `address`, `photo_url`
- `role_id` (FK)
- `weight`, `height`, `gym_days`, `weight_goal`, `weekly_duration`, `calories_per_day`

### Password Reset Tokens Table
- `id` (PK)
- `token` (unique)
- `user_id` (FK)
- `expiry_date`
- `used` (boolean)

### Refresh Tokens Table
- `id` (PK)
- `token` (unique)
- `user_id` (FK)
- `expiry_date`
- `revoked` (boolean)

### Blacklisted Tokens Table
- `id` (PK)
- `token` (unique)
- `blacklisted_at`
- `expiry_date`

---

## ⚙️ Configuration

### application.properties
```properties
# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000
jwt.refresh.expiration=604800000

# Profile
spring.profiles.active=dev
```

### Development Mode
When `spring.profiles.active=dev`, all endpoints are accessible without authentication for easier testing.

### Production Mode
Remove or change `spring.profiles.active` to enable full authentication.

---

## 🧪 Testing

### Postman Collection
Import these requests into Postman:

1. **Register** - POST `/users/register`
2. **Login** - POST `/users/login`
3. **Get Profile** - GET `/users/1` (add Bearer token)
4. **Update Profile** - PUT `/users/1` (add Bearer token)
5. **Change Password** - PUT `/users/1/password` (add Bearer token)
6. **Forgot Password** - POST `/users/forgot-password`
7. **Reset Password** - POST `/users/reset-password`
8. **Refresh Token** - POST `/users/refresh-token`
9. **Logout** - POST `/users/logout` (add Bearer token)

---

## 🐛 Common Issues

### Issue: 401 Unauthorized
- Check if token is included in Authorization header
- Verify token hasn't expired (24 hours)
- Check if token was blacklisted (after logout)

### Issue: Token expired
- Use refresh token endpoint to get new access token
- If refresh token also expired, user must login again

### Issue: Password reset token not working
- Tokens expire after 1 hour
- Tokens can only be used once
- Check console output for the token in development mode

---

## 📧 Email Service

Currently, emails are printed to console. To enable production email:

1. Add Spring Mail dependency
2. Configure SMTP settings in application.properties
3. Update EmailService.java to send actual emails

Example SMTP configuration:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
