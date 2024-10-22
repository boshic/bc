package barcode.controllers;

import barcode.tools.xml.XmlCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path="/")
public class MainController {

    @GetMapping("/login")
    public String redirectLogin(){
        return "login";
    }


//    @RequestMapping(value = "/files", method = RequestMethod.GET)
//    @ResponseBody public FileSystemResource getFile(HttpServletResponse response) {
//        response.setContentType("application/xml");
//        return new FileSystemResource(new File("try.xml")); //Or path to your file
//    }
    @GetMapping("/home")
    public String redirectHome() throws Exception{
        XmlCreator.create();
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

