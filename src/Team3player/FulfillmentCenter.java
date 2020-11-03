package Team3player;
import battlecode.common.*;

public class FulfillmentCenter extends Robot {
    int FcCount = 0;
    public FulfillmentCenter(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        if(FcCount < 4) {
            for (Direction dir : directions) {
                if (tryBuild(RobotType.DELIVERY_DRONE, dir)) {
                    FcCount++;
                }

            }
        }
    }
}
