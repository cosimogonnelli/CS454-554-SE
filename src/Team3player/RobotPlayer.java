package Team3player;
import battlecode.common.*;

public strictfp class RobotPlayer {

    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        Robot gundam = null;

        switch (rc.getType()) {
            case HQ:
                gundam = new HQ(rc);
                break;
            case MINER:
                gundam = new Miner(rc);
                break;
            case REFINERY:
                gundam = new Refinery(rc);
                break;
            case DESIGN_SCHOOL:
                gundam = new DesignSchool(rc);
                break;
            case FULFILLMENT_CENTER:
                gundam = new FulfillmentCenter(rc);
                break;
            case LANDSCAPER:
                gundam = new Landscaper(rc);
                break;
            case DELIVERY_DRONE:
                gundam = new Drone(rc);
                break;
            //case NET_GUN:
            // gundam = new Shooter(rc);
            // break;
            case VAPORATOR:
             gundam = new Vaporator(rc);
             break;
        }

        while (true) {
            try {
                gundam.takeTurn();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception"); // darn
                e.printStackTrace();
            }
        }
    }

    // Sprint 3
    static void runVaporator() throws GameActionException {

    }

    // Sprint 3
    static void runNetGun() throws GameActionException {

    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }
}
