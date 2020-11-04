package Team3player;
import battlecode.common.*;
import java.util.ArrayList;

public class Robot {

    RobotController rc;
    Radio radio;
    ArrayList<MapLocation> HQLocation = new ArrayList<>();
    int turn = 0;

    static Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST
    };

    public Robot(RobotController r) {
        this.rc = r;
        radio = new Radio(rc);
    }

    public void takeTurn() throws GameActionException {
        turn += 1;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of build
     * @return true if a build was performed
     * @throws GameActionException
     */
    boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Makes sure no target robots are nearby
     *
     * @param target The type of the robot to check for
     * @return true if no target robots are nearby
     */
    boolean notNearby(RobotType target) {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo r : robots) {
            if (r.getType() == target) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }
}
