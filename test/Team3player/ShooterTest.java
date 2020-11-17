package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Team;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShooterTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        Shooter shooter = new Shooter(r);
        when(r.getTeam()).thenReturn(Team.NEUTRAL);
        shooter.takeTurn();
        assert(shooter.turn > 0);
    }
}