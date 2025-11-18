package com.ehei.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application Spring Boot
 * 
 * @SpringBootApplication : Annotation qui combine :
 *   - @Configuration : Indique que cette classe contient des configurations
 *   - @EnableAutoConfiguration : Active la configuration automatique de Spring
 *   - @ComponentScan : Scanne les composants dans le package et sous-packages
 */
@SpringBootApplication
public class ChatApplication {

    /**
     * Point d'entrée de l'application
     * Spring Boot démarre un serveur Tomcat intégré sur le port 8080
     */
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
        System.out.println("✅ Application EHEI Chat Platform démarrée sur http://localhost:8080");
    }
}

