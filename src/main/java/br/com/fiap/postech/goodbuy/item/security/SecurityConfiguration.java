package br.com.fiap.postech.goodbuy.item.security;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "br.com.fiap.postech.goodbuy.security")
public class SecurityConfiguration {

}
