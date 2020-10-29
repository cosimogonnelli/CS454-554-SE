package Team3player;
import battlecode.common.*;

public class Landscaper extends Unit {

    public Landscaper(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (rc.getDirtCarrying() == 0) {
            if (tryToDig())
                System.out.println("I got some dirt! ");
        }
        if (HQLocation != null) {
            MapLocation bestPlaceToBuildWall = null;
            int lowestElevation = 9999999;
            // Loop over tiles around HQ and try to add that direction to get all 8 tiles around HQ
            for (Direction dir : directions) {
                MapLocation tileToCheck = HQLocation.add(dir);
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
        // Move if HQ not found
        tryMove(randomDirection());
    }

    boolean tryToDig() throws GameActionException {
        Direction dir = randomDirection();
        if (rc.canDigDirt(dir)) {
            rc.digDirt(dir);
            return true;
        }
        return false;
    }

}
