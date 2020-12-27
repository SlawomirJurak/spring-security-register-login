package pl.sgnit.springsecurityregisterlogin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.sgnit.springsecurityregisterlogin.model.AppUser;
import pl.sgnit.springsecurityregisterlogin.service.AppUserService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class MainController {

    private AppUserService userService;

    public MainController(AppUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/signup")
    public ModelAndView signup() {
        return new ModelAndView("registration", "user", new AppUser());
    }

    @PostMapping("/register")
    public ModelAndView register(AppUser user, HttpServletRequest request) {
        userService.addNewUser(user, request);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/verify-token")
    public ModelAndView verifyToken(@RequestParam String token) {
        userService.verifyToken(token);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/verify-token-admin")
    public ModelAndView verifyTokenAdmin(@RequestParam String token) {
        userService.verifyTokenAdmin(token);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/admin")
    @ResponseBody
    public String adminPage(Principal principal) {
        return "Hello admin: " + principal.getName();
    }

    @GetMapping("/user")
    @ResponseBody
    public String userPage(Principal principal) {
        return "Hello admin: " + principal.getName();
    }
}
