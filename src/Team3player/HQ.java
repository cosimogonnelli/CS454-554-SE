package Team3player;
import battlecode.common.*;

public class HQ extends Robot {

    int minerCount = 0;

    public HQ(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        //Build Miners for 250 to begin, after that prioritize refineries unless there are fewer than 5 miners
        if(minerCount < 8) {
            for (Direction dir : directions)
                if (tryBuild(RobotType.MINER, randomDirection()))
                    ++minerCount;
        }
    }
}
