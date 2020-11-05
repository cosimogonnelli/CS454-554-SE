package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;

public class FulfillmentCenterTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        FulfillmentCenter fc = new FulfillmentCenter(r);
        fc.takeTurn();
        assert(fc.turn > 0);
    }
}