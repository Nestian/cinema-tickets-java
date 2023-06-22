package uk.gov.dwp.uc.pairtest.domain;

/**
 * Utility class to obtain the total number of adult, child and infant tickets
 * given a collection of TicketTypeRequests
 * */
public class TicketRequestCounts {
    private int adultTickets = 0;
    private int childTickets = 0;
    private int infantTickets = 0;

    public TicketRequestCounts (TicketTypeRequest[] ticketTypeRequests) {
        for (TicketTypeRequest request: ticketTypeRequests) {
            int noOfTickets = request.getNoOfTickets();
            switch (request.getTicketType()) {
                case ADULT:
                    adultTickets += noOfTickets;
                    break;
                case CHILD:
                    childTickets += noOfTickets;
                    break;
                case INFANT:
                    infantTickets += noOfTickets;
                    break;
            }
        }
    }

    public int getAdultTickets() {
        return adultTickets;
    }

    public int getChildTickets() {
        return childTickets;
    }

    public int getInfantTickets() {
        return infantTickets;
    }

    public int getTotalTickets() {
        return adultTickets + childTickets + infantTickets;
    }
}
