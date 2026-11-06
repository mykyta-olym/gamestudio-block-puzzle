package sk.tuke.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.database.PlayerDAO_RestClient;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.BlockPuzzleGame;
import sk.tuke.gamestudio.services.*;

import java.util.Scanner;

@SpringBootApplication
@ComponentScan(basePackages = {"sk.tuke.gamestudio"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "sk.tuke.gamestudio.server.*"))
public class SpringClient {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringClient.class)
            .web(WebApplicationType.NONE)
            .run(args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    public CommentService commentService(RestTemplate restTemplate) {
        //return new CommentServiceJPA();
         return new CommentServiceRestClient(restTemplate);
    }

    @Bean
    public RatingService ratingService(RestTemplate restTemplate) {
        // return new RatingServiceJPA();
        return new RatingServiceRestClient(restTemplate);
    }

    @Bean
    public ScoreService scoreService(RestTemplate restTemplate) {
        // return new ScoreServiceJPA();
        return new ScoreServiceRestClient(restTemplate);
    }
    @Bean
    public PlayerDAO_RestClient playerDAORestClient(RestTemplate restTemplate) {
        return new PlayerDAO_RestClient(restTemplate);
    }

    @Bean
    public GameSessionService gameSessionService(RestTemplate restTemplate) {
        return new GameSessionServiceRestClient(restTemplate);
       //return new GameSessionServiceJPA();
    }
    @Bean
    public IUserAuthenticationService userAuthenticationService(RestTemplate restTemplate) {
        return new UserAuthenticationServiceRestClient(restTemplate);
    }

    @Bean
    public CommandLineRunner gameRunner(BlockPuzzleGame game) {
        return args -> game.start();
    }
}
