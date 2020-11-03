package Team3player;
import battlecode.common.*;

import java.sql.Ref;

public strictfp class RobotPlayer {
    static RobotController rc;

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
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;

    /**
     * Add location of HQ when startinig the game.
     * This will be used by the Miner to go back to deposit soup.
     */
    static MapLocation HQlocation;
    static MapLocation Reflocation;
    static int minerCount = 0;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the misssing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());

                // Try to find the HQ at every turn
                findHQ();

                switch (rc.getType()) {
                    case HQ:
                        runHQ();
                        break;
                    case MINER:
                        runMiner();
                        break;
                    case REFINERY:
                        runRefinery();
                        break;
                    case VAPORATOR:
                        runVaporator();
                        break;
                    case DESIGN_SCHOOL:
                        runDesignSchool();
                        break;
                    case FULFILLMENT_CENTER:
                        runFulfillmentCenter();
                        break;
                    case LANDSCAPER:
                        runLandscaper();
                        break;
                    case DELIVERY_DRONE:
                        runDeliveryDrone();
                        break;
                    case NET_GUN:
                        runNetGun();
                        break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    // Try to find the HQ at every turn
    static void findHQ() throws GameActionException {
        if (HQlocation == null) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.HQ && robot.team == rc.getTeam())
                    HQlocation = robot.location;
            }
        }
        // TODO: Implement to find HQ even when robot are not close enough to find it.
        // Use blockchain to brodcast location
    }

    //Build Miners for 250 to begin, after that prioritize refineries unless there are fewer than 5 miners
    static void runHQ() throws GameActionException {
        if ((Reflocation != null && rc.getTeamSoup() > 400) || minerCount <= 5) {
            tryBuild(RobotType.MINER, randomDirection());
            ++minerCount;
        } else if (turnCount < 150) {
            tryBuild(RobotType.MINER, randomDirection());
            ++minerCount;
        }
    }

    /******************* MINER STRATEGY *********************
     * 1 - Build refinery if possible
     * 2 - Look around and try to refinery
     * 3 - If we can't refine we try to mine
     * 4 - When full of soup we go to a refinery
     * 5 - Try to build a design_school
     *******************************************************/

    static void runMiner() throws GameActionException {

        // tryBlockchain();
        ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();
        ArrayList<MapLocation> refLocations = new ArrayList<MapLocation>();

        //Builds refinery and creates location pointer to it
        if(turnCount > 375) {
            if (tryBuild(RobotType.REFINERY, randomDirection()))
                System.out.println("A refinery was built!");
        }

        if (Reflocation == null) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam())
                    Reflocation = robot.location;
            }
        }

        // Loop in all directions and try to refine in that direction
        for (Direction dir : directions) {
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        }

        // If we can't refine we than try to Mine.
        // Check again all direction and try to mine
        for (Direction dir : directions) {
            if (tryMine(dir))
                System.out.println("I mined soup! " + rc.getSoupCarrying());

            // With max soup limit and no refineries return to the HQ otherwise move randomly
            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && Reflocation == null) {
                System.out.println("Time to go back to HQ");
                Direction toHQ = rc.getLocation().directionTo(HQlocation);
                tryMove(toHQ);
            }
            //Return to a refinery to refine when full of soup
            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && Reflocation != null) {
                System.out.println("Time to go refine");
                if (Reflocation != null) {
                    Direction toRef = rc.getLocation().directionTo(Reflocation);
                    tryMove(toRef);
                }

            } else {
                System.out.println("Keep moving around to get Soup: " + rc.getSoupCarrying());
                tryMove(randomDirection());
            }
            if (turnCount > 275) {
                if (!checkNearby(RobotType.DESIGN_SCHOOL)) {
                    if (tryBuild(RobotType.DESIGN_SCHOOL, randomDirection()))
                        System.out.println("A design school was built!");
                }
            }
        }

        // Try to move after cheking to do stuff since it is less important.
        // Moving brings cooldown to 2. This will stop the miner from doinig other things.
        // tryMove(randomDirection()); With this line it will try to move.
        if (tryMove(randomDirection()))
            System.out.println("I moved!");
    }

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }

    static void runVaporator() throws GameActionException {

    }

    static void runDesignSchool() throws GameActionException {
        for (Direction dir : directions)
            if (tryBuild(RobotType.LANDSCAPER, dir))
                System.out.println("Create landscaper");
    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException {
        if (rc.getDirtCarrying() == 0) {
            tryToDig();
        }
        if (HQlocation != null) {
            MapLocation bestPlaceToBuildWall = null;
            int lowestElevation = 9999999;
            // Loop over tiles around HQ and try to add that direction to get all 8 tiles around HQ
            for (Direction dir : directions) {
                MapLocation tileToCheck = HQlocation.add(dir);
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

    static void runDeliveryDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */

    // Check any nearby robots or buildings
    static boolean checkNearby(RobotType target) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo r : robots) {
            if (r.getType() == target) {
                return true;
            }
        }
        return false;
    }

    static boolean tryToDig() throws GameActionException {
        Direction dir = randomDirection();
        if (rc.canDigDirt(dir)) {
            rc.digDirt(dir);
            return true;
        }
        return false;
    }

    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir  The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
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
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }


    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }

    public void shareLocation(MapLocation loc, int resource) throws GameActionException {
        int[] message = new int[7];
        message[0] = 3333;
        message[1] = resource;
        message[2] = loc.x; // x coord of resource
        message[3] = loc.y; // y coord of resource
        if (rc.canSubmitTransaction(message, 3)) {
            rc.submitTransaction(message, 3);
            System.out.println("Found new soup!" + loc);
        }
    }

    public void updateSoupLocations(ArrayList<MapLocation> soupLocations) throws GameActionException {
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] mess = tx.getMessage();
            if(mess[0] == 3333 && mess[1] == 0){
                // TODO: don't add duplicate locations
                soupLocations.add(new MapLocation(mess[2], mess[3]));
            }
        }
    }
}
