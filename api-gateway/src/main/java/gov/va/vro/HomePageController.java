package gov.va.vro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
class HomePageController {
    final HomePageModel homePageModel;

    @GetMapping("/")
    String index(final Model model) {
        model.addAttribute("model", homePageModel);
        return "index";
    }
}
