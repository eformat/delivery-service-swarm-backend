package eu.javaland.tracing.backend.rest.boundary;

import eu.javaland.tracing.backend.rest.control.DispatcherService;
import eu.javaland.tracing.backend.rest.control.LocatorService;
import eu.javaland.tracing.backend.rest.control.ReservationService;
import eu.javaland.tracing.backend.rest.entity.Drone;
import eu.javaland.tracing.backend.rest.entity.DroneReservation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Stateless
@Path("/request")
public class RequestDroneEndpoint {

    @Inject
    LocatorService locatorService;

    @Inject
    DispatcherService dispatcherService;

    @Inject
    ReservationService reservationService;

    @GET
    @Produces("application/json")
    public Response request() {

        // locates the best available drone to dispatch
        Drone drone = locatorService.locate();
        if (null == drone) {
            return Response.status(Response.Status.NOT_FOUND).entity("Sorry, no drones available.").build();
        }

        // reserves the drone for our usage
        DroneReservation reservation = reservationService.reserve(drone);

        // dispatches the drone based on the reservation ticket we got
        dispatcherService.dispatch(reservation);

        // let the caller know which drone has been dispatched
        return Response.ok(drone).build();
    }
}