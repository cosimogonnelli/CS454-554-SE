package Team3player;

import battlecode.common.*;

import java.util.*;

public class Miner extends Unit {

    ArrayList<MapLocation> soupMap = new ArrayList<>();
    ArrayList<MapLocation> refineryMap = new ArrayList<>();
    int designSchoolCount = 0;
    int fulfillmentCenterCount = 0;
    int vaporatorCount = 0;

    public Miner(RobotController r) {
        super(r);
    }

    /******************* MINER STRATEGY *********************
     * Update maps and counts / Communicate with blockchain
     * Try to build refinery if it's a good time/place to
     * Look around and try to refine
     * Look around and try to mine
     * Try to build a design school if we have less than 1
     * Try to build a fulfillment center if we have less than 1
     * When full of soup we go to nearest refinery
     * Otherwise, go to a known soup location
     * If no known soup locations, move randomly
     *******************************************************/
    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Check the blockchain for soup locations
        radio.updateMap(soupMap, 1);
        // Check the blockchain for refinery locations
        radio.updateMap(refineryMap, 2);
        // Check the blockchain updated number of fulfillment centers and design schools
        designSchoolCount += radio.updateBuildingCount(3);
        fulfillmentCenterCount += radio.updateBuildingCount(4);
        vaporatorCount += radio.updateBuildingCount(5);
        // Check if nearby soup is depleted
        updateSoupMap();

        // Add any new refinery locations discovered nearby to refineryMap
        // Share refinery location to blockchain
        RobotInfo[] robots = rc.senseNearbyRobots();
        if (robots != null) {
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam()) {
                    // Don't add duplicates to refineryMap
                    if (!refineryMap.contains(robot.location)) {
                        refineryMap.add(robot.location);
                    }
                    radio.shareLocation(robot.location, 2);
                }
            }
        }

        // Locate nearby soup
        // Unfortunately, trying to add this to the soupMap overwhelms the miner.
        MapLocation [] nearbySoup = rc.senseNearbySoup();
        if(nearbySoup != null) {
            // If there is soup nearby, determine if we should build refinery
            // If distance from other refineries and HQ > some good amount, try build refinery
            // Add it to map, share loc on blockchain
            if (!HQLocation.get(0).isWithinDistanceSquared(rc.getLocation(), 35)) {
                boolean build = false;
                if (refineryMap.size() == 0) {
                    build = true;
                } else if (refineryMap.size() < 2 && designSchoolCount > 0
                        && fulfillmentCenterCount > 0 && vaporatorCount > 0) {
                    build = !(findNearest(refineryMap).isWithinDistanceSquared(rc.getLocation(), 150));
                }
                if (build) {
                    Direction dir = randomDirection();
                    if (tryBuild(RobotType.REFINERY, dir)) {
                        MapLocation refineryLoc = rc.getLocation().add(dir);
                        System.out.println("A refinery was built!");
                        refineryMap.add(refineryLoc);
                        radio.shareLocation(refineryLoc, 2);
                    }
                }
            }
        }

        // Loop in all directions and try to refine in that direction
        for (Direction dir : directions) {
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        }

        if (notNearby(RobotType.HQ) && refineryMap.size() > 0) {
            if (designSchoolCount < 1 && rc.getTeamSoup() > 200) {
                if (tryBuild(RobotType.DESIGN_SCHOOL, randomDirection())) {
                    System.out.println("A design school was built!");
                    radio.shareBuilding(3);
                    designSchoolCount += 1;
                }
            } else if (fulfillmentCenterCount < 1 && rc.getTeamSoup() > 300) {
                if (tryBuild(RobotType.FULFILLMENT_CENTER, randomDirection())) {
                    System.out.println("A fulfillment center has been built");
                    radio.shareBuilding(4);
                    fulfillmentCenterCount += 1;
                }
            } else if (vaporatorCount < refineryMap.size() && !notNearby(RobotType.REFINERY)
                    && rc.getTeamSoup() > 600) {
                if (tryBuild(RobotType.VAPORATOR, randomDirection())) {
                    System.out.println("A vaporator has been built");
                    radio.shareBuilding(5);
                    vaporatorCount += 1;
                }
            }
        }

        // If we can't refine we than try to Mine.
        // Check again all direction and try to mine
        MapLocation soupLoc = null;
        for (Direction dir : directions) {
            if (tryMine(dir)) {
                System.out.println("I mined soup! " + rc.getSoupCarrying());
                soupLoc = rc.getLocation().add(dir);
            }
        }
        // Only wanna add one soup location so we don't overwhelm the blockchain
        if (soupLoc != null && !soupMap.contains(soupLoc)) {
            radio.shareLocation(soupLoc, 1);
            soupMap.add(soupLoc);
        }

        // With max soup limit, go to nearest refinery
        // If no refineries, go refine at HQ
        // Under max soup limit, go to nearest soup location
        // If no known soup locations, move randomly
        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            System.out.println("I'm full of soup.");
            if (refineryMap.size() > 0) {
                System.out.println("Going to the nearest refinery");
                MapLocation nearest = findNearest(refineryMap);
                goToLocation(nearest);
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

    /**
     * Removes depleted soup locations from this miner's soupMap.
     *
     * @throws GameActionException
     */
    void updateSoupMap() throws GameActionException {
        int numSoupLocations = soupMap.size();
        for (int i = 0; i < numSoupLocations; i++) {
            MapLocation soup = soupMap.get(i);
            if (rc.canSenseLocation(soup) && rc.senseSoup(soup) == 0) {
                soupMap.remove(i);
            }
        }
    }
}
