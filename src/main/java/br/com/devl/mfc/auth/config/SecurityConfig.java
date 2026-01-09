package br.com.devl.mfc.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.devl.mfc.auth.exception.SecurityExceptionHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtFilter jwtFilter;
    private final SecurityExceptionHandler securityExceptionHandler;
	
	public SecurityConfig(JwtFilter jwtFilter, SecurityExceptionHandler securityExceptionHandler) {
		this.jwtFilter = jwtFilter;
		this.securityExceptionHandler = securityExceptionHandler;
	}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(
					session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.exceptionHandling(ex -> ex
					.authenticationEntryPoint(securityExceptionHandler)
			)
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/auth/**", "/h2-console/**").permitAll()
					.anyRequest().authenticated()
			)
			//RENDER H2 ON BROWSER(NO BLOCKS)
			.headers(headers -> 
				headers.frameOptions(frame -> frame.disable())
			)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
		
	}
    
    @Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
	
}
