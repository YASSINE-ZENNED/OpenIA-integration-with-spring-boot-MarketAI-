package com.example.MarketAI.AI.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference  // Manage the relationship
    private List<Message> messages; // One-to-many relationship with Comment

    private LocalDate dateOfLastMessage;

}
