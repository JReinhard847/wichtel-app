package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class WichtelEventDTO {
    @With
    String id;
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
    boolean hasPairing;

}
