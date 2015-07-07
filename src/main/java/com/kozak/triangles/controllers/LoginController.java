package com.kozak.triangles.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.validators.LoginValidator;

@SessionAttributes("user")
@Controller
public class LoginController {

    private LoginValidator loginValidator;
    private UserRep userRepository;

    @Autowired
    public LoginController(LoginValidator loginValidator, UserRep userRepository) {
	this.loginValidator = loginValidator;
	this.userRepository = userRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
	if (!model.containsAttribute("user")) // если регистрировались, то сессия не пустая и в модели юзер есть
	    model.addAttribute("user", new User()); // иначе - добавим юзера в модель (ну и в сессию попадет автоматом)
	return "index/index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(@ModelAttribute("user") User user, BindingResult bindResult) {

	ModelAndView mAndView = new ModelAndView();

	List<User> allUsers = userRepository.getAllUsers();
	loginValidator.validate(user, bindResult, allUsers);
	if (bindResult.hasErrors()) {
	    mAndView.setViewName("index/index");
	    return mAndView;
	}

	RedirectView rv = new RedirectView("home");
	mAndView.setView(rv);
	return mAndView;
    }

    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public String exit(Model model) {
	model.addAttribute("user", new User());
	return "redirect:/";
    }
}
