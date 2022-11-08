package com.example.soccergroup.util.messaging.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@ToString
@EqualsAndHashCode
@Builder
public class PointEarnedMessage {

    private Long userId;

    private Long groupId;

    private Long meetingId;

    private double userXCoordinate;

    private double userYCoordinate;

    private int earnedPoint;

    private int earnedYear;

    private int earnedMonth;

    private int earnedDay;

    private int earnedHour;

    private int earnedMinute;

    private int earnedSeconds;

}
