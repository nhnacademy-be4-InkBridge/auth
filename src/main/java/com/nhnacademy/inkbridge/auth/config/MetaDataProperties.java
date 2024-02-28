package com.nhnacademy.inkbridge.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * class: GatewayProfile.
 *
 * @author devminseo
 * @version 2/22/24
 */
@Configuration
@ConfigurationProperties(prefix = "inkbridge")
@Getter
@Setter
public class MetaDataProperties {
    private String gateway;
    private String front;
}
