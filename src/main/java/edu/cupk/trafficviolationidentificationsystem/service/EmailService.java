package edu.cupk.trafficviolationidentificationsystem.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}