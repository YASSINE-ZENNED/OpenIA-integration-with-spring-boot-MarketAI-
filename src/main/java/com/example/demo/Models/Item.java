package com.example.demo.Models;

import java.util.List;

public record Item(String name, String description, List<String> KeyFeatures ,Boolean isNew) {
}
