package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;

public class RefineryTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        Refinery ref = new Refinery(r);
        ref.takeTurn();
        assert(ref.turn > 0);
    }
}