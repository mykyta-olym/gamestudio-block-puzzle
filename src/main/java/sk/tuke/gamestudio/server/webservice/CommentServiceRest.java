package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.services.CommentException;
import sk.tuke.gamestudio.services.CommentService;

import java.util.Date;
import java.util.List;

// Вказує, що цей клас є REST контролером, тобто він обробляє HTTP запити.
@RestController
// Визначає базовий шлях для всіх методів у цьому контролері.
@RequestMapping("/comments")
public class CommentServiceRest {

    // Змінна для сервісу, що використовується для роботи з коментарями.
    private final CommentService commentService;

    // Автоматичне введення залежності CommentService через конструктор через бін.
    @Autowired
    public CommentServiceRest(CommentService commentService) {
        this.commentService = commentService;
    }

    // Обробка POST запитів для додавання нового коментаря. Вказує статус HTTP 201 при успішному додаванні.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addComment(@RequestBody Comment comment) {
        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment object cannot be null");
        }
        if (comment.getGame() == null || comment.getGame().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game name cannot be empty");
        }
        try {
            comment.setCreatedOn(new Date());
            commentService.addComment(comment);
        } catch (CommentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding comment: " + e.getMessage(), e);
        }
    }

    // Обробка GET запитів для отримання коментарів за назвою гри.
    @GetMapping("/{game}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String game) {
        if (game == null || game.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<Comment> comments = commentService.getComments(game);
            if (comments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(comments);
        } catch (CommentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving comments: " + e.getMessage(), e);
        }
    }

    // Обробка DELETE запитів для скидання (видалення) всіх коментарів. Вказує статус HTTP 204 при успішному скиданні.
    @DeleteMapping("/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset() {
        try {
            commentService.reset();
        } catch (CommentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resetting comments: " + e.getMessage(), e);
        }
    }
}
