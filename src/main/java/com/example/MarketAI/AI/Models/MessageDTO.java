package com.example.MarketAI.AI.Models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long ConversationID;

    private String content;

    private Sender sender;

    private List<String> photos;

    private Long timestamp;

}
