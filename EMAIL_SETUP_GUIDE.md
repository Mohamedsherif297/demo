# Email Setup Guide - Gmail SMTP

## 🚀 Quick Setup (5 minutes)

### Step 1: Enable 2-Factor Authentication on Gmail

1. Go to your Google Account: https://myaccount.google.com/
2. Click **Security** in the left menu
3. Under "Signing in to Google", click **2-Step Verification**
4. Follow the steps to enable it

### Step 2: Generate App Password

1. Go to: https://myaccount.google.com/apppasswords
2. Select app: **Mail**
3. Select device: **Other (Custom name)**
4. Enter name: **Meal Planner Backend**
5. Click **Generate**
6. **Copy the 16-character password** (you'll need this!)

### Step 3: Update application.properties

Open `src/main/resources/application.properties` and update:

```properties
# Email Configuration (Gmail SMTP)
spring.mail.username=your-actual-email@gmail.com
spring.mail.password=your-16-char-app-password
```

**Example:**
```properties
spring.mail.username=john.doe@gmail.com
spring.mail.password=abcd efgh ijkl mnop
```

### Step 4: Test It!

Start your application and register a new user. You should receive a welcome email!

---

## 📧 What Emails Are Sent?

### 1. Welcome Email
- **When:** User registers
- **Subject:** "Welcome to Meal Planner!"
- **Contains:** Welcome message

### 2. Password Reset Email
- **When:** User requests password reset
- **Subject:** "Password Reset Request - Meal Planner"
- **Contains:** Reset link (expires in 1 hour)

### 3. Email Verification (Ready for implementation)
- **When:** User registers (not yet implemented)
- **Subject:** "Verify Your Email - Meal Planner"
- **Contains:** Verification link

---

## 🔧 Configuration Options

### Change Frontend URL
If your frontend is not at `http://localhost:3000`, update:

```properties
app.url=http://your-frontend-url.com
```

### Use Different Email Provider

#### Outlook/Hotmail
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

#### Yahoo
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

#### Custom SMTP Server
```properties
spring.mail.host=smtp.your-domain.com
spring.mail.port=587
spring.mail.username=your-email@your-domain.com
spring.mail.password=your-password
```

---

## 🐛 Troubleshooting

### Issue: "Authentication failed"
**Solution:** 
- Make sure 2-Factor Authentication is enabled
- Use App Password, not your regular Gmail password
- Remove any spaces from the app password

### Issue: "Connection timeout"
**Solution:**
- Check your internet connection
- Make sure port 587 is not blocked by firewall
- Try port 465 with SSL:
  ```properties
  spring.mail.port=465
  spring.mail.properties.mail.smtp.ssl.enable=true
  ```

### Issue: Emails go to spam
**Solution:**
- This is normal for development
- In production, use a professional email service (SendGrid, AWS SES)
- Set up SPF, DKIM, and DMARC records for your domain

### Issue: "Username and Password not accepted"
**Solution:**
- Double-check the email and app password
- Make sure there are no extra spaces
- Try generating a new app password

---

## 🔒 Security Best Practices

### For Development
✅ Use App Passwords (not your main password)
✅ Keep credentials in application.properties (not committed to Git)

### For Production
✅ Use environment variables:
```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

Then in application.properties:
```properties
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

✅ Or use a professional email service:
- **SendGrid** - 100 emails/day free
- **AWS SES** - $0.10 per 1000 emails
- **Mailgun** - 5000 emails/month free

---

## 📝 Testing Emails

### Test Password Reset
```bash
curl -X POST http://localhost:8080/api/v1/users/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com"}'
```

Check your email inbox for the reset link!

### Test Welcome Email
```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'
```

Check your email inbox for the welcome message!

---

## 🎨 Customizing Email Templates

The current emails are plain text. To use HTML templates:

1. Create HTML templates in `src/main/resources/templates/`
2. Add Thymeleaf dependency to pom.xml
3. Update EmailService to use `MimeMessage` instead of `SimpleMailMessage`

Example HTML email:
```html
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; }
        .button { background-color: #4CAF50; color: white; padding: 10px 20px; }
    </style>
</head>
<body>
    <h1>Welcome to Meal Planner!</h1>
    <p>Hello {{fullName}},</p>
    <a href="{{resetLink}}" class="button">Reset Password</a>
</body>
</html>
```

---

## ✅ Checklist

- [ ] Enable 2-Factor Authentication on Gmail
- [ ] Generate App Password
- [ ] Update `spring.mail.username` in application.properties
- [ ] Update `spring.mail.password` in application.properties
- [ ] Restart application
- [ ] Test by registering a new user
- [ ] Check email inbox (and spam folder)
- [ ] Test password reset flow

---

## 🚀 Next Steps

Once email is working:
1. Implement email verification flow
2. Add HTML email templates
3. Consider using a professional email service for production
4. Set up email analytics/tracking
5. Add unsubscribe functionality

---

## 📞 Need Help?

If emails still don't work:
1. Check application logs for error messages
2. Verify Gmail settings at https://myaccount.google.com/security
3. Try using a different email provider
4. Check if your ISP blocks SMTP ports

The application will fall back to console output if email sending fails, so you can still test the functionality!
