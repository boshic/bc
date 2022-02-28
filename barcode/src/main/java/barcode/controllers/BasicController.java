package barcode.controllers;
import barcode.dao.entities.User;
import barcode.dao.services.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class BasicController {

    @Autowired
    private UserHandler userHandler;
    public BasicController() {};

    Boolean checkUserCtrlr(User user) {
        if(user == null)
            return false;
        User userDb = userHandler.getUserByName(user.getName());
        return user.getPosition().equals(userDb.getPassword());
    }

    String checkIp() {
        return "isOk";
    }
}
