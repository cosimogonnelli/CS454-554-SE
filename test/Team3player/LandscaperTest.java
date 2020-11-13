package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import junit.framework.TestCase;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class LandscaperTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);

        Landscaper landscaper = new Landscaper(r);
        landscaper.takeTurn();
        assert(landscaper.turn > 0);
    }

    public void testTryToDig() throws GameActionException {
        RobotController r = mock(RobotController.class);
        when(r.canDigDirt(any())).thenReturn(true);

        Landscaper landscaper = new Landscaper(r);
        boolean retVal = landscaper.tryToDig(Robot.randomDirection());
        assert(retVal);
    }
}