package demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")          public String home()     { return "home"; }
    @GetMapping("/products")  public String products() { return "products"; }
    @GetMapping("/about")     public String about()    { return "about"; }
    @GetMapping("/projects")  public String projects() { return "projects"; }
    @GetMapping("/news")      public String news()     { return "news"; }
    @GetMapping("/contact")   public String contact()  { return "contact"; }
}
