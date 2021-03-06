package pl.ampv.movie_cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.ampv.movie_cart.model.Order;
import pl.ampv.movie_cart.service.OrderService;
import pl.ampv.movie_cart.service.ReviewService;
import pl.ampv.registration.model.User;
import pl.ampv.registration.service.UserService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    @GetMapping("/my_account")
    public String showAccountPage(Model model) {
        String emailUser = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("users", userService.findUserByUsername(emailUser));

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getId();

        model.addAttribute("reviews", reviewService.findByUserId(userId));


        model.addAttribute("orders", orderService.findByUserId(userId));
        return "account_page";
    }

    @GetMapping("/user/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("edited", userService.findById(id));
        return "edit_page";
    }

    @PostMapping("/user/edit/update")
    public String update(@Valid @ModelAttribute("edited") User user, Errors errors) {
        if (errors.hasErrors()) {
            log.error("Error occurred in front: " + errors.getFieldError());
            return "redirect:/user/edit/{id}";
        }
        userService.update(user);
        log.info("User updated");

        return "redirect:/my_account";
    }

    @GetMapping("/user/order/{orderId}")
    public String getUserSingleOrder(@PathVariable Long orderId, Model model) {

        model.addAttribute("order", orderService.findByOrderId(orderId));


        Order byOrderId = orderService.findByOrderId(orderId);
        model.addAttribute("copies", byOrderId.getCopies());


        return "single_order";
    }


}
