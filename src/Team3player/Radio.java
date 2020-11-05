package Team3player;
import battlecode.common.*;
import java.util.ArrayList;

public class Radio {

    RobotController rc;

    // unique signet to discern our messages from others
    static final int signet = 392781;

    public Radio(RobotController r) {
        rc = r;
    }

    static final String[] resourceType = {
            "HQ",
            "soup",
            "refinery",
            "design school",
            "fulfillment center"
    };

    /**
     * Submits transaction to blockchain that shares location of resource.
     *
     * @param loc x, y coords of resource
     * @param resource number correlates to resourceType
     * @throws GameActionException
     */
    public void shareLocation(MapLocation loc, int resource) throws GameActionException {
        int[] transmission = new int[7];
        transmission[0] = signet;
        transmission[1] = resource;
        transmission[2] = loc.x;
        transmission[3] = loc.y;
        if (rc.canSubmitTransaction(transmission, 3)) {
            rc.submitTransaction(transmission, 3);
            System.out.println("Shared " + resourceType[resource] + " location at: " + loc);
        }
    }

    /**
     * Submits transaction to blockchain that shares that a building has been created.
     *
     * @param resource number correlates to resourceType
     * @throws GameActionException
     */
    public void shareBuilding(int resource) throws GameActionException {
        int[] transmission = new int[7];
        transmission[0] = signet;
        transmission[1] = resource;
        if (rc.canSubmitTransaction(transmission, 8)) {
            rc.submitTransaction(transmission, 8);
            System.out.println("Shared " + resourceType[resource] + " creation.");
        }
    }

    /**
     * Updates miner's map with locations of resource
     *
     * @param map miner's map of resource
     * @param resource number correlates to resourceType
     * @throws GameActionException
     */
    public void updateMap(ArrayList<MapLocation> map, int resource) throws GameActionException {
        Transaction[] retVal = rc.getBlock(rc.getRoundNum() - 1);
        if (retVal != null) {
            for (Transaction tx : retVal) {
                int[] transmission = tx.getMessage();
                if (transmission[0] == signet && transmission[1] == resource) {
                    MapLocation resourceLoc = new MapLocation(transmission[2], transmission[3]);
                    // Don't add duplicates
                    if (!map.contains(resourceLoc)) {
                        System.out.println("Added new " + resourceType[resource] + " location to nav");
                        map.add(resourceLoc);
                    }
                }
            }
        }
    }

    /**
     * Updates miner's count of resource
     * i.e Number of design schools
     *
     * @param resource number correlates to resourceType
     * @throws GameActionException
     */
    public int updateBuildingCount(int resource) throws GameActionException {
        int count = 0;
        Transaction[] retVal = rc.getBlock(rc.getRoundNum() - 1);
        if (retVal != null) {
            for (Transaction tx : retVal) {
                int[] transmission = tx.getMessage();
                if (transmission[0] == signet && transmission[1] == resource) {
                    count += 1;
                }
            }
        }
        return count;
    }
}
