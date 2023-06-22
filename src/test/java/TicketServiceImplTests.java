import org.junit.Test;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for TicketServiceImpl
 * */

public class TicketServiceImplTests {
    private TicketServiceImpl ticketService = new TicketServiceImpl();
    @Test
    public void invalidatesWrongId() {
        TicketTypeRequest typeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        // case where the id is invalid, i.e. < 0
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(-1L, typeRequest1)
        );
    }

    @Test
    public void invalidatesLargeNoOfTickets() {
        TicketTypeRequest typeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4);
        TicketTypeRequest typeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 17);
        // case where the total number of tickets is invalid, e.g. exceeds 20
        TicketTypeRequest[] multipleRequests1 = new TicketTypeRequest[2];
        multipleRequests1[0] = typeRequest1;
        multipleRequests1[1] = typeRequest2;

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(15L, multipleRequests1)
        );
    }

    @Test
    public void invalidatesTicketsWithNoAdults() {
        TicketTypeRequest typeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 4);
        TicketTypeRequest typeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        // case where there are only children tickets
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, typeRequest1)
        );

        // case where there are also infant children tickets requested
        TicketTypeRequest[] multipleRequests1 = new TicketTypeRequest[2];
        multipleRequests1[0] = typeRequest1;
        multipleRequests1[1] = typeRequest2;
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, multipleRequests1)
        );
    }

    @Test
    public void validatesInfantsAccompaniedByAdults() {
        TicketTypeRequest typeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest typeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        TicketTypeRequest[] multipleRequests1 = new TicketTypeRequest[2];
        multipleRequests1[0] = typeRequest1;
        multipleRequests1[1] = typeRequest2;
        assertDoesNotThrow(() -> ticketService.purchaseTickets(2L, multipleRequests1));
    }

    @Test
    public void invalidatesInfantsNotAccompaniedByAdults() {
        // case where there are more infants than adults to hold them
        TicketTypeRequest typeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest typeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);
        TicketTypeRequest[] multipleRequests1 = new TicketTypeRequest[2];
        multipleRequests1[0] = typeRequest1;
        multipleRequests1[1] = typeRequest2;
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, multipleRequests1)
        );
    }

    /**
     * because the payment & seat services' exposed functions return void and the methods
     * in TicketServiceImpl need to be private,  the behaviour is tested by gaining
     * access to the private methods responsible for calculating the price & seats
     **/
    private Method calculateTotalPriceMethod() throws NoSuchMethodException {
        Method method = TicketServiceImpl.class.getDeclaredMethod("calculateTotalPrice", TicketTypeRequest[].class);
        method.setAccessible(true);
        return method;
    }

    @Test
    public void calculatesTotalPrice() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        TicketTypeRequest typeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest typeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        TicketTypeRequest typeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 4);
        TicketTypeRequest[] multipleRequests1 = new TicketTypeRequest[3];
        multipleRequests1[0] = typeRequest1;
        multipleRequests1[1] = typeRequest2;
        multipleRequests1[2] = typeRequest3;
        // The total should be £80, 2 x Adult Tickets and 4 x Child Tickets.
        assertEquals(80,calculateTotalPriceMethod().invoke(
                ticketService, new Object[] {multipleRequests1}));
    }

    private Method calculateTotalSeats() throws NoSuchMethodException {
        Method method = TicketServiceImpl.class.getDeclaredMethod("calculateTotalSeats");
        method.setAccessible(true);
        return method;
    }

    @Test
    public void calculatesTotalSeats() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        TicketTypeRequest typeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        TicketTypeRequest typeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        TicketTypeRequest typeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 4);
        TicketTypeRequest[] multipleRequests1 = new TicketTypeRequest[3];
        multipleRequests1[0] = typeRequest1;
        multipleRequests1[1] = typeRequest2;
        multipleRequests1[2] = typeRequest3;
        // 5 seats need to be reserved (3 ADULT, 2 CHILD, 0 for INFANTS).
        ticketService.purchaseTickets(1L,multipleRequests1);
        assertEquals(5,calculateTotalSeats().invoke(
                ticketService));
    }
}
