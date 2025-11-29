package com.mealplanner.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.url:http://localhost:3000}")
    private String appUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = appUrl + "/reset-password?token=" + resetToken;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request - Meal Planner");
        message.setText(
            "Hello,\n\n" +
            "You requested to reset your password for your Meal Planner account.\n\n" +
            "Click the link below to reset your password:\n" +
            resetLink + "\n\n" +
            "This link will expire in 1 hour.\n\n" +
            "If you didn't request this, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Meal Planner Team"
        );

        try {
            mailSender.send(message);
            System.out.println("✅ Password reset email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send password reset email: " + e.getMessage());
            // Log to console as fallback
            System.out.println("==============================================");
            System.out.println("PASSWORD RESET EMAIL (Fallback)");
            System.out.println("To: " + toEmail);
            System.out.println("Reset Link: " + resetLink);
            System.out.println("==============================================");
        }
    }

    public void sendEmailVerification(String toEmail, String verificationToken) {
        String verificationLink = appUrl + "/verify-email?token=" + verificationToken;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify Your Email - Meal Planner");
        message.setText(
            "Hello,\n\n" +
            "Thank you for registering with Meal Planner!\n\n" +
            "Please verify your email address by clicking the link below:\n" +
            verificationLink + "\n\n" +
            "If you didn't create this account, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Meal Planner Team"
        );

        try {
            mailSender.send(message);
            System.out.println("✅ Verification email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send verification email: " + e.getMessage());
            // Log to console as fallback
            System.out.println("==============================================");
            System.out.println("EMAIL VERIFICATION (Fallback)");
            System.out.println("To: " + toEmail);
            System.out.println("Verification Link: " + verificationLink);
            System.out.println("==============================================");
        }
    }

    public void sendWelcomeEmail(String toEmail, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Meal Planner!");
        message.setText(
            "Hello " + fullName + ",\n\n" +
            "Welcome to Meal Planner! 🎉\n\n" +
            "We're excited to have you on board. Start planning your meals and achieving your fitness goals today!\n\n" +
            "If you have any questions, feel free to reach out to our support team.\n\n" +
            "Best regards,\n" +
            "Meal Planner Team"
        );

        try {
            mailSender.send(message);
            System.out.println("✅ Welcome email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome email: " + e.getMessage());
        }
    }
}
