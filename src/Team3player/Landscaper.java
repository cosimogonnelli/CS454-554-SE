package Team3player;
import battlecode.common.*;

public class Landscaper extends Unit {

    public Landscaper(RobotController r) {
        super(r);
    }

    /******************* LANDSCAPER STRATEGY *********************
     * Try to Dig
     * If HQ location is known, move to it
     * If near HQ, build wall around it
     *******************************************************/
    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (rc.getDirtCarrying() == 0) {
            if (tryToDig())
                System.out.println("I got some dirt! ");
        }
        if (HQLocation.size() > 0) {
            if (notNearby(RobotType.HQ)) {
                // Get closer to HQ
                goToLocation(HQLocation.get(0));
            } else {
                MapLocation bestPlaceToBuildWall = null;
                int lowestElevation = 9999999;
                // Loop over tiles around HQ and try to add that direction to get all 8 tiles around HQ
                for (Direction dir : directions) {
                    MapLocation tileToCheck = HQLocation.get(0).add(dir);
                    // If we are close enough to HQ and we can deposit dirt we do it
                    // 4 since the square with 0 at the center is: "212,101,212"
                    if (rc.getLocation().distanceSquaredTo(tileToCheck) < 4 &&
                            rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))) {
                        // Add dirt to the lowes elevation tile
                        if (rc.senseElevation(tileToCheck) < lowestElevation) {
                            lowestElevation = rc.senseElevation(tileToCheck);
                            bestPlaceToBuildWall = tileToCheck;
                        }
                    }
                }
                if (bestPlaceToBuildWall != null) {
                    rc.depositDirt(rc.getLocation().directionTo(bestPlaceToBuildWall));
                    System.out.println("Build a wall!");
                }
            }
        }
        // Move if HQ not found
        goTo(randomDirection());
    }

    /**
     * Landscaper attempts to dig
     *
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryToDig() throws GameActionException {
        Direction dir = randomDirection();
        if (rc.canDigDirt(dir)) {
            rc.digDirt(dir);
            return true;
        }
        return false;
    }
}
