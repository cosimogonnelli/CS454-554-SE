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
        if((RefLocation != null && rc.getTeamSoup() > 400) || minerCount <= 5) {
            tryBuild(RobotType.MINER, randomDirection());
            ++minerCount;
        }
        else if(turn < 150) {
            tryBuild(RobotType.MINER, randomDirection());
            ++minerCount;
        }
    }
}
