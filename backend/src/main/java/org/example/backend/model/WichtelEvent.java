package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@With
public class WichtelEvent {
    String id;
    WichtelUser organizer;
    String title;
    String description;
    String budget;
    String image;
    LocalDateTime drawDate;
    LocalDateTime giftExchangeDate;
    List<WichtelParticipant> participants;
    Map<String,WichtelParticipant> pairings;
}
