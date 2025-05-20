package rencanakan.id.talentpool.service;

public interface EmailService {
    void sendResetPasswordEmail(String to, String resetLink);
    void processResetPassword(String email);
}