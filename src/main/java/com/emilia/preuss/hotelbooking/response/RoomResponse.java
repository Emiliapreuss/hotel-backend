package com.emilia.preuss.hotelbooking.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.List;

@Data
@Builder
public class RoomResponse {

    private Long id;

    private String type;

    private BigDecimal price;

    private String photo;

    private List<BookingResponse> bookings;

}
