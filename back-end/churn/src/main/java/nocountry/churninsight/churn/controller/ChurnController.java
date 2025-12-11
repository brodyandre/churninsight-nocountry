package nocountry.churninsight.churn.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class ChurnController{

    @GetMapping(value = "/example",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String test(){
        return "teste";
    }
}