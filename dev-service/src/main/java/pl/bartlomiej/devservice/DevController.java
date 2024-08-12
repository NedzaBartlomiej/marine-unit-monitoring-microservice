package pl.bartlomiej.devservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/dev-controller")
public class DevController {
    @GetMapping
    public String hello() {
        return "Hello from dev controller.";
    }
}
