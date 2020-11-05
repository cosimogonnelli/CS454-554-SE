package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Team;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DroneTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        Team team = Team.A;
        when(r.getTeam()).thenReturn(team);
        when(r.isCurrentlyHoldingUnit()).thenReturn(true);
        Drone drone = new Drone(r);
        drone.takeTurn();
        assert(drone.turn > 0);
    }
}