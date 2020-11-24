package Team3player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Transaction;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HQTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        when(r.getRoundNum()).thenReturn(1);
        Transaction[] tran = new Transaction[1];
        when(r.getBlock(1)).thenReturn(tran);

        //HQ hq = new HQ(r);
        //hq.takeTurn();
        //assert(hq.turn > 0);
    }
}