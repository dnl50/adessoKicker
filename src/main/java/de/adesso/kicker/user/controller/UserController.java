package de.adesso.kicker.user.controller;

import de.adesso.kicker.ranking.service.RankingService;
import de.adesso.kicker.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final RankingService rankingService;

    @Autowired
    public UserController(UserService userService, RankingService rankingService) {
        this.userService = userService;
        this.rankingService = rankingService;
    }

    @GetMapping("/u/{id}")
    public ModelAndView getUserProfile(@PathVariable String id) {
        var modelAndView = new ModelAndView();
        var user = userService.getUserById(id);
        var rankingPosition = rankingService.getPositionOfPlayer(user.getRanking());
        modelAndView.addObject("user", user);
        modelAndView.addObject("rankingPosition", rankingPosition);
        modelAndView.setViewName("sites/profile.html");
        return modelAndView;
    }

    @GetMapping("/you")
    public ModelAndView getOwnProfile() {
        ModelAndView modelAndView = new ModelAndView();
        var user = userService.getLoggedInUser();
        var rankingPosition = rankingService.getPositionOfPlayer(user.getRanking());
        modelAndView.addObject("user", user);
        modelAndView.addObject("rankingPosition", rankingPosition);
        modelAndView.setViewName("sites/profile.html");
        return modelAndView;
    }
}
