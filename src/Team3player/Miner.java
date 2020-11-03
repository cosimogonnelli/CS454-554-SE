package Team3player;
import battlecode.common.*;
import java.util.ArrayList;

public class Miner extends Unit {

    ArrayList<MapLocation> soupMap = new ArrayList<>();
    ArrayList<MapLocation> refineryMap = new ArrayList<>();

    public Miner(RobotController r) {
        super(r);
    }

    /******************* MINER STRATEGY *********************
     * 1 - Build refinery if possible
     * 2 - Look around and try to refine
     * 3 - If we can't refine we try to mine
     * 4 - When full of soup we go to a refinery
     * 5 - Try to build a design school
     * 6 - Try to build a fulfillment center
     *******************************************************/
    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Check the blockchain for soup locations
        radio.updateMap(soupMap, 1);
        // Check the blockchain for refinery locations
        radio.updateMap(refineryMap, 2);
        // Check if nearby soup is depleted
        updateSoupMap();

        // Builds refinery and creates location pointer to it
        // distance from HQ > some good amount
        // is next to soup
        if (turn > 450) {
            Direction dir = randomDirection();
            if (tryBuild(RobotType.REFINERY, dir)) {
                MapLocation refineryLoc = rc.getLocation().add(dir);
                System.out.println("A refinery was built!");
                refineryMap.add(refineryLoc);
                radio.shareLocation(refineryLoc, 2);
            }
        }

        // Add any new refinery locations discovered
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots) {
            if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam())
                refineryMap.add(robot.location);
        }

        // Loop in all directions and try to refine in that direction
        for (Direction dir : directions) {
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        }

        // If we can't refine we than try to Mine.
        // Check again all direction and try to mine
        for (Direction dir : directions) {
            if (tryMine(dir)) {
                System.out.println("I mined soup! " + rc.getSoupCarrying());
                MapLocation soupLoc = rc.getLocation().add(dir);
                if (!soupMap.contains(soupLoc)) {
                    radio.shareLocation(soupLoc, 1);
                    soupMap.add(soupLoc);
                }
            }
        }

        // With max soup limit, go to nearest refinery
        // If no refineries, go refine at HQ
        // Under max soup limit, go to nearest soup location
        // If no known soup locations, move randomly
        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            System.out.println("I'm full of soup.");
            if (refineryMap.size() > 0) {
                System.out.println("Going to the nearest refinery");
                goToLocation(refineryMap.get(0));
            } else {
                System.out.println("No refineries, going to HQ");
                goToLocation(HQLocation.get(0));
            }
        } else if (soupMap.size() > 0) {
            goToLocation(soupMap.get(0));
        } else {
            System.out.println("Keep moving around to get Soup: " + rc.getSoupCarrying());
            goTo(randomDirection());
        }

        if (!checkNearby(RobotType.DESIGN_SCHOOL) && turn > 30) {
            if (tryBuild(RobotType.DESIGN_SCHOOL, randomDirection()))
                System.out.println("A design school was built!");
        }
        if (!checkNearby(RobotType.FULFILLMENT_CENTER)) {
            if (tryBuild(RobotType.FULFILLMENT_CENTER, randomDirection()))
                System.out.println("A fulfillment center has been built");
        }
//        if (goTo(randomDirection())) {
//            System.out.println("I moved!");
//        }
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }

    void updateSoupMap() throws GameActionException {
        int numSoupLocations = soupMap.size();
        for(int i = 0; i < numSoupLocations; i++) {
            MapLocation soup = soupMap.get(i);
            if (rc.canSenseLocation(soup) && rc.senseSoup(soup) == 0) {
                soupMap.remove(i);
            }
        }
    }
}
