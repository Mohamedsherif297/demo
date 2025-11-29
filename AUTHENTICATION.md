# Authentication Guide

## Overview
This API uses JWT (JSON Web Token) based authentication with Spring Security.

## Endpoints

### 1. Register a New User
**POST** `/api/v1/users/register`

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
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "CLIENT"
}
```

### 2. Login
**POST** `/api/v1/users/login`

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
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "CLIENT"
}
```

### 3. Get User Profile (Protected)
**GET** `/api/v1/users/{userId}`

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

## How to Use

1. **Register or Login** to get a JWT token
2. **Include the token** in the `Authorization` header for protected endpoints:
   ```
   Authorization: Bearer YOUR_JWT_TOKEN
   ```
3. The token is valid for **24 hours** by default

## Configuration

JWT settings in `application.properties`:
```properties
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000  # 24 hours in milliseconds
```

## Development Mode

When running with `spring.profiles.active=dev`, all endpoints are accessible without authentication for easier testing.

## Security Features

- Passwords are hashed using BCrypt
- JWT tokens are signed with HS256 algorithm
- Stateless session management
- Role-based access control ready (ROLE_CLIENT, ROLE_ADMIN, etc.)
