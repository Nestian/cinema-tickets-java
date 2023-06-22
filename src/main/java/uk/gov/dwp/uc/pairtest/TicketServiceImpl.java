package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketRequestCounts;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    // tracks number of tickets of each type in the request
    private TicketRequestCounts requestCounts;
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        // validates purchase requests
        hasValidId(accountId);
        requestCounts = new TicketRequestCounts(ticketTypeRequests);
        requestedValidNoOfTickets();
        containsAdultTicket();
        allInfantsAccompaniedByAdult();

        // performs payment
        TicketPaymentService paymentService = new TicketPaymentServiceImpl();
        paymentService.makePayment(accountId, calculateTotalPrice(ticketTypeRequests));

        // books seats
        SeatReservationService seatReservationService = new SeatReservationServiceImpl();
        seatReservationService.reserveSeat(accountId, calculateTotalSeats());
    }

    private void hasValidId(Long accountId) throws InvalidPurchaseException {
        if (accountId <= 0) {
            throw new InvalidPurchaseException();
        }
    }

    private void requestedValidNoOfTickets() throws InvalidPurchaseException {
        if ((requestCounts.getTotalTickets() < 0) || (requestCounts.getTotalTickets() > 20)) {
            throw new InvalidPurchaseException();
        }
    }

    private void containsAdultTicket() throws InvalidPurchaseException {
        if (requestCounts.getAdultTickets() <= 0) {
            throw new InvalidPurchaseException();
        }
    }

    private void allInfantsAccompaniedByAdult() throws InvalidPurchaseException {
        if (requestCounts.getInfantTickets() > requestCounts.getAdultTickets()) {
            throw new InvalidPurchaseException();
        }
    }

    private int calculateTotalPrice(TicketTypeRequest[] ticketTypeRequests) {
        int total = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            total += request.getNoOfTickets() * request.getPrice();
        }
        return total;
    }

    private int calculateTotalSeats() {
        return
                requestCounts.getAdultTickets() +
                requestCounts.getChildTickets() -
                requestCounts.getInfantTickets();
    }
}
