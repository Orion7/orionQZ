package com.example.qz.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.example.qz.dto.AnswerDto;
import com.example.qz.entities.Answer;
import com.example.qz.entities.Question;
import com.example.qz.entities.Game;
import com.example.qz.entities.User;
import com.example.qz.repositories.AnswerRepository;
import com.example.qz.repositories.DBRepository;
import com.example.qz.repositories.GameRepository;
import com.example.qz.repositories.QuestionRepository;
import com.example.qz.repositories.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class QuizController {

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    private DBRepository dbRepository;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String greetingForm(Authentication authentication, Model model) {
        User user = userRepository.findByName(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("name", authentication.getName());
        return "home";
    }

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public String gamesForm(Authentication authentication, Model model) {
        model.addAttribute("name", authentication.getName());
        model.addAttribute("games", gameRepository.findAll());
        return "games";
    }

    @RequestMapping(value = "/addgame", params = {"addRow"})
    public String addRow(Game game, final BindingResult bindingResult) {
        game.getQuestions().add(new Question());
        return "new_game";
    }

    @RequestMapping(value = "/addgame", params = {"removeRow"})
    public String removeRow(Game game, BindingResult bindingResult, HttpServletRequest req) {
        int rowId = Integer.parseInt(req.getParameter("removeRow"));
        game.getQuestions().remove(rowId);
        return "new_game";
    }

    @RequestMapping(value = "/game/create", method = RequestMethod.GET)
    public String gameCreate(Authentication authentication, Game game, Model model) {
        return "new_game";
    }

    @RequestMapping(value = "/game/edit/{id}", method = RequestMethod.GET)
    public String gameCreate(Authentication authentication, @PathVariable("id") long id, Model model) {
        model.addAttribute(gameRepository.findById(id).orElse(new Game()));
        return "new_game";
    }

    @PostMapping("/addgame")
    public String addGame(Game game, Model model) {
        List<Question> questions = new ArrayList<>(game.getQuestions());

        game.setQuestions(Collections.emptyList());
        Game savedGame = gameRepository.save(game);
        questions.forEach(q -> {
            q.setGameId(savedGame.getId());
            if (q.getCost() == null) {
                q.setCost(100);
            }
        });

        questionRepository.saveAll(questions);

        model.addAttribute("games", gameRepository.findAll());
        return "games";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin(Model model) {
        model.addAttribute("questions", questionRepository.findAll());
        return "admin";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String greeting(Authentication authentication, Model model) {
        User user = userRepository.findByName(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("name", authentication.getName());
        return "home";
    }

    @MessageMapping("/greeting")
    public void processMessageFromClient(@Payload String nickname, Authentication authentication) {
        User user = userRepository.findByName(authentication.getName());
        user.setIsLogged(true);
        user.setNickname(nickname);
        userRepository.save(user);

//        template.convertAndSendToUser(authentication.getName(),
//            "/queue/reply", "success");

        List<User> logged = userRepository.findByIsLogged(true);
        logged.sort(Comparator.comparingInt(User::getScore).reversed());
        template.convertAndSend("/topic/info", logged);
    }

    @MessageMapping("/answer")
    public void createAnswer(@Payload String playerAnswer, Authentication authentication) {
        User user = userRepository.findByName(authentication.getName());

        Answer answer = new Answer();
        answer.setAnswer(playerAnswer);
        answer.setUser(user);
        answer.setDate(LocalDateTime.now());
        answer.setQuestion(questionRepository.findById(2L).get());
        answer.setProcessed(false);
        answerRepository.save(answer);

        template.convertAndSend("/topic/answers", answerRepository.findByProcessed(false));
    }

    @MessageMapping("/processAnswer")
    @Transactional
    public void processAnswer(@Payload AnswerDto answer) {
        Answer ans = answerRepository.findById(answer.getAnswerId()).get();
        User user = ans.getUser();
        Question question = ans.getQuestion();

        if (answer.getApproved()) {
            user.addPoints(question.getCost());
        }

        ans.setProcessed(true);
        answerRepository.save(ans);

        template.convertAndSend("/topic/answers", answerRepository.findByProcessed(false));
    }

    @MessageMapping("/question")
    public void runQuestion(@Payload Question question) {
        Gson gson = new GsonBuilder().create();
        template.convertAndSend("/topic/question", gson.toJson(question));
    }

    @Synchronized
    @MessageMapping("/ready")
    public void ready(@Payload String message, Authentication authentication) {
        if (questionRepository.getCurrent() > 2) {
            template.convertAndSend("/topic/ready", "start");
            dbRepository.resetSequence();
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

}