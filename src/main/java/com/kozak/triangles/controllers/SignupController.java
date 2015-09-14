package com.kozak.triangles.controllers;

import java.security.NoSuchAlgorithmException;
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
import com.kozak.triangles.entities.UserLicense;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.utils.Encryptor;
import com.kozak.triangles.validators.SignupValidator;

@SessionAttributes("user")
@Controller
public class SignupController {
    @Autowired
    private SignupValidator signupValidator;
    @Autowired
    private UserRep userRepository;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "index/signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView toHome(@Valid @ModelAttribute("user") User user, BindingResult bindResult)
            throws NoSuchAlgorithmException {

        ModelAndView mAndView = new ModelAndView();

        List<User> allUsers = userRepository.getAllUsers();
        signupValidator.validate(user, bindResult, allUsers);
        if (bindResult.hasErrors()) {
            mAndView.setViewName("index/signup");
            return mAndView;
        }
        // шифруем логин (нужно для куки)
        String encrLogin = Encryptor.toMD5(user.getLogin());
        user.setEncrLogin(encrLogin);

        // шифруем пароль и присваиваем юзеру
        String originPass = user.getPassword();
        String encrPass = Encryptor.toMD5(originPass);
        user.setPassword(encrPass);

        // присвоить юзеру лицензии на строительство
        UserLicense license = new UserLicense();
        user.setUserLicense(license);

        userRepository.addUser(user);

        user.setPassword(originPass); // устанавливаем обратно оригинальный пароль
        mAndView.addObject("user", user);
        RedirectView rv = new RedirectView("");
        mAndView.setView(rv);
        return mAndView;
    }
}
