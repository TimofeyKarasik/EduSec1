package org.example.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.ArrayList;

@Configuration
@ComponentScan("org.example.controller")
public class WebConfiguration {

    final ArrayList<String> whiteList = doWhiteList();

    private ArrayList<String> doWhiteList(){
        ArrayList<String> list = new ArrayList<String>();
        list.add("http://localhost:8080/");
        list.add("http://localhost:8080/errorReferer");
        list.add("http://localhost:8080/login");
        list.add("http://localhost:8080/home");
        list.add("http://localhost:8080/loguot");
        return list;
    };
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http
    ) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated());

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);
        http.formLogin(c-> c.defaultSuccessUrl("/",true));
        /*Ниже задается что у пользователя не может быть больше одной сессии*/
        http.sessionManagement(sessionManagement -> {sessionManagement.maximumSessions(1);});
        return http.build();
    }


    @Bean
    WebSecurityCustomizer globalSecurity(){
        return web -> web.requestRejectedHandler((request, response, requestRejectedException) -> {

                    response.sendRedirect(response.encodeRedirectURL("http://localhost:8080/errorReferer"));
                })
                .httpFirewall(
                        new StrictHttpFirewall() {
                            @Override
                            public FirewalledRequest getFirewalledRequest(
                                    HttpServletRequest request
                            ) throws RequestRejectedException{
                                /*
                                проверка по белому списку срабатывает при переходе с плохой ссылки.
                                 */
                                request.getRequestURI();
                                 String referer = request.getHeader("Referer");
                                if (referer != null) {
                                    if (whiteList.indexOf(referer) == -1&&!request.getRequestURI().equals("/errorReferer"))
                                    {
                                        throw new RequestRejectedException("Not in whiteList");
                                    }
                                }


                                return super.getFirewalledRequest(request);
                            }
                        }
                );
    }


}
