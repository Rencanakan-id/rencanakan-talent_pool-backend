package rencanakan.id.talentpool.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ir") // Base path untuk semua endpoint di controller ini
public class IRController {

    public IRController() {}

    @GetMapping("/user-dto")
    public String sayHello() {
        return "Hello from IR Controller!";
    }

    @PostMapping("/user")
    public String echoMessage(@RequestBody String message) {
        return "You sent: " + message;
    }
}