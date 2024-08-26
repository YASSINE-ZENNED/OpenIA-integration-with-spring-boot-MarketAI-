package com.example.MarketAI.AI.Models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT e.content FROM Message e WHERE e.conversation.id = :var  ORDER BY e.id DESC LIMIT 10")
    List<String> findLast10ById(@Param("var") Long var);

}
