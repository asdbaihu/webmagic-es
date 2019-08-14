package app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author faith.huan 2019-08-14 10:53
 */
@Controller
@RequestMapping("/")
public class ViewController {

    @RequestMapping("/")
    public String search(){
        return "search";
    }
}
