package com.kozak.triangles.controllers;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.Messages;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.utils.Constants;
import com.kozak.triangles.utils.RecaptchaVerifier;
import com.kozak.triangles.utils.ResponseUtil;

@SessionAttributes("user")
@Controller
public class MessageController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    /**
     * Страница беседки
     */
    @RequestMapping(value = "/arbor", method = RequestMethod.GET)
    String arbor(User user, Model model) {
		model = addMoneyInfoToModel(model, user);

        List<Messages> messages = msgRep.getAllMsgs();
        model.addAttribute("messages", messages);
        model.addAttribute("currUserLogin", user.getLogin());
        model.addAttribute("currUserAdmin", user.getLogin().equals(Constants.ADMIN_LOGIN));

        return "arbor";
    }

    /**
     * Постинг сообщения со страницы беседки
     * 
     * @throws IOException
     */
    @RequestMapping(value = "/arbor", method = RequestMethod.POST)
    String postMessage(@RequestParam("message") String message,
            @RequestParam("g-recaptcha-response") String gRecapResp, User user, Model model) throws IOException {

        if (message == null || message.trim().isEmpty() || message.length() > Constants.MSG_LEN) {
            return "redirect:/arbor";
        }

        boolean validRecaptcha = RecaptchaVerifier.verify(gRecapResp); // проверка капчи
        logger.debug("recaptcha valid: {}", validRecaptcha);

        if (validRecaptcha) {
            logger.debug("message from arbor: {}", message);

            // if (!user.getLogin().equals(Consts.ADMIN_LOGIN)) {
            // message = Jsoup.parse(message).text(); // убрать html теги, если постит не админ
            // }
            message = message.replaceAll("(\r\n|\n)", "<br/>"); // заменить переносы строки на теги
            logger.debug("msg to DB: {}", message.replaceAll("(\r\n|\n)", "<br/>"));

            // сохранить сообщение
            Messages msg = new Messages(user.getLogin(), message);
            msgRep.addMsg(msg);
        }

        return "redirect:/arbor";
    }

    /**
     * Удалить сообщение
     */
    @RequestMapping(value = "/arbor", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<String> removeMsg(@RequestParam("msgId") int msgId, User user, Model model) {

        String currUserLogin = user.getLogin();
        Messages msg = msgRep.getMsgById(msgId);

        if (currUserLogin.equals(Constants.ADMIN_LOGIN) || currUserLogin.equals(msg.getAuthor())) {
            msgRep.removeMsg(msgId);
        }

        return ResponseUtil.createTypicalResponseEntity(new JSONObject());
    }

}
