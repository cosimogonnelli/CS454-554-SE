package Team3player;
import battlecode.common.*;

public class Miner extends Unit {

    public Miner(RobotController r) {
        super(r);
    }

    /******************* MINER STRATEGY *********************
     * 1 - Build refinery if possible
     * 2 - Look around and try to refinery
     * 3 - If we can't refine we try to mine
     * 4 - When full of soup we go to a refinery
     * 5 - Try to build a design_school
     *******************************************************/
    public void takeTurn() throws GameActionException {
        super.takeTurn();

        //Builds refinery and creates location pointer to it
        if(turn > 450) {
            if (tryBuild(RobotType.REFINERY, randomDirection()))
                System.out.println("A refinery was built!");
        }

        if (RefLocation == null) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.REFINERY && robot.team == rc.getTeam())
                    RefLocation = robot.location;
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
            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && RefLocation == null) {
                System.out.println("Time to go back to HQ");
                Direction toHQ = rc.getLocation().directionTo(HQLocation);
                goTo(toHQ);
            }
            //Return to a refinery to refine when full of soup
            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && RefLocation != null) {
                System.out.println("Time to go refine");
                if (RefLocation != null) {
                    Direction toRef = rc.getLocation().directionTo(RefLocation);
                    goTo(toRef);
                }

            } else {
                System.out.println("Keep moving around to get Soup: " + rc.getSoupCarrying());
                goTo(randomDirection());
            }
                if (!checkNearby(RobotType.DESIGN_SCHOOL) && turn > 50) {
                    if (tryBuild(RobotType.DESIGN_SCHOOL, randomDirection()))
                        System.out.println("A design school was built!");
                }
                if(!checkNearby(RobotType.FULFILLMENT_CENTER)){
                    if(tryBuild(RobotType.FULFILLMENT_CENTER, randomDirection()))
                    System.out.println("A fulfillment center has been built");
                }

        }

        // Try to move after checking to do stuff since it is less important.
        // Moving brings cooldown to 2. This will stop the miner from doing other things.
        // tryMove(randomDirection()); With this line it will try to move.
        if (goTo(randomDirection()))
            System.out.println("I moved!");
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
}
