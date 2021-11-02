package io.security.controller.login;

import io.security.controller.login.Dto.AccessDeniedExceptionDto;
import io.security.controller.login.Dto.LoginExceptionDto;
import io.security.domain.entity.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@ModelAttribute LoginExceptionDto loginExceptionDto, Model model) {
        model.addAttribute("error", loginExceptionDto.getError());
        model.addAttribute("exception", loginExceptionDto.getException());
        return "user/login/login";
    }

    @GetMapping("logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/login";
    }

    @GetMapping("/denied")
    public String accessDenied(@ModelAttribute AccessDeniedExceptionDto accessDeniedExceptionDto, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account account = (Account)authentication.getPrincipal();

        model.addAttribute("username", account.getUsername());
        model.addAttribute("exception", accessDeniedExceptionDto.getException());

        return "user/login/denied";
    }

}
