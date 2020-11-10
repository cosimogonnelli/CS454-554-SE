package Team3player;
import battlecode.common.*;

public class Drone extends Unit {

    public Drone(RobotController r) {
        super(r);
    }

    /******************* DRONE STRATEGY *********************
     * Move randomly, until enemy found
     * Pick up enemy to take it out of play
     *******************************************************/
    public void takeTurn() throws GameActionException {
        super.takeTurn();
        if(HQLocation.size() > 0)
            goToLocation(HQLocation.get(0));
        else {
            goTo(randomDirection());
        }
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED,enemy);

            if (robots.length > 0 && robots[0].type != RobotType.DELIVERY_DRONE && robots[0].type != RobotType.FULFILLMENT_CENTER && robots[0].type != RobotType.HQ && robots[0].type != RobotType.DESIGN_SCHOOL && robots[0].type != RobotType.REFINERY) {
                // Pick up a first robot within range
                if(robots[0].type == RobotType.LANDSCAPER || robots[0].type == RobotType.MINER) {
                    rc.pickUpUnit(robots[0].getID());
                    System.out.println("I picked up " + robots[0].getID() + "!");
                }
            }
        } else {
            // No close robots, so search for robots within sight radius
            for(Direction dir: directions){
                //if(rc.senseFlooding(rc.adjacentLocation(dir))){
                    if(rc.canDropUnit(dir)) {
                        rc.dropUnit(dir);
                        System.out.println("I killed a unit!");
                    }
                //}
            }
            //if(rc.canDropUnit(randomDirection()))
            //    rc.dropUnit(randomDirection());
        }
    }
}
