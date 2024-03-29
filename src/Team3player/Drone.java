package Team3player;
import battlecode.common.*;

public class Drone extends Unit {

    public Drone(RobotController r) {
        super(r);
    }

    /******************* DRONE STRATEGY *********************
     * Move according to algorithm below, until enemy found
     * Pick up enemy/cow and move until adjacent to water
     * Drop enemy/cow into the water
     *******************************************************/
    public void takeTurn() throws GameActionException {
        super.takeTurn();
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED);
            for (RobotInfo rob: robots
                 ) {
                System.out.println(rob.type);
            }
            if (robots.length > 0) {
                // Pick up a first robot within range
                if(rc.canPickUpUnit(robots[0].getID()) && (robots[0].getTeam() == enemy || robots[0].getType() == RobotType.COW)) {
                    rc.pickUpUnit(robots[0].getID());
                }
            }
        } else {
            // Robot being held, so go dump it in water
            for(Direction dir: directions){
                if(rc.senseFlooding(rc.adjacentLocation(dir))){
                    if(rc.canDropUnit(dir)) {
                        rc.dropUnit(dir);
                        System.out.println("I Destroyed a unit!");
                    }
                }
                //move again until water is sensed
                else{
                    goTo(randomDirection());
                }
            }
        }
        /******************Drone Movement handling**********************************
         * If the drone is holding unit, move randomly until water can be found
         * If the drone is not holding a unit, go to the first enemy/cow unit sensed
         * If none are sensed, move randomly again.
         ***************************************************************************/
        RobotInfo[] gotoRobots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared());
        if(rc.isCurrentlyHoldingUnit()){
            goTo(randomDirection());
            System.out.println("Moving Randomly until enemy can be dumped in water");
        }
        else{
            if(gotoRobots != null){
                if(gotoRobots.length > 0 ) {
                    if (gotoRobots[0].getTeam() == enemy || gotoRobots[0].getType() == RobotType.COW) {
                        System.out.println("Going to " + gotoRobots[0].getType() + " " + gotoRobots[0].getID() + "!");
                        goToLocation(gotoRobots[0].getLocation());
                    }
                    else goTo(randomDirection());
                }
            else {
                goTo(randomDirection());
                    System.out.println("Moving Randomly until enemy found");
                }
            }
        }
    }

}
