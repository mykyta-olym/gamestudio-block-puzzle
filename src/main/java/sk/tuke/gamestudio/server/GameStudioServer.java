package sk.tuke.gamestudio.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.database.PlayerDAO;
import sk.tuke.gamestudio.database.PlayerDAO_JPA;
import sk.tuke.gamestudio.services.*;
@SpringBootApplication
@Configuration
@EntityScan("sk.tuke.gamestudio.entity")
@EnableJpaRepositories(basePackages = {"sk.tuke.gamestudio.services"})//Буде сканувати пакет анотованого класу конфігурації для репозиторіїв
@ComponentScan(basePackages = {"sk.tuke.gamestudio.services", "sk.tuke.gamestudio.server"})
public class GameStudioServer {
    public static void main(String[] args) {
        SpringApplication.run(GameStudioServer.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ScoreService scoreService() {
        return new ScoreServiceJPA();
    }
    @Bean
    public CommentService commentService() {
        return new CommentServiceJPA();
    }
    @Bean
    public RatingService ratingService() {
        return new RatingServiceJPA();
    }
    @Bean
    public PlayerDAO playerDAO() {
        return new PlayerDAO_JPA();
    }
    @Bean
    public GameSessionService gameSessionService() {
        return new GameSessionServiceJPA();
    }

    @Bean
    public IUserAuthenticationService userAuthenticationService() {
        return new UserAuthenticationServiceJPA();
    }
}
