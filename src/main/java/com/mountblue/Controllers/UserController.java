package com.mountblue.Controllers;

import com.mountblue.Service.UserService;
import com.mountblue.model.RegisterUser;
import com.mountblue.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Data
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/login")
    public ModelAndView loginForm() {
        ModelAndView modelAndView = new ModelAndView("login-page");
        User user = new User();
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView registerForm() {
        ModelAndView modelAndView = new ModelAndView("register");
        RegisterUser registerUser = new RegisterUser();
        modelAndView.addObject("registerUser", registerUser);
        modelAndView.addObject("isError",false);
        return modelAndView;
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute("registerUser") RegisterUser registerUser, @RequestParam("password") String reTypedPassword, Model  model){
        if(registerUser.getPassWord().equals(reTypedPassword)){
            try {
                userService.saveTheUser(registerUser);
            } catch (Exception e) {
                return "registerUser-Error";
            }
            return "redirect:/login";
        }
        model.addAttribute("isError",true);
        return "register";
    }

}
