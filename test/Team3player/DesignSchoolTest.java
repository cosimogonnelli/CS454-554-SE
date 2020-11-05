package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import junit.framework.TestCase;
import static org.mockito.Mockito.*;

public class DesignSchoolTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        DesignSchool ds = new DesignSchool(r);
        ds.takeTurn();
        assert(ds.turn > 0);
    }
}