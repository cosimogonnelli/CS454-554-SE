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
    void findHQ() throws GameActionException {
        if (HQLocation == null) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.HQ && robot.team == rc.getTeam())
                    HQLocation = robot.location;
            }
        }
        // TODO: Implement to find HQ even when robot are not close enough to find it.
        // Use blockchain to broadcast location
    }

    void tryBlockchain() throws GameActionException {
        if (turn < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
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
