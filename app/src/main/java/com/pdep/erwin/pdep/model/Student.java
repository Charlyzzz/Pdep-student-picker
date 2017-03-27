package com.pdep.erwin.pdep.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class Student {

    private String name;
    private String lastName;

    public Student(List<String> row) {
        ArrayList<String> parsedRow = row.stream().map(capitalize()).collect(toCollection(ArrayList::new));
        name = parsedRow.get(0);
        lastName = parsedRow.get(1);
    }

    private Function<String, String> capitalize() {
        return (word) -> {
            List<String> words = Arrays.asList(word.split(" "));
            return words
                    .stream()
                    .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase())
                    .reduce("", (a, b) -> a + b);
        };
    }

    public String fullname() {
        return lastName + ", " + name;
    }

    @Override
    public String toString() {
        return fullname();
    }
}
