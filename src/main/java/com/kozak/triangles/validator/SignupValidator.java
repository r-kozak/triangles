package com.kozak.triangles.validator;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.kozak.triangles.entity.User;

@Component
public class SignupValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {

    }

    public void validate(Object obj, Errors errors, List<User> userList) {
        User user = (User) obj;
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "confirmPassword.passwordDontMatch", "Пароли не совпадают!");
        }

        for (User u : userList) {
            if (u.getLogin().equalsIgnoreCase(user.getLogin())) {
                errors.rejectValue("login", "login.alreadyExist", "Пользователь с таким логином уже существует!");
            }
        }
    }
}
