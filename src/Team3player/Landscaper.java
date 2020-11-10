package Team3player;

import battlecode.common.*;

import java.util.ArrayList;

public class Landscaper extends Unit {

    ArrayList<MapLocation> placesToDig = new ArrayList<>();
    ArrayList<MapLocation> placesToDepositDirt = new ArrayList<>();

    public Landscaper(RobotController r) {
        super(r);
    }

    /******************* LANDSCAPER STRATEGY *********************
     * Try to Dig at nearest placesToDig
     * If HQ location is known, move to it
     * If near HQ, find lowest elevation of placesToDepositDirt
     * Build a wall around HQ
     *******************************************************/
    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // We can only do work, if we can find the HQ
        // Sometimes we don't listen to the blockchain to find out where it is
        if (HQLocation.size() > 0) {
            MapLocation hq = HQLocation.get(0);
            MapLocation bestPlaceToBuildWall = null;
            int lowestElevation = 9999999;
            // These are the specific locations we want to dig at
            // Leaving room around HQ for wall and not trapping landscaper
            if (placesToDig.size() == 0) {
                placesToDig.add(hq.translate(2,2));
                placesToDig.add(hq.translate(2,-2));
                placesToDig.add(hq.translate(-2,2));
                placesToDig.add(hq.translate(-2,-2));
                placesToDig.add(hq.translate(0,2));
                placesToDig.add(hq.translate(0,-2));
                placesToDig.add(hq.translate(2,0));
                placesToDig.add(hq.translate(-2,0));
            }
            // These are the specific locations we want to build a wall
            // All around the HQ
            if (placesToDepositDirt.size() == 0) {
                placesToDepositDirt.add(hq.translate(1, 0));
                placesToDepositDirt.add(hq.translate(0, 1));
                placesToDepositDirt.add(hq.translate(-1, 0));
                placesToDepositDirt.add(hq.translate(0, -1));
                placesToDepositDirt.add(hq.translate(1, 1));
                placesToDepositDirt.add(hq.translate(1, -1));
                placesToDepositDirt.add(hq.translate(-1, 1));
                placesToDepositDirt.add(hq.translate(-1, -1));
            }

            // Find the nearest place to dig
            while (rc.getDirtCarrying() == 0) {
                MapLocation digLoc = findNearest(placesToDig);
                if (rc.getLocation().isAdjacentTo(digLoc)) {
                    Direction dir = rc.getLocation().directionTo(digLoc);
                    if (tryToDig(dir))
                        System.out.println("I got some dirt! ");
                } else {
                    goToLocation(digLoc);
                }
            }

            // If we aren't near the HQ, go to it
            // Otherwise, find the best place to build a wall by measuring elevations
            if (notNearby(RobotType.HQ)) {
                goToLocation(hq);
            } else {
                for (MapLocation depositLoc : placesToDepositDirt) {
                    // Compare locations to see which needs dirt most
                    if (rc.senseElevation(depositLoc) < lowestElevation) {
                        lowestElevation = rc.senseElevation(depositLoc);
                        bestPlaceToBuildWall = depositLoc;
                    }
                }
            }

            // If we have found the best place to build a wall, deposit dirt there
            // If we aren't close enough, go to it
            if (bestPlaceToBuildWall != null) {
                if (rc.getLocation().isAdjacentTo(bestPlaceToBuildWall) &&
                        rc.canDepositDirt(rc.getLocation().directionTo(bestPlaceToBuildWall))) {
                    rc.depositDirt(rc.getLocation().directionTo(bestPlaceToBuildWall));
                    System.out.println("Build a wall!");
                } else {
                    goToLocation(bestPlaceToBuildWall);
                }
            }
        // If we can't find the HQ yet, move randomly
        } else {
            goTo(randomDirection());
        }
    }

    /**
     * Landscaper attempts to dig
     *
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryToDig(Direction dir) throws GameActionException {
        if (rc.canDigDirt(dir)) {
            rc.digDirt(dir);
            return true;
        }
        return false;
    }
}
