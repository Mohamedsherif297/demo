# Delivery Tracking API Documentation

## Overview

The Delivery Tracking System provides real-time tracking of daily meal deliveries for subscribed users. Deliveries are automatically created for active subscriptions and progress through predefined status stages (Preparing → Shipped → Delivered → Confirmed).

**Base URL:** `http://localhost:8080/api`

---

## 🔐 Authentication

All delivery endpoints require authentication via JWT Bearer token unless otherwise specified.

**Header Format:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Error Responses:**
- `401 Unauthorized` - Missing or invalid authentication token
- `403 Forbidden` - User does not have permission to access the resource

---

## 📦 Delivery Status Lifecycle

Deliveries automatically progress through the following statuses:

1. **PREPARING** - Initial status when delivery is created (default)
2. **SHIPPED** - Delivery is in transit (auto-transitions 2 hours before delivery time)
3. **DELIVERED** - Delivery has arrived (auto-transitions at delivery time)
4. **CONFIRMED** - User has confirmed receipt (manual confirmation only)

---

## 👤 User Delivery Endpoints

All user endpoints are under `/api/deliveries` and require authentication.

### 1. Get Current Delivery

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
    },
    {
      "mealId": 2,
      "name": "Quinoa Bowl",
      "calories": 400,
      "protein": 15,
      "carbs": 60,
      "fats": 12
    },
    {
      "mealId": 3,
      "name": "Salmon with Vegetables",
      "calories": 450,
      "protein": 40,
      "carbs": 25,
      "fats": 20
    }
  ]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `404 Not Found` - No delivery found for today

**Example Error (404):**
```json
{
  "message": "No active delivery found for today",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Requirements:** 7.1, 4.1

---

### 2. Get Delivery by ID

Get details of a specific delivery by ID. Users can only access their own deliveries.

**Endpoint:** `GET /api/deliveries/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id` (Integer) - Delivery ID

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
- `403 Forbidden` - Delivery does not belong to the authenticated user
- `404 Not Found` - Delivery not found

**Example Error (403):**
```json
{
  "message": "You do not have permission to access this delivery",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Requirements:** 7.2, 4.2

---

### 3. Get Delivery History

Get paginated delivery history for the authenticated user with optional filtering.

**Endpoint:** `GET /api/deliveries/history`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `startDate` (optional) - Filter by start date (format: YYYY-MM-DD)
- `endDate` (optional) - Filter by end date (format: YYYY-MM-DD)
- `status` (optional) - Filter by status (PREPARING, SHIPPED, DELIVERED, CONFIRMED)
- `page` (default: 0) - Page number (zero-indexed)
- `size` (default: 20) - Items per page

**Example Request:**
```
GET /api/deliveries/history?startDate=2024-01-01&endDate=2024-01-31&status=DELIVERED&page=0&size=10
```

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
    },
    {
      "deliveryId": 124,
      "deliveryDate": "2024-01-13",
      "status": "DELIVERED",
      "deliveryTime": "18:00",
      "confirmed": false,
      "mealCount": 3
    }
  ],
  "totalPages": 3,
  "totalElements": 30,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated

**Requirements:** 7.4, 4.5, 10.1

---

### 4. Update Delivery Preferences

Update delivery time and/or address for a delivery. Only allowed when delivery status is PREPARING.

**Endpoint:** `PATCH /api/deliveries/{id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Path Parameters:**
- `id` (Integer) - Delivery ID

**Request Body:** (at least one field required)
```json
{
  "deliveryTime": "19:30",
  "address": "456 Oak Avenue, City, Country"
}
```

**Field Validations:**
- `deliveryTime` - Must be in HH:mm format (24-hour)
- `address` - Must be between 5 and 255 characters

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
- `403 Forbidden` - Delivery does not belong to the authenticated user
- `404 Not Found` - Delivery not found
- `400 Bad Request` - Invalid input or delivery has already shipped

**Example Error (400 - Already Shipped):**
```json
{
  "message": "Cannot update delivery preferences. Delivery has already shipped",
  "timestamp": "2024-01-15T17:00:00"
}
```

**Example Error (400 - Invalid Time Format):**
```json
{
  "message": "Invalid time format. Expected HH:mm",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Example Error (400 - Invalid Address):**
```json
{
  "message": "Address must be between 5 and 255 characters",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Requirements:** 8.1, 8.2, 8.3

---

### 5. Confirm Delivery

Confirm receipt of a delivered order. Only allowed when delivery status is DELIVERED.

**Endpoint:** `POST /api/deliveries/{id}/confirm`

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id` (Integer) - Delivery ID

**Request Body:** None

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
- `403 Forbidden` - Delivery does not belong to the authenticated user
- `404 Not Found` - Delivery not found
- `400 Bad Request` - Delivery is not in DELIVERED status

**Example Error (400 - Wrong Status):**
```json
{
  "message": "Cannot confirm delivery. Delivery must be in 'Delivered' status",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Note:** If a delivery is already confirmed, the endpoint returns success (idempotent operation).

**Requirements:** 7.3, 5.1

---

## 👨‍💼 Admin Delivery Endpoints

All admin endpoints are under `/api/admin/deliveries` and require ADMIN role.

### 6. List All Deliveries (Admin)

Get paginated list of all deliveries with optional filtering. Admin only.

**Endpoint:** `GET /api/admin/deliveries`

**Headers:**
```
Authorization: Bearer {token}
```

**Required Role:** ADMIN

**Query Parameters:**
- `status` (optional) - Filter by status (PREPARING, SHIPPED, DELIVERED, CONFIRMED)
- `date` (optional) - Filter by delivery date (format: YYYY-MM-DD)
- `userId` (optional) - Filter by user ID
- `userEmail` (optional) - Filter by user email (partial match)
- `page` (default: 0) - Page number (zero-indexed)
- `size` (default: 20) - Items per page

**Example Request:**
```
GET /api/admin/deliveries?status=DELIVERED&date=2024-01-15&page=0&size=20
```

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
      "meals": [
        {
          "mealId": 1,
          "name": "Grilled Chicken Salad",
          "calories": 350,
          "protein": 35,
          "carbs": 20,
          "fats": 15
        }
      ],
      "statusHistory": []
    }
  ],
  "totalPages": 5,
  "totalElements": 95,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User does not have ADMIN role

**Requirements:** 9.1, 9.5

---

### 7. Get Delivery Details (Admin)

Get complete delivery details including user information and status history. Admin only.

**Endpoint:** `GET /api/admin/deliveries/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Required Role:** ADMIN

**Path Parameters:**
- `id` (Integer) - Delivery ID

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
  "meals": [
    {
      "mealId": 1,
      "name": "Grilled Chicken Salad",
      "calories": 350,
      "protein": 35,
      "carbs": 20,
      "fats": 15
    },
    {
      "mealId": 2,
      "name": "Quinoa Bowl",
      "calories": 400,
      "protein": 15,
      "carbs": 60,
      "fats": 12
    }
  ],
  "statusHistory": [
    {
      "status": "PREPARING",
      "timestamp": "2024-01-15T08:00:00",
      "updatedBy": "SYSTEM"
    },
    {
      "status": "SHIPPED",
      "timestamp": "2024-01-15T16:00:00",
      "updatedBy": "SYSTEM"
    },
    {
      "status": "DELIVERED",
      "timestamp": "2024-01-15T18:00:00",
      "updatedBy": "SYSTEM"
    }
  ]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User does not have ADMIN role
- `404 Not Found` - Delivery not found

**Requirements:** 9.2, 9.4

---

### 8. Update Delivery Status (Admin)

Manually update delivery status. Admin only.

**Endpoint:** `PATCH /api/admin/deliveries/{id}/status`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Required Role:** ADMIN

**Path Parameters:**
- `id` (Integer) - Delivery ID

**Request Body:**
```json
{
  "status": "DELIVERED"
}
```

**Valid Status Values:**
- `PREPARING`
- `SHIPPED`
- `DELIVERED`
- `CONFIRMED`

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
  "meals": [
    {
      "mealId": 1,
      "name": "Grilled Chicken Salad",
      "calories": 350,
      "protein": 35,
      "carbs": 20,
      "fats": 15
    }
  ],
  "statusHistory": [
    {
      "status": "PREPARING",
      "timestamp": "2024-01-15T08:00:00",
      "updatedBy": "SYSTEM"
    },
    {
      "status": "DELIVERED",
      "timestamp": "2024-01-15T17:30:00",
      "updatedBy": "admin@example.com"
    }
  ]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User does not have ADMIN role
- `404 Not Found` - Delivery not found
- `400 Bad Request` - Invalid status value

**Example Error (400 - Invalid Status):**
```json
{
  "message": "Status must be one of: PREPARING, SHIPPED, DELIVERED, CONFIRMED",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Requirements:** 9.3

---

## 📋 Data Models

### DeliveryResponseDto

User-facing delivery response object.

```json
{
  "deliveryId": 123,
  "deliveryDate": "2024-01-15",
  "deliveryTime": "18:00",
  "address": "123 Main St, City, Country",
  "status": "DELIVERED",
  "statusUpdatedAt": "2024-01-15T18:00:00",
  "confirmedAt": "2024-01-15T18:15:30",
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

**Fields:**
- `deliveryId` (Integer) - Unique delivery identifier
- `deliveryDate` (String) - Delivery date in YYYY-MM-DD format
- `deliveryTime` (String) - Delivery time in HH:mm format (24-hour)
- `address` (String) - Delivery address
- `status` (String) - Current delivery status
- `statusUpdatedAt` (String) - ISO 8601 timestamp of last status update
- `confirmedAt` (String, nullable) - ISO 8601 timestamp of confirmation
- `meals` (Array) - List of meals in the delivery

---

### DeliveryHistoryDto

Simplified delivery object for history listing.

```json
{
  "deliveryId": 123,
  "deliveryDate": "2024-01-15",
  "status": "CONFIRMED",
  "deliveryTime": "18:00",
  "confirmed": true,
  "mealCount": 3
}
```

**Fields:**
- `deliveryId` (Integer) - Unique delivery identifier
- `deliveryDate` (String) - Delivery date in YYYY-MM-DD format
- `status` (String) - Current delivery status
- `deliveryTime` (String) - Delivery time in HH:mm format
- `confirmed` (Boolean) - Whether delivery has been confirmed
- `mealCount` (Integer) - Number of meals in the delivery

---

### AdminDeliveryDto

Extended delivery object with user and subscription information for admins.

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
  "meals": [],
  "statusHistory": []
}
```

**Additional Fields (beyond DeliveryResponseDto):**
- `createdAt` (String) - ISO 8601 timestamp of delivery creation
- `userId` (Integer) - User ID who owns the delivery
- `userEmail` (String) - User's email address
- `userName` (String) - User's full name
- `subscriptionId` (Integer) - Associated subscription ID
- `subscriptionPlan` (String) - Name of the subscription plan
- `statusHistory` (Array) - History of status changes

---

### UpdateDeliveryDto

Request body for updating delivery preferences.

```json
{
  "deliveryTime": "19:30",
  "address": "456 Oak Avenue, City, Country"
}
```

**Fields (at least one required):**
- `deliveryTime` (String, optional) - New delivery time in HH:mm format
- `address` (String, optional) - New delivery address (5-255 characters)

---

### UpdateDeliveryStatusDto

Request body for admin status updates.

```json
{
  "status": "DELIVERED"
}
```

**Fields:**
- `status` (String, required) - New status (PREPARING, SHIPPED, DELIVERED, CONFIRMED)

---
### StatusHistoryDto

Status change history entry.

```json
{
  "status": "DELIVERED",
  "timestamp": "2024-01-15T18:00:00",
  "updatedBy": "SYSTEM"
}
```

**Fields:**
- `status` (String) - Status at this point in history
- `timestamp` (String) - ISO 8601 timestamp of the change
- `updatedBy` (String) - Who made the change (SYSTEM or admin email)

---

## 🔄 Automatic Status Progression

The system automatically progresses delivery statuses based on time:

### Progression Rules

1. **PREPARING → SHIPPED**
   - Triggers: 2 hours before delivery time
   - Example: If delivery time is 18:00, transitions at 16:00

2. **SHIPPED → DELIVERED**
   - Triggers: At delivery time
   - Example: If delivery time is 18:00, transitions at 18:00

3. **DELIVERED → CONFIRMED**
   - Triggers: Manual user confirmation only
   - No automatic transition

### Scheduler Jobs

**Daily Delivery Creation:**
- Runs: Daily at midnight (00:00)
- Action: Creates deliveries for all active subscriptions for the current day
- Status: New deliveries start in PREPARING status

**Status Progression:**
- Runs: Every minute
- Action: Updates deliveries in PREPARING or SHIPPED status based on time
- Catch-up: If a delivery is late, it immediately progresses to the correct status

---

## 🚨 Error Response Format

All error responses follow a consistent format:

```json
{
  "message": "Error description",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Common HTTP Status Codes

- `200 OK` - Request succeeded
- `201 Created` - Resource created successfully
- `204 No Content` - Request succeeded with no response body
- `400 Bad Request` - Invalid input or business rule violation
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---
## 🧪 Testing Examples

### cURL Examples

#### Get Current Delivery
```bash
curl -X GET http://localhost:8080/api/deliveries/current \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Update Delivery Preferences
```bash
curl -X PATCH http://localhost:8080/api/deliveries/123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryTime": "19:30",
    "address": "456 Oak Avenue, City, Country"
  }'
```

#### Confirm Delivery
```bash
curl -X POST http://localhost:8080/api/deliveries/123/confirm \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Get Delivery History (with filters)
```bash
curl -X GET "http://localhost:8080/api/deliveries/history?startDate=2024-01-01&endDate=2024-01-31&status=DELIVERED&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Admin: List All Deliveries
```bash
curl -X GET "http://localhost:8080/api/admin/deliveries?status=DELIVERED&page=0&size=20" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```

#### Admin: Update Delivery Status
```bash
curl -X PATCH http://localhost:8080/api/admin/deliveries/123/status \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "DELIVERED"
  }'
```

---

### JavaScript/TypeScript Examples

```javascript
// Get current delivery
const getCurrentDelivery = async () => {
  const response = await fetch('http://localhost:8080/api/deliveries/current', {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    }
  });
  
  if (response.ok) {
    const delivery = await response.json();
    console.log('Current delivery:', delivery);
  } else if (response.status === 404) {
    console.log('No delivery for today');
  }
};

// Update delivery preferences
const updateDeliveryPreferences = async (deliveryId, updates) => {
  const response = await fetch(`http://localhost:8080/api/deliveries/${deliveryId}`, {
    method: 'PATCH',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(updates)
  });
  
  if (response.ok) {
    const updatedDelivery = await response.json();
    console.log('Delivery updated:', updatedDelivery);
  } else {
    const error = await response.json();
    console.error('Update failed:', error.message);
  }
};

// Confirm delivery
const confirmDelivery = async (deliveryId) => {
  const response = await fetch(`http://localhost:8080/api/deliveries/${deliveryId}/confirm`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    }
  });
  
  if (response.ok) {
    const confirmedDelivery = await response.json();
    console.log('Delivery confirmed:', confirmedDelivery);
  } else {
    const error = await response.json();
    console.error('Confirmation failed:', error.message);
  }
};

// Get delivery history with pagination
const getDeliveryHistory = async (page = 0, size = 20, filters = {}) => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    ...filters
  });
  
  const response = await fetch(`http://localhost:8080/api/deliveries/history?${params}`, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    }
  });
  
  if (response.ok) {
    const history = await response.json();
    console.log('Delivery history:', history);
    return history;
  }
};

// Admin: Get all deliveries with filters
const adminGetAllDeliveries = async (filters = {}, page = 0, size = 20) => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    ...filters
  });
  
  const response = await fetch(`http://localhost:8080/api/admin/deliveries?${params}`, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
    }
  });
  
  if (response.ok) {
    const deliveries = await response.json();
    console.log('All deliveries:', deliveries);
    return deliveries;
  }
};

// Admin: Update delivery status
const adminUpdateStatus = async (deliveryId, newStatus) => {
  const response = await fetch(`http://localhost:8080/api/admin/deliveries/${deliveryId}/status`, {
    method: 'PATCH',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('adminToken')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ status: newStatus })
  });
  
  if (response.ok) {
    const updatedDelivery = await response.json();
    console.log('Status updated:', updatedDelivery);
  }
};
```

---
## 💡 Usage Scenarios

### Scenario 1: User Checks Today's Delivery

1. User logs in and receives JWT token
2. User calls `GET /api/deliveries/current`
3. System returns delivery with status PREPARING, SHIPPED, or DELIVERED
4. User sees delivery time, address, and included meals

### Scenario 2: User Updates Delivery Time

1. User checks current delivery (status: PREPARING)
2. User realizes they'll be home later than expected
3. User calls `PATCH /api/deliveries/{id}` with new delivery time
4. System validates status is PREPARING
5. System updates delivery time and recalculates status progression
6. User receives updated delivery information

### Scenario 3: User Confirms Delivery Receipt

1. Delivery status automatically progresses to DELIVERED at delivery time
2. User receives their meals
3. User calls `POST /api/deliveries/{id}/confirm`
4. System validates status is DELIVERED
5. System updates status to CONFIRMED and records timestamp
6. User sees confirmation in their delivery history

### Scenario 4: Admin Monitors Deliveries

1. Admin logs in with admin credentials
2. Admin calls `GET /api/admin/deliveries` with filters (e.g., status=SHIPPED)
3. System returns all deliveries matching filters
4. Admin sees user information, subscription details, and status history
5. Admin can manually update status if needed

### Scenario 5: User Reviews Delivery History

1. User calls `GET /api/deliveries/history` with date range filter
2. System returns paginated list of past deliveries
3. User sees delivery dates, statuses, and confirmation status
4. User can click on specific delivery to see full details

---

## 🔧 Integration with Subscriptions

### Subscription Creation

When a user creates a subscription, they must specify a preferred delivery time:

```json
{
  "planId": 1,
  "startDate": "2024-01-01",
  "frequency": "DAILY",
  "deliveryAddress": "123 Main St, City, Country",
  "preferredTime": "18:00"
}
```

This preferred time is used for all deliveries created from that subscription.

### Daily Delivery Creation

The system automatically creates deliveries:
- **When:** Daily at midnight (00:00)
- **For:** All active subscriptions
- **Initial Status:** PREPARING
- **Delivery Time:** Inherited from subscription's preferred time
- **Address:** Inherited from subscription's delivery address

---
## 📊 Database Schema

### Delivery Table

```sql
CREATE TABLE delivery (
  delivery_id INT PRIMARY KEY AUTO_INCREMENT,
  subscription_meal_id INT NOT NULL,
  address VARCHAR(255),
  delivery_time TIME,
  status_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status_updated_at TIMESTAMP,
  confirmed_at TIMESTAMP,
  estimated_delivery_time TIME,
  FOREIGN KEY (subscription_meal_id) REFERENCES subscription_meal(subscription_meal_id),
  FOREIGN KEY (status_id) REFERENCES delivery_status(status_id)
);
```

### Delivery Status Table

```sql
CREATE TABLE delivery_status (
  status_id INT PRIMARY KEY AUTO_INCREMENT,
  status_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO delivery_status (status_name) VALUES 
  ('PREPARING'),
  ('SHIPPED'),
  ('DELIVERED'),
  ('CONFIRMED');
```

### History Table (for status tracking)

```sql
CREATE TABLE history (
  history_id INT PRIMARY KEY AUTO_INCREMENT,
  delivery_id INT NOT NULL,
  status_id INT NOT NULL,
  changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  changed_by VARCHAR(255),
  FOREIGN KEY (delivery_id) REFERENCES delivery(delivery_id),
  FOREIGN KEY (status_id) REFERENCES delivery_status(status_id)
);
```

---

## 🎯 Business Rules

### Delivery Creation Rules

1. Deliveries are only created for active subscriptions
2. One delivery per subscription per day
3. Deliveries are created at midnight for the current day
4. Initial status is always PREPARING

### Status Transition Rules

1. Status must progress in order: PREPARING → SHIPPED → DELIVERED → CONFIRMED
2. Cannot skip statuses (except via admin manual update)
3. PREPARING → SHIPPED: Automatic, 2 hours before delivery time
4. SHIPPED → DELIVERED: Automatic, at delivery time
5. DELIVERED → CONFIRMED: Manual user action only

### Update Restrictions

1. Delivery preferences can only be updated in PREPARING status
2. Once SHIPPED, no user modifications allowed
3. Admins can manually update status at any time
4. Confirmation only allowed in DELIVERED status

### Authorization Rules

1. Users can only access their own deliveries
2. Admins can access all deliveries
3. All endpoints require authentication
4. Admin endpoints require ADMIN role

---
## 🐛 Troubleshooting

### Issue: "No active delivery found for today"

**Cause:** No delivery exists for the current date
**Solutions:**
- Check if user has an active subscription
- Verify subscription start date is not in the future
- Check if daily delivery creation job ran successfully
- Verify subscription status is ACTIVE (not PAUSED or CANCELLED)

### Issue: "Cannot update delivery preferences. Delivery has already shipped"

**Cause:** Attempting to update delivery in SHIPPED or DELIVERED status
**Solutions:**
- Check delivery status before attempting update
- Updates only allowed in PREPARING status
- Contact admin for manual status adjustment if needed

### Issue: "Cannot confirm delivery. Delivery must be in 'Delivered' status"

**Cause:** Attempting to confirm delivery that hasn't been delivered yet
**Solutions:**
- Wait for delivery status to progress to DELIVERED
- Check current delivery status
- Verify automatic status progression is working

### Issue: 403 Forbidden on delivery access

**Cause:** User attempting to access another user's delivery
**Solutions:**
- Verify delivery ID belongs to authenticated user
- Check JWT token is valid and not expired
- Ensure user is accessing their own deliveries

### Issue: Admin endpoints returning 403

**Cause:** User does not have ADMIN role
**Solutions:**
- Verify user has ADMIN role in database
- Check JWT token contains correct role claim
- Re-authenticate if role was recently granted

---

## 📈 Performance Considerations

### Pagination

- Default page size: 20 items
- Maximum recommended page size: 100 items
- Use pagination for history and admin list endpoints

### Caching Recommendations

- Cache delivery status lookups (short TTL: 1 minute)
- Cache user's current delivery (TTL: 5 minutes)
- Invalidate cache on status updates

### Database Indexes

Recommended indexes for optimal performance:

```sql
CREATE INDEX idx_delivery_status ON delivery(status_id);
CREATE INDEX idx_delivery_date ON subscription_meal(delivery_date);
CREATE INDEX idx_delivery_user ON subscription_meal(subscription_id);
CREATE INDEX idx_subscription_user ON subscription(user_id);
```

---
## 🔐 Security Best Practices

### Authentication

1. Always include JWT token in Authorization header
2. Store tokens securely (HttpOnly cookies or secure storage)
3. Refresh tokens before expiration
4. Logout invalidates tokens

### Authorization

1. Users can only access their own deliveries
2. Admin role required for admin endpoints
3. Validate ownership on all user operations
4. Log all admin actions for audit trail

### Input Validation

1. Validate time format (HH:mm)
2. Validate address length (5-255 characters)
3. Validate status values against enum
4. Sanitize all user inputs

### Rate Limiting

Recommended rate limits:
- User endpoints: 100 requests per minute
- Admin endpoints: 200 requests per minute
- Status update endpoint: 10 requests per minute

---

## 📝 Changelog

### Version 1.0.0 (Initial Release)

**Features:**
- Automatic daily delivery creation for active subscriptions
- Time-based automatic status progression
- User delivery tracking and history
- Delivery preference updates (time and address)
- Delivery confirmation
- Admin monitoring and management
- Comprehensive filtering and pagination

**Endpoints:**
- `GET /api/deliveries/current` - Get current delivery
- `GET /api/deliveries/{id}` - Get delivery by ID
- `GET /api/deliveries/history` - Get delivery history
- `PATCH /api/deliveries/{id}` - Update delivery preferences
- `POST /api/deliveries/{id}/confirm` - Confirm delivery
- `GET /api/admin/deliveries` - List all deliveries (admin)
- `GET /api/admin/deliveries/{id}` - Get delivery details (admin)
- `PATCH /api/admin/deliveries/{id}/status` - Update status (admin)

---

## 📞 Support

For API support or questions:
- Check this documentation first
- Review error messages for specific guidance
- Contact development team for assistance

---

## 📚 Related Documentation

- [Main API Documentation](API_DOCUMENTATION.md) - Complete API reference
- [Authentication Guide](AUTHENTICATION.md) - Authentication details
- [Subscription API](API_DOCUMENTATION.md#-subscription-endpoints) - Subscription management

---

**Last Updated:** January 2024  
**API Version:** 1.0.0  
**Base URL:** `http://localhost:8080/api`
