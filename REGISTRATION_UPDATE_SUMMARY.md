# Registration Update Summary

## Changes Made

The registration endpoint has been updated to accept additional user attributes during signup, eliminating the need for a separate profile update step.

## Updated Registration Payload

### Endpoint
`POST /api/v1/users/register`

### Request Body

**Required Fields:**
- `fullName` - User's full name (1-255 characters)
- `email` - Valid email address (unique)
- `password` - Password (minimum 8 characters)

**Optional Fields (New):**
- `phoneNumber` - Phone number (max 20 characters)
- `address` - Delivery address (max 500 characters)
- `photoUrl` - Profile photo URL (max 500 characters)
- `dob` - Date of birth (YYYY-MM-DD format, must be in the past)
- `weight` - Weight in kg (0-500)
- `height` - Height in cm (0-300)
- `gymDays` - Number of gym days per week (0-7)
- `weightGoal` - Weight goal (e.g., "lose", "gain", "maintain")
- `weeklyDuration` - Weekly exercise duration in minutes
- `caloriesPerDay` - Target daily calorie intake (0-10000)

### Example Request

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123",
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

### Minimal Request (Still Works)

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

## Files Modified

1. **UserRegistrationDto.java** - Added all optional profile fields with validation
2. **UserController.java** - Updated registration handler to set optional fields on User entity
3. **API_DOCUMENTATION.md** - Updated documentation with new payload structure

## Benefits

- Users can complete their profile during registration
- Reduces friction in the onboarding process
- All fields remain optional (except fullName, email, password)
- Backward compatible - minimal registration still works
- Validation is applied to all fields
