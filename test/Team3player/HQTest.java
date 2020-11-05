package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;

public class HQTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        HQ hq = new HQ(r);
        hq.takeTurn();
        assert(hq.turn > 0);
    }
}