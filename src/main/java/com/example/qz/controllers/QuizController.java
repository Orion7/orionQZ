package com.example.qz.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.qz.dto.Question;
import com.example.qz.repositories.QuestionRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class QuizController {

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    QuestionRepository questionRepository;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String greetingForm(Authentication authentication, Model model) {
        model.addAttribute("name", authentication.getName());
        return "home";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin(Model model) {
        model.addAttribute("questions", questionRepository.findAll());
        return "admin";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String greeting(Authentication authentication, Model model) {
        model.addAttribute("name", authentication.getName());
        return "home";
    }

    @MessageMapping("/greeting")
    public void processMessageFromClient(@Payload String message, Authentication authentication) {
        template.convertAndSendToUser(authentication.getName(),
            "/queue/reply", "success");

        template.convertAndSend("/topic/info", authentication.getName());
    }

    @MessageMapping("/question")
    public void runQuestion(@Payload Question question) {
        Gson gson = new GsonBuilder().create();
        template.convertAndSend("/topic/question", gson.toJson(question));
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

}