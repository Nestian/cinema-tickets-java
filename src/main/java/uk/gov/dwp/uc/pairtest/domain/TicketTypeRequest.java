package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public class TicketTypeRequest {

    private int noOfTickets;
    private Type type;
    private TicketPrice price;

    public TicketTypeRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
        setPrice(type);
    }

    private void setPrice(Type type) {
        switch (type) {
            case ADULT:
                price = new AdultTicketPrice();
                break;
            case CHILD:
                price = new ChildTicketPrice();
                break;
            case INFANT:
                price = new InfantTicketPrice();
                break;
        }
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public int getPrice() {
        return price.getPrice();
    }

    public enum Type {
        ADULT, CHILD , INFANT
    }
}
