package br.com.devl.mfc.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	UrlBasedCorsConfigurationSource corsConfigurationSource(
			@Value("${cors.allowed-origins:#{null}}") List<String> allowedOrigins) {
		CorsConfiguration configuration = new CorsConfiguration();

		if (allowedOrigins != null) {
			configuration.setAllowedOrigins(allowedOrigins);
		}

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
