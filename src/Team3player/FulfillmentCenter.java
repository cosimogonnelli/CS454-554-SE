package Team3player;
import battlecode.common.*;

public class FulfillmentCenter extends Robot {

    public FulfillmentCenter(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }
}
