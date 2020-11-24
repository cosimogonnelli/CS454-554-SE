package Team3player;
import battlecode.common.*;


public class FulfillmentCenter extends Robot {

    public FulfillmentCenter(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        if(drones.size() < 4) {
            for (Direction dir : directions) {
                if (tryBuild(RobotType.DELIVERY_DRONE, dir)) {
                    getUnitIDs(drones, RobotType.DELIVERY_DRONE);
                    radio.shareUnitCreation(drones,8);
                }
            }
        }
    }
}
