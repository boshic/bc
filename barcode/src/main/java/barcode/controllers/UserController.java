package barcode.controllers;

import barcode.dao.entities.User;
import barcode.dao.services.UserHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(path="/")
public class UserController {

    private UserHandler userHandler;

    public UserController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @GetMapping(path="/getUser")
    public @ResponseBody
    User getUser() {
        return this.userHandler.getCurrentUser();
    }

    @GetMapping(path="/getUsers")
    public @ResponseBody
    List<User> getUsers() {
        return this.userHandler.getUsers();
    }

}
