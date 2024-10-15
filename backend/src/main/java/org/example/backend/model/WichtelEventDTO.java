package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
public class WichtelEventDTO {
    WichtelUserDTO organizer;
    @With
    String title;
    @With
    String description;
    @With
    String budget;
    @With
    String image;
    @With
    LocalDateTime drawDate;
    @With
    LocalDateTime giftExchangeDate;
    List<WichtelParticipantDTO> participants;
}
