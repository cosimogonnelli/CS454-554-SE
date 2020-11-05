package Team3player;
import battlecode.common.*;

public class Unit extends Robot {

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
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
            rc.move(dir);
            return true;
        } else return false;
    }

    // Try to find the HQ at every turn
    // If HQ is nearby, mark it on map and share it on blockchain
    // If still not found, check blockchain for HQ location
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

    // Move in a more smart way trying direction is order
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

    // Move towards a target
    boolean goToLocation(MapLocation d) throws GameActionException {
        return goTo(rc.getLocation().directionTo(d));
    }
}
