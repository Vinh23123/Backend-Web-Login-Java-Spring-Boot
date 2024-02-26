package com.Vinh.spring.login.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.Vinh.spring.login.security.jwt.AuthEntryPointJwt;
import com.Vinh.spring.login.security.jwt.AuthTokenFilter;
import com.Vinh.spring.login.security.services.impl.UserDetailsServiceImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
//(securedEnabled = true,
//jsr250Enabled = true,
//prePostEnabled = true) // by default
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {

  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;


  private final WebClient userInfoClient;

  public WebSecurityConfig(AuthEntryPointJwt unauthorizedHandler, WebClient userInfoClient) {
    this.unauthorizedHandler = unauthorizedHandler;
    this.userInfoClient = userInfoClient;
  }

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() { // * Check carefully
    return new AuthTokenFilter();
  }



//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//            .allowedMethods("*");
//  }
//  @Override
//  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }
  
//  @Bean
//  @Override
//  public AuthenticationManager authenticationManagerBean() throws Exception {
//    return super.authenticationManagerBean();
//  }
  
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http.cors().and().csrf().disable()
//      .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//      .authorizeRequests().antMatchers("/api/auth/**").permitAll()
//      .antMatchers("/api/test/**").permitAll()
//      .anyRequest().authenticated();
//
//    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//  }
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> 
          auth
                  .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
//                  .requestMatchers(HttpMethod.PUT, "/api/v1/**").permitAll()
//                  .requestMatchers(HttpMethod.DELETE, "/api/v1/**").permitAll()
                  .requestMatchers("/api/auth/**").permitAll()
                  .requestMatchers("/v3/api-docs/**").permitAll()
                  .requestMatchers("/swagger-ui/**").permitAll()

//                  .requestMatchers("/api/v1/users/**").permitAll()
              .requestMatchers("/api/test/**").permitAll()
              .anyRequest()
                  .authenticated()
        )
            .oauth2ResourceServer(session -> session.opaqueToken(Customizer.withDefaults()));
//            .logout(logout -> logout
//                    .logoutSuccessHandler(oidcLogoutSuccessHandler())
//                    .invalidateHttpSession(true)
//                    .clearAuthentication(true)
//                    .deleteCookies("JSESSIONID")
//            );


    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
  }


//  private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
//    OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(
//            clientRegistrationRepository);
//    successHandler.setPostLogoutRedirectUri("http://localhost:8080/");
//    return successHandler;
//  }

  @Bean
  public FilterRegistrationBean corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

//    config.addAllowedOrigin("*");
    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost:4200");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");


    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
    bean.setOrder(0);
    return bean;
  }

  @Bean
  public OpaqueTokenIntrospector introspector() {
    return new GoogleOpaqueTokenIntrospector(userInfoClient);
  }
}
