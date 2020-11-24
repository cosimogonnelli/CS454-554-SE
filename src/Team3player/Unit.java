package Team3player;
import battlecode.common.*;
import java.util.ArrayList;

public class Unit extends Robot {

    ArrayList<MapLocation> waterMap = new ArrayList<>();

    public Unit(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        findHQ();
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if(rc.getType() != RobotType.DELIVERY_DRONE) {
            if (rc.senseFlooding(rc.getLocation().add(dir))) {
                //share the location of the water on the blockchain and add to water map
                MapLocation waterLoc = rc.getLocation().add(dir);
                radio.shareLocation(waterLoc, 7);
                waterMap.add(waterLoc);
                return false;
            }
            else if (rc.isReady() && rc.canMove(dir)) {
                rc.move(dir);
                return true;
            } else return false;
        }
        else {
            if (rc.isReady() && rc.canMove(dir) ) {
                rc.move(dir);
                return true;
            } else return false;
        }
    }

    /**
     * Try to find the HQ at every turn
     * If HQ is nearby, mark it on map and share it on blockchain
     * If still not found, check blockchain for HQ location
     *
     * @throws GameActionException
     */
    void findHQ() throws GameActionException {
        if (HQLocation.size() == 0) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            if (robots != null) {
                for (RobotInfo robot : robots) {
                    if (robot.type == RobotType.HQ && robot.team == rc.getTeam()) {
                        MapLocation HQLoc = robot.location;
                        HQLocation.add(HQLoc);
                        radio.shareLocation(HQLoc, 0);
                    }
                }
            }
        }
        if (HQLocation.size() == 0) {
            radio.updateMap(HQLocation, 0);
        }
    }

    /**
     * Attempts to move in a smarter way in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean goTo(Direction dir) throws GameActionException {
        Direction[] toTry = {
                dir,
                dir.rotateRight(),
                dir.rotateRight().rotateRight(),
                dir.rotateRight().rotateRight().rotateRight()
        };
        for (Direction d : toTry) {
            if (tryMove(d))
                return true;
        }
        return false;
    }

    /**
     * Attempts to move towards target.
     *
     * @param target The target location
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean goToLocation(MapLocation target) throws GameActionException {
        return goTo(rc.getLocation().directionTo(target));
    }

    /**
     * Finds nearest resource on map to miner's current location.
     * Catches locations that are flooded and removes them from the map
     *
     * @param map of resources (soupMap, refineryMap, placesToDig, etc)
     * @return nearest resource location
     * @throws GameActionException
     */
    MapLocation findNearest(ArrayList<MapLocation> map) throws GameActionException {
        MapLocation me = rc.getLocation();
        MapLocation nearest = map.get(0);
        int distanceToNearest = me.distanceSquaredTo(nearest);
        for (int i = 1; i < map.size(); i++) {
            MapLocation I = map.get(i);
            int distanceToI = me.distanceSquaredTo(I);
            if (distanceToI < distanceToNearest) {
                nearest = I;
            }
        }
        if (rc.canSenseLocation(nearest) && rc.senseFlooding(nearest)) {
            map.remove(nearest);
            findNearest(map);
        }
        return nearest;
    }
}
