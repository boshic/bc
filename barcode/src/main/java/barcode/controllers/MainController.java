package barcode.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path="/")
public class MainController {

    @GetMapping("/login")
    public String redirectLogin() {
        return "login";
    }

    @GetMapping("/home")
    public String redirectHome() {

        return "home";
    }

    @GetMapping("/mainPage")
    public String redirectMainPage() {
        return "mainPage";
    }

//    @GetMapping("/public")
//    public String redirectPublic() {
//
//        return "public";
//    }


    @GetMapping("/")
    public String redirectRoot() {
        return "redirect:/public/";
    }

}

