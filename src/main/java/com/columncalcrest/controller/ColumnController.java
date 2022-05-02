package com.columncalcrest.controller;

import com.columncalcrest.dto.ColumnResults;
import com.columncalcrest.service.ColumnService;
import com.columncalcrest.wrapper.ColumnWrapper;
import com.columncalcrest.service.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ColumnController {

    @PostMapping("/test/post")
    public ColumnResults columnResults(@RequestBody ColumnWrapper newColumnWrapper) {


//        long startTime = System.nanoTime();
//        Runtime runtime = Runtime.getRuntime();
//        long memoryMax = runtime.maxMemory();
//        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
//        double memoryUsedPercent = (memoryUsed * 100.0) / memoryMax;

        ColumnService columnService = new ColumnService(newColumnWrapper);

        ColumnResults columnResults = columnService.getColumnResults();

//
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime) / 10000000;  //divide by 1000000 to get milliseconds.
//        System.out.println(duration);
//        System.out.println(memoryMax);
//        System.out.println(memoryUsed);
//        System.out.println(memoryUsedPercent);


        return columnResults;
    }
}
