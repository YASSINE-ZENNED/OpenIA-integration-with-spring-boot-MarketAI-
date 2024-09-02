package com.example.MarketAI.AI.Models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {

    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    private List<String> photos;

    private Sender sender;

    private Long timestamp;

    @ManyToOne
    @JoinColumn(name = "conversationId", nullable = false)
    @JsonBackReference
    private Conversation conversation;
}
