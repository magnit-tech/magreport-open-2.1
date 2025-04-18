package ru.magnit.magreportbackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/")
public class IndexHtmlController {

    @Value("${magreport.frontend.root.path}")
    private String indexHtmlUrl;

    @GetMapping(value = "/")
    public RedirectView redirectToIndex() {
        return new RedirectView(indexHtmlUrl);
    }

    @GetMapping(value = "/ui/{*ignored}")
    public ModelAndView redirectUIToIndex(@PathVariable String ignored) {
        final var modelAndView = new ModelAndView();
        modelAndView.setViewName(indexHtmlUrl);
        return modelAndView;
    }
}
