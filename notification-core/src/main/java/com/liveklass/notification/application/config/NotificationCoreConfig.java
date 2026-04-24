package com.liveklass.notification.application.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NotificationRetryProperties.class)
public class NotificationCoreConfig {
}
