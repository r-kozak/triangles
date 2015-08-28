package com.kozak.triangles.validators;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.utils.Encryptor;

@Component
public class LoginValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object arg0, Errors arg1) {
    }

    public void validate(Object target, Errors errors, List<User> userList) throws NoSuchAlgorithmException {
        User user = (User) target;
        String login = user.getLogin().toLowerCase();
        String password = Encryptor.toMD5(user.getPassword());

        for (User u : userList) {
            boolean dataMatched = u.getLogin().equalsIgnoreCase(login) && u.getPassword().equals(password);
            user.setAuthenticated(dataMatched);

            if (dataMatched) // если юзер аутентифицирован - прерываем цикл
                break;
        }

        if (!user.isAuthenticated()) {
            errors.rejectValue("login", "authenticated.notCorrectLoginPassword",
                    "Please check that you have entered your login and password correctly.");
        }
    }

}
