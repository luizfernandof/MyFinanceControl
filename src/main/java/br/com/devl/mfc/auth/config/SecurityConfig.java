package br.com.devl.mfc.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
	        .cors(Customizer.withDefaults())
	        .csrf(csrf -> csrf.disable())
	        .sessionManagement(
	                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        )
	        .exceptionHandling(ex -> ex
	                .authenticationEntryPoint(securityExceptionHandler)
	        )
	        .authorizeHttpRequests(auth -> auth
	                // Liberando as rotas de autenticação e H2
	                .requestMatchers("/auth/**", "/h2-console/**").permitAll()
	                
	                // LIBERAÇÃO COMPLETA DO SWAGGER (Caminhos Críticos)
	                .requestMatchers(
	                    "/v3/api-docs/**",          // JSON de definição
	                    "/v3/api-docs.yaml",        // YAML de definição
	                    "/swagger-ui/**",           // Recursos da interface (JS/CSS)
	                    "/swagger-ui.html",         // Página principal
	                    "/swagger-resources/**",    // Recursos extras
	                    "/webjars/**"               // Bibliotecas web
	                ).permitAll()
	                
	                .anyRequest().authenticated()
	        )
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
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
    throws Exception {
    	return authenticationConfiguration.getAuthenticationManager();
    }
	
}
