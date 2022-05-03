package com.columncalcrest.controller;

import com.columncalcrest.dto.ColumnResults;
import com.columncalcrest.exception.ConcreteFailedException;
import com.columncalcrest.exception.InvalidColumnInput;
import com.columncalcrest.exception.MaxIterationsExceededException;
import com.columncalcrest.exception.RebarFailedException;
import com.columncalcrest.service.ColumnService;
import com.columncalcrest.wrapper.ColumnWrapper;
import com.columncalcrest.service.*;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ColumnController {

    @PostMapping("/test/post")
    public ColumnResults columnResults(@NonNull @RequestBody ColumnWrapper newColumnWrapper) {


//        long startTime = System.nanoTime();
//        Runtime runtime = Runtime.getRuntime();
//        long memoryMax = runtime.maxMemory();
//        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
//        double memoryUsedPercent = (memoryUsed * 100.0) / memoryMax;


        ColumnResults columnResults;

        try {
            ColumnService columnService = new ColumnService(newColumnWrapper);

            columnResults = columnService.getColumnResults();

        } catch (SingularMatrixException |
                 MaxIterationsExceededException |
                 ConcreteFailedException |
                 RebarFailedException |
                 InvalidColumnInput exception) {

            if ( exception instanceof SingularMatrixException ) {
                System.out.println(exception.getMessage());
                columnResults = new ColumnResults((SingularMatrixException) exception);
            } else if ( exception instanceof MaxIterationsExceededException ) {
                System.out.println(exception.getMessage());
                columnResults = new ColumnResults((MaxIterationsExceededException) exception);
            } else if ( exception instanceof  ConcreteFailedException ){
                System.out.println(exception.getMessage());
                columnResults = new ColumnResults((ConcreteFailedException) exception);
            } else if ( exception instanceof  InvalidColumnInput ){
                System.out.println((exception.getMessage()));
                columnResults = new ColumnResults((InvalidColumnInput) exception);
            } else {
                System.out.println((exception.getMessage()));
                columnResults = new ColumnResults((RebarFailedException) exception);
            }
        }


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
