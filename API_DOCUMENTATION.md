# Meal Planner API - Complete Documentation

## Base URL
```
Development: http://localhost:8080
```

---

## 🔐 Authentication Endpoints

All authentication endpoints are under `/api/v1/users`

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

### 10. Verify Email
Verify user email address using verification token.

**Endpoint:** `GET /users/verify-email?token={token}`

**Query Parameters:**
- `token` - Email verification token (sent via email)

**Response (200 OK):**
```json
{
  "message": "Email verified successfully"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid, expired, or already used token

---

### 11. Resend Verification Email
Request a new email verification token.

**Endpoint:** `POST /users/resend-verification`

**Request Body:**
```json
{
  "email": "john@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Verification email sent"
}
```

**Error Responses:**
- `400 Bad Request` - Email is already verified
- `404 Not Found` - User not found

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

### Implemented
- ✅ Email verification with tokens
- ✅ Authorization checks (users can only update own data)
- ✅ Resend verification email

### Pending
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

## 🍽️ Meal Management Endpoints

All meal endpoints are under `/api/meals`

### 12. List Meals (Public)
Get paginated list of meals with optional filters.

**Endpoint:** `GET /meals`

**Query Parameters:**
- `search` (optional) - Search term for meal name/description
- `minRating` (optional) - Minimum rating filter
- `excludeAllergens` (optional) - Comma-separated allergen IDs to exclude
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page

**Response (200 OK):**
```json
{
  "content": [
    {
      "mealId": 1,
      "name": "Grilled Chicken Salad",
      "description": "Healthy protein-rich salad",
      "calories": 350,
      "protein": 35,
      "carbs": 20,
      "fats": 15,
      "averageRating": 4.5,
      "imageUrl": "https://example.com/image.jpg"
    }
  ],
  "totalPages": 5,
  "totalElements": 100,
  "size": 20,
  "number": 0
}
```

---

### 13. Get Meal Details (Public)
Get detailed information about a specific meal.

**Endpoint:** `GET /meals/{id}`

**Response (200 OK):**
```json
{
  "mealId": 1,
  "name": "Grilled Chicken Salad",
  "description": "Healthy protein-rich salad",
  "calories": 350,
  "protein": 35,
  "carbs": 20,
  "fats": 15,
  "averageRating": 4.5,
  "imageUrl": "https://example.com/image.jpg",
  "ingredients": ["Chicken", "Lettuce", "Tomatoes"],
  "allergens": ["Dairy"],
  "preparationTime": 20
}
```

**Error Responses:**
- `404 Not Found` - Meal not found

---

### 14. Rate Meal (Authenticated)
Rate a meal (1-5 stars).

**Endpoint:** `POST /meals/{id}/rate`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "rating": 5
}
```

**Response (200 OK):**
```json
{}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `404 Not Found` - Meal not found

---

### 15. Create Meal (Admin Only)
Create a new meal.

**Endpoint:** `POST /meals`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "New Meal",
  "description": "Description",
  "calories": 400,
  "protein": 30,
  "carbs": 40,
  "fats": 15,
  "imageUrl": "https://example.com/image.jpg",
  "ingredients": ["Ingredient 1", "Ingredient 2"],
  "allergenIds": [1, 2],
  "preparationTime": 30
}
```

**Response (201 Created):**
```json
{
  "mealId": 10,
  "name": "New Meal",
  "description": "Description",
  "calories": 400,
  "protein": 30,
  "carbs": 40,
  "fats": 15,
  "averageRating": 0,
  "imageUrl": "https://example.com/image.jpg"
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not an admin

---

### 16. Update Meal (Admin Only)
Update an existing meal.

**Endpoint:** `PUT /meals/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:** (all fields optional)
```json
{
  "name": "Updated Meal",
  "description": "Updated description",
  "calories": 450
}
```

**Response (200 OK):**
```json
{
  "mealId": 10,
  "name": "Updated Meal",
  "description": "Updated description",
  "calories": 450,
  "protein": 30,
  "carbs": 40,
  "fats": 15,
  "averageRating": 4.2,
  "imageUrl": "https://example.com/image.jpg"
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not an admin
- `404 Not Found` - Meal not found

---

### 17. Delete Meal (Admin Only)
Delete a meal.

**Endpoint:** `DELETE /meals/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (204 No Content)**

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not an admin
- `404 Not Found` - Meal not found

---

## 📋 Custom Plan Endpoints

All custom plan endpoints are under `/api/plans`

### 18. List Plan Categories (Public)
Get all available plan categories.

**Endpoint:** `GET /plans/categories`

**Response (200 OK):**
```json
[
  {
    "categoryId": 1,
    "name": "Weight Loss",
    "description": "Plans focused on losing weight"
  },
  {
    "categoryId": 2,
    "name": "Muscle Gain",
    "description": "Plans for building muscle"
  }
]
```

---

### 19. List Custom Plans (Public)
Get paginated list of custom plans with optional category filter.

**Endpoint:** `GET /plans`

**Query Parameters:**
- `categoryId` (optional) - Filter by category
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page

**Response (200 OK):**
```json
{
  "content": [
    {
      "planId": 1,
      "name": "30-Day Weight Loss",
      "description": "Comprehensive weight loss plan",
      "categoryName": "Weight Loss",
      "creatorName": "John Doe",
      "mealCount": 90,
      "totalCalories": 54000
    }
  ],
  "totalPages": 3,
  "totalElements": 50,
  "size": 20,
  "number": 0
}
```

---

### 20. Get Plan Details (Public)
Get detailed information about a custom plan.

**Endpoint:** `GET /plans/{id}`

**Response (200 OK):**
```json
{
  "planId": 1,
  "name": "30-Day Weight Loss",
  "description": "Comprehensive weight loss plan",
  "categoryName": "Weight Loss",
  "creatorName": "John Doe",
  "meals": [
    {
      "mealId": 1,
      "name": "Breakfast Oatmeal",
      "calories": 300,
      "protein": 10,
      "carbs": 50,
      "fats": 8
    }
  ],
  "totalCalories": 54000,
  "mealCount": 90
}
```

**Error Responses:**
- `404 Not Found` - Plan not found

---

### 21. Create Custom Plan (Authenticated)
Create a new custom meal plan.

**Endpoint:** `POST /plans`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "My Custom Plan",
  "description": "My personalized meal plan",
  "categoryId": 1,
  "mealIds": [1, 2, 3, 4, 5]
}
```

**Response (201 Created):**
```json
{
  "planId": 10,
  "name": "My Custom Plan",
  "description": "My personalized meal plan",
  "categoryName": "Weight Loss",
  "creatorName": "John Doe",
  "mealCount": 5,
  "totalCalories": 1800
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `404 Not Found` - Category or meals not found

---

### 22. Update Custom Plan (Owner or Admin)
Update a custom plan.

**Endpoint:** `PUT /plans/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:** (all fields optional)
```json
{
  "name": "Updated Plan Name",
  "description": "Updated description",
  "categoryId": 2
}
```

**Response (200 OK):**
```json
{
  "planId": 10,
  "name": "Updated Plan Name",
  "description": "Updated description",
  "categoryName": "Muscle Gain",
  "creatorName": "John Doe",
  "mealCount": 5,
  "totalCalories": 1800
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner or admin
- `404 Not Found` - Plan not found

---

### 23. Delete Custom Plan (Owner or Admin)
Delete a custom plan.

**Endpoint:** `DELETE /plans/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (204 No Content)**

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner or admin
- `404 Not Found` - Plan not found

---

### 24. Add Meals to Plan (Owner or Admin)
Add meals to an existing custom plan.

**Endpoint:** `POST /plans/{id}/meals`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "mealIds": [6, 7, 8]
}
```

**Response (200 OK):**
```json
{}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner or admin
- `404 Not Found` - Plan or meals not found

---

### 25. Remove Meal from Plan (Owner or Admin)
Remove a meal from a custom plan.

**Endpoint:** `DELETE /plans/{id}/meals/{mealId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (204 No Content)**

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner or admin
- `404 Not Found` - Plan or meal not found

---

## 📅 Subscription Endpoints

### 26. Create Subscription (Authenticated)
Create a new meal subscription.

**Endpoint:** `POST /api/subscriptions`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "planId": 1,
  "startDate": "2024-01-01",
  "frequency": "DAILY",
  "deliveryAddress": "123 Main St, City, Country"
}
```

**Response (201 Created):**
```json
{
  "subscriptionId": 1,
  "planName": "30-Day Weight Loss",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "frequency": "DAILY",
  "status": "ACTIVE",
  "deliveryAddress": "123 Main St, City, Country"
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `404 Not Found` - Plan not found

---

### 27. List User Subscriptions (Authenticated)
Get user's subscriptions with optional status filter.

**Endpoint:** `GET /api/subscriptions`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `status` (optional) - Filter by status (ACTIVE, PAUSED, CANCELLED)
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page

**Response (200 OK):**
```json
{
  "content": [
    {
      "subscriptionId": 1,
      "planName": "30-Day Weight Loss",
      "startDate": "2024-01-01",
      "endDate": "2024-01-31",
      "frequency": "DAILY",
      "status": "ACTIVE",
      "deliveryAddress": "123 Main St, City, Country"
    }
  ],
  "totalPages": 1,
  "totalElements": 3,
  "size": 20,
  "number": 0
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated

---

### 28. Get Subscription Details (Owner or Admin)
Get detailed information about a subscription.

**Endpoint:** `GET /api/subscriptions/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "subscriptionId": 1,
  "planName": "30-Day Weight Loss",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "frequency": "DAILY",
  "status": "ACTIVE",
  "deliveryAddress": "123 Main St, City, Country",
  "meals": [
    {
      "mealId": 1,
      "name": "Breakfast Oatmeal",
      "calories": 300
    }
  ]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner or admin
- `404 Not Found` - Subscription not found

---

### 29. Pause Subscription (Owner)
Pause an active subscription.

**Endpoint:** `PATCH /api/subscriptions/{id}/pause`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner
- `404 Not Found` - Subscription not found

---

### 30. Resume Subscription (Owner)
Resume a paused subscription.

**Endpoint:** `PATCH /api/subscriptions/{id}/resume`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner
- `404 Not Found` - Subscription not found

---

### 31. Cancel Subscription (Owner)
Cancel a subscription.

**Endpoint:** `PATCH /api/subscriptions/{id}/cancel`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner
- `404 Not Found` - Subscription not found

---

### 32. Get Scheduled Meals (Owner or Admin)
Get meals scheduled for a subscription within a date range.

**Endpoint:** `GET /api/subscriptions/{id}/meals`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `startDate` (optional) - Start date (ISO format: YYYY-MM-DD)
- `endDate` (optional) - End date (ISO format: YYYY-MM-DD)

**Response (200 OK):**
```json
[
  {
    "scheduledDate": "2024-01-01",
    "mealId": 1,
    "mealName": "Breakfast Oatmeal",
    "calories": 300,
    "deliveryStatus": "PENDING"
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not the owner or admin
- `404 Not Found` - Subscription not found

---

### 33. List All Subscriptions (Admin Only)
Get all subscriptions with optional filters.

**Endpoint:** `GET /api/admin/subscriptions`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `userId` (optional) - Filter by user
- `status` (optional) - Filter by status
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page

**Response (200 OK):**
```json
{
  "content": [
    {
      "subscriptionId": 1,
      "planName": "30-Day Weight Loss",
      "userName": "John Doe",
      "startDate": "2024-01-01",
      "endDate": "2024-01-31",
      "frequency": "DAILY",
      "status": "ACTIVE"
    }
  ],
  "totalPages": 5,
  "totalElements": 100,
  "size": 20,
  "number": 0
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not an admin

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

### Email Verification Tokens Table
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

### Meals Table
- `meal_id` (PK)
- `name`
- `description`
- `calories`, `protein`, `carbs`, `fats`
- `image_url`
- `preparation_time`
- `average_rating`

### Custom Plans Table
- `plan_id` (PK)
- `name`
- `description`
- `category_id` (FK)
- `creator_id` (FK - User)
- `created_at`

### Subscriptions Table
- `subscription_id` (PK)
- `user_id` (FK)
- `plan_id` (FK)
- `start_date`
- `end_date`
- `frequency`
- `status`
- `delivery_address`

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

---

## 📦 Delivery Tracking

The Meal Planner API includes a comprehensive delivery tracking system for monitoring daily meal deliveries.

### Quick Overview

- **Automatic Creation:** Deliveries are automatically created daily at midnight for active subscriptions
- **Status Progression:** Deliveries progress through PREPARING → SHIPPED → DELIVERED → CONFIRMED
- **User Control:** Users can update delivery preferences (time/address) and confirm receipt
- **Admin Monitoring:** Admins can monitor and manage all deliveries with filtering

### Status Lifecycle

1. **PREPARING** - Initial status when delivery is created (default)
2. **SHIPPED** - Delivery is in transit (auto-transitions 2 hours before delivery time)
3. **DELIVERED** - Delivery has arrived (auto-transitions at delivery time)
4. **CONFIRMED** - User has confirmed receipt (manual confirmation only)

### User Endpoints

#### 34. Get Current Delivery
Get today's delivery for the authenticated user.

**Endpoint:** `GET /api/deliveries/current`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "deliveryId": 123,
  "deliveryDate": "2024-01-15",
  "deliveryTime": "18:00",
  "address": "123 Main St, City, Country",
  "status": "SHIPPED",
  "statusUpdatedAt": "2024-01-15T16:00:00",
  "confirmedAt": null,
  "meals": [
    {
      "mealId": 1,
      "name": "Grilled Chicken Salad",
      "calories": 350,
      "protein": 35,
      "carbs": 20,
      "fats": 15
    }
  ]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `404 Not Found` - No delivery found for today

---

#### 35. Get Delivery by ID
Get details of a specific delivery. Users can only access their own deliveries.

**Endpoint:** `GET /api/deliveries/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "deliveryId": 123,
  "deliveryDate": "2024-01-15",
  "deliveryTime": "18:00",
  "address": "123 Main St, City, Country",
  "status": "DELIVERED",
  "statusUpdatedAt": "2024-01-15T18:00:00",
  "confirmedAt": null,
  "meals": [...]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Delivery does not belong to user
- `404 Not Found` - Delivery not found

---

#### 36. Get Delivery History
Get paginated delivery history with optional filtering.

**Endpoint:** `GET /api/deliveries/history`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `startDate` (optional) - Filter by start date (YYYY-MM-DD)
- `endDate` (optional) - Filter by end date (YYYY-MM-DD)
- `status` (optional) - Filter by status (PREPARING, SHIPPED, DELIVERED, CONFIRMED)
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page

**Response (200 OK):**
```json
{
  "content": [
    {
      "deliveryId": 125,
      "deliveryDate": "2024-01-14",
      "status": "CONFIRMED",
      "deliveryTime": "18:00",
      "confirmed": true,
      "mealCount": 3
    }
  ],
  "totalPages": 3,
  "totalElements": 30,
  "size": 20,
  "number": 0
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated

---

#### 37. Update Delivery Preferences
Update delivery time and/or address. Only allowed when status is PREPARING.

**Endpoint:** `PATCH /api/deliveries/{id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:** (at least one field required)
```json
{
  "deliveryTime": "19:30",
  "address": "456 Oak Avenue, City, Country"
}
```

**Response (200 OK):**
```json
{
  "deliveryId": 123,
  "deliveryDate": "2024-01-15",
  "deliveryTime": "19:30",
  "address": "456 Oak Avenue, City, Country",
  "status": "PREPARING",
  "statusUpdatedAt": "2024-01-15T08:00:00",
  "confirmedAt": null,
  "meals": [...]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Delivery does not belong to user
- `404 Not Found` - Delivery not found
- `400 Bad Request` - Invalid input or delivery has already shipped

---

#### 38. Confirm Delivery
Confirm receipt of a delivered order. Only allowed when status is DELIVERED.

**Endpoint:** `POST /api/deliveries/{id}/confirm`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "deliveryId": 123,
  "deliveryDate": "2024-01-15",
  "deliveryTime": "18:00",
  "address": "123 Main St, City, Country",
  "status": "CONFIRMED",
  "statusUpdatedAt": "2024-01-15T18:00:00",
  "confirmedAt": "2024-01-15T18:15:30",
  "meals": [...]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Delivery does not belong to user
- `404 Not Found` - Delivery not found
- `400 Bad Request` - Delivery is not in DELIVERED status

---

### Admin Endpoints

#### 39. List All Deliveries (Admin)
Get paginated list of all deliveries with optional filtering.

**Endpoint:** `GET /api/admin/deliveries`

**Headers:**
```
Authorization: Bearer {token}
```

**Required Role:** ADMIN

**Query Parameters:**
- `status` (optional) - Filter by status
- `date` (optional) - Filter by delivery date (YYYY-MM-DD)
- `userId` (optional) - Filter by user ID
- `userEmail` (optional) - Filter by user email (partial match)
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page

**Response (200 OK):**
```json
{
  "content": [
    {
      "deliveryId": 123,
      "deliveryDate": "2024-01-15",
      "deliveryTime": "18:00",
      "address": "123 Main St, City, Country",
      "status": "DELIVERED",
      "createdAt": "2024-01-15T08:00:00",
      "statusUpdatedAt": "2024-01-15T18:00:00",
      "confirmedAt": null,
      "userId": 42,
      "userEmail": "john@example.com",
      "userName": "John Doe",
      "subscriptionId": 10,
      "subscriptionPlan": "30-Day Weight Loss",
      "meals": [...],
      "statusHistory": []
    }
  ],
  "totalPages": 5,
  "totalElements": 95,
  "size": 20,
  "number": 0
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User does not have ADMIN role

---

#### 40. Get Delivery Details (Admin)
Get complete delivery details including user information and status history.

**Endpoint:** `GET /api/admin/deliveries/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Required Role:** ADMIN

**Response (200 OK):**
```json
{
  "deliveryId": 123,
  "deliveryDate": "2024-01-15",
  "deliveryTime": "18:00",
  "address": "123 Main St, City, Country",
  "status": "DELIVERED",
  "createdAt": "2024-01-15T08:00:00",
  "statusUpdatedAt": "2024-01-15T18:00:00",
  "confirmedAt": null,
  "userId": 42,
  "userEmail": "john@example.com",
  "userName": "John Doe",
  "subscriptionId": 10,
  "subscriptionPlan": "30-Day Weight Loss",
  "meals": [...],
  "statusHistory": [
    {
      "status": "PREPARING",
      "timestamp": "2024-01-15T08:00:00",
      "updatedBy": "System"
    },
    {
      "status": "SHIPPED",
      "timestamp": "2024-01-15T16:00:00",
      "updatedBy": "System"
    },
    {
      "status": "DELIVERED",
      "timestamp": "2024-01-15T18:00:00",
      "updatedBy": "System"
    }
  ]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User does not have ADMIN role
- `404 Not Found` - Delivery not found

---

#### 41. Update Delivery Status (Admin)
Manually update delivery status.

**Endpoint:** `PATCH /api/admin/deliveries/{id}/status`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Required Role:** ADMIN

**Request Body:**
```json
{
  "status": "DELIVERED"
}
```

**Valid Status Values:** PREPARING, SHIPPED, DELIVERED, CONFIRMED

**Response (200 OK):**
```json
{
  "deliveryId": 123,
  "deliveryDate": "2024-01-15",
  "deliveryTime": "18:00",
  "address": "123 Main St, City, Country",
  "status": "DELIVERED",
  "createdAt": "2024-01-15T08:00:00",
  "statusUpdatedAt": "2024-01-15T17:30:00",
  "confirmedAt": null,
  "userId": 42,
  "userEmail": "john@example.com",
  "userName": "John Doe",
  "subscriptionId": 10,
  "subscriptionPlan": "30-Day Weight Loss",
  "meals": [...],
  "statusHistory": [...]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User does not have ADMIN role
- `404 Not Found` - Delivery not found
- `400 Bad Request` - Invalid status value

---

### Automatic Status Progression

The system automatically progresses delivery statuses:

1. **PREPARING → SHIPPED:** 2 hours before delivery time
2. **SHIPPED → DELIVERED:** At delivery time
3. **DELIVERED → CONFIRMED:** Manual user confirmation only

**Scheduler Jobs:**
- **Daily Delivery Creation:** Runs at midnight (00:00), creates deliveries for all active subscriptions
- **Status Progression:** Runs every minute, updates deliveries based on time

### Complete Documentation

For detailed delivery tracking API documentation including:
- Complete request/response examples
- Error responses and troubleshooting
- Status progression rules
- Integration examples
- Business rules and security

**See:** [Delivery Tracking API Documentation](DELIVERY_TRACKING_API.md)

---

## 📚 Additional Resources

- [Delivery Tracking API](DELIVERY_TRACKING_API.md) - Complete delivery tracking documentation
- [Authentication Guide](AUTHENTICATION.md) - Detailed authentication information
- [Frontend Integration](FRONTEND_INTEGRATION.md) - Frontend integration guide
