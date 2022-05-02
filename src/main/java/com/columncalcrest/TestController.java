package com.columncalcrest;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/test")
    public Test test(@RequestParam(value = "name", defaultValue = "World") String name){
        return new Test(counter.incrementAndGet(), String.format(template, name));


    }

//    @PostMapping("/test/post")
//    public ColumnWrapper test(@RequestBody ColumnWrapper newColumnContext) {
//        return newColumnContext;
//    }
}
