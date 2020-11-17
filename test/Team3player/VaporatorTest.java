package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;

public class VaporatorTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        Vaporator vaporator = new Vaporator(r);
        vaporator.takeTurn();
        assert(vaporator.turn > 0);
    }
}