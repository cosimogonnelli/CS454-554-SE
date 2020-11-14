package Team3player;
import battlecode.common.*;

public class Shooter extends Robot{

    public Shooter(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        Team enemy = rc.getTeam().opponent();
        RobotInfo[] inRange = rc.senseNearbyRobots(GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED, enemy);

        for (RobotInfo e : inRange) {
            if(e.type == RobotType.DELIVERY_DRONE) {
                if(rc.canShootUnit(e.ID)){
                    rc.shootUnit(e.ID);
                    break;
                }
            }
        }
    }
}
