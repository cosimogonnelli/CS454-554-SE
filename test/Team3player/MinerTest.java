package Team3player;

import battlecode.common.*;
import junit.framework.TestCase;

import static org.mockito.Mockito.*;

public class MinerTest extends TestCase {

    public void testTakeTurn() throws GameActionException {
        RobotController r = mock(RobotController.class);
        when(r.getRoundNum()).thenReturn(1);
        Transaction[] tran = new Transaction[1];
        when(r.getBlock(1)).thenReturn(tran);

        Miner miner = new Miner(r);
        miner.takeTurn();
        assert(miner.turn > 0);
    }

    public void testTryMine() throws GameActionException {
        RobotController r = mock(RobotController.class);
        Direction dir = Direction.NORTH;
        when(r.isReady()).thenReturn(true);
        when(r.canMineSoup(any())).thenReturn(true);

        Miner miner = new Miner(r);
        boolean retVal = miner.tryMine(dir);
        assert(retVal);
    }

    public void testTryRefine() throws GameActionException {
        RobotController r = mock(RobotController.class);
        Direction dir = Direction.NORTH;
        when(r.isReady()).thenReturn(true);
        when(r.canDepositSoup(any())).thenReturn(true);

        Miner miner = new Miner(r);
        boolean retVal = miner.tryRefine(dir);
        assert(retVal);
    }

}