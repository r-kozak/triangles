package com.kozak.triangles.controllers;

import java.util.List;

import javax.validation.Valid;

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
import com.kozak.triangles.repositories.UserRepository;
import com.kozak.triangles.validators.SignupValidator;

@SessionAttributes("user")
@Controller
public class SignupController {
	private SignupValidator signupValidator;
	private UserRepository userRepository;

	@Autowired
	public SignupController(SignupValidator signupValidator, UserRepository userRepository) {
		this.signupValidator = signupValidator;
		this.userRepository = userRepository;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ModelAndView toHome(@Valid @ModelAttribute("user") User user, BindingResult bindResult) {

		ModelAndView mAndView = new ModelAndView();

		List<User> allUsers = userRepository.getAllUsers();
		signupValidator.validate(user, bindResult, allUsers);
		if (bindResult.hasErrors()) {
			mAndView.setViewName("signup");
			return mAndView;
		}
		userRepository.addUser(user);

		mAndView.addObject("user", user);
		RedirectView rv = new RedirectView("");
		mAndView.setView(rv);
		return mAndView;
	}
}