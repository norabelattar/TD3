package cleancode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restaurant {
    private final Map<LocalDateTime, Integer> reservedSeatsPerDay;
    private final List<Reservation> reservations;
    private static final int MINIMUM_OF_SEATS_PER_RESERVATION = 1;
    private static final int MINIMUM_RESERVATION_DELAY_IN_HOURS = 3;
    private static final int MAXIMUM_CAPACITY = 40;

    public Restaurant(){
        reservedSeatsPerDay = new HashMap<>();
        reservations = new ArrayList<>();
    }

    public String reserve(int numberOfSeats, String person, LocalDateTime dateTime){
        if(isReservationInvalid(numberOfSeats, dateTime)) {
            throw new ReservationInvalidException();
        }
        if (isCapacityReached(numberOfSeats, dateTime)) {
            throw new CapacityReachedException();
        }
        updateReservedSeats(numberOfSeats, dateTime);
        addReservation(numberOfSeats, person, dateTime);

        return createConfirmation(person, dateTime);
    }

    private void addReservation(int numberOfSeats, String person, LocalDateTime dateTime) {
        Reservation reservation = new Reservation(dateTime, person, numberOfSeats);
        reservations.add(reservation);
    }

    private void updateReservedSeats(int numberOfSeats, LocalDateTime dateTime) {
        reservedSeatsPerDay.put(dateTime, reservedSeatsPerDay.getOrDefault(dateTime, 0) + numberOfSeats);
    }

    private String createConfirmation(String person, LocalDateTime dateTime) {
        return String.format("%s-%s-%s", person, reservations.size(), dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    private boolean isMinimalSeatPerReservation(int numberOfSeats) {
        return numberOfSeats < MINIMUM_OF_SEATS_PER_RESERVATION;
    }

    private boolean isReservationDelayRespected(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now().plusHours(MINIMUM_RESERVATION_DELAY_IN_HOURS));
    }

    private boolean isReservationInvalid(int numberOfSeats, LocalDateTime dateTime) {
        return isMinimalSeatPerReservation(numberOfSeats) || isReservationDelayRespected(dateTime);
    }

    private boolean isCapacityReached(int numberOfSeats, LocalDateTime dateTime){
        int previousReservedSeats = getPreviouslyReservedSeats(dateTime);
        return MAXIMUM_CAPACITY - previousReservedSeats < numberOfSeats;
    }

    private int getPreviouslyReservedSeats(LocalDateTime dateTime) {
        return reservedSeatsPerDay.getOrDefault(dateTime,0);
    }
}
