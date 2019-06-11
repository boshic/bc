package barcode.dao.services;

import barcode.dao.entities.User;
import barcode.dao.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserHandler {

    private UserRepository userRepository;

    private UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {

        return userRepository.findAll();

    }

    public User getUserByName(String name) {

        return  userRepository.findByName(name);
    }

    public User getCurrentUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails)
            return this.getUserByName(((UserDetails)principal).getUsername());

        else
            return this.getUserByName(principal.toString());

    }

    public User checkUser(User user, String grantedUserName) {

        if (getCurrentUser() == null && user.getName().equals(grantedUserName))
            return getUserByName(grantedUserName);

        return getCurrentUser();
    }
}
