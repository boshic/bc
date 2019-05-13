package barcode.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by xlinux on 30.04.19.
 */
@Controller
@RequestMapping(path="/public")
public class PublicController {

    @GetMapping("/")
    public String redirectPublic() {

        return "public/public";
    }
}
