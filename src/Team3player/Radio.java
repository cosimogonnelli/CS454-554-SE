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
            "HQ (0)",
            "soup (1)",
            "refinery (2)",
            "design school (3)",
            "fulfillment center (4)",
            "net gun (5)",
            "vaporator (6)",
            "enemyHQ (7)",
            "drone (8)",
            "landscaper (9)",
            "water (10)"
    };

    /**
     * Submits transaction to blockchain that shares location of resource.
     *
     * @param loc x, y coords of resource
     * @param resource number correlates to resourceType
     * @throws GameActionException
     */
    public void shareLocation(MapLocation loc, int resource) throws GameActionException {
        int fee = 3;
        if (resource == 0 || resource == 6){
            fee = 5;
        }
        int[] transmission = new int[7];
        transmission[0] = signet;
        transmission[1] = resource;
        transmission[2] = loc.x;
        transmission[3] = loc.y;
        if (rc.canSubmitTransaction(transmission, fee)) {
            rc.submitTransaction(transmission, fee);
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
        if (rc.canSubmitTransaction(transmission, 4)) {
            rc.submitTransaction(transmission, 4);
            System.out.println("Shared " + resourceType[resource] + " creation.");
        }
    }

    /**
     * Submits transaction to blockchain that shares that a unit has been created.
     * Adds unit IDs to block chain
     *
     * @param unitType number correlates to resourceType (drone, landscaper)
     * @throws GameActionException
     */
    public void shareUnitCreation(ArrayList<Integer> unitIDs, int unitType) throws GameActionException {
        int[] transmission = new int[7];
        transmission[0] = signet;
        transmission[1] = unitType;
        for (int i = 0; i < unitIDs.size() && i+2 < 7; i++) {
            transmission[i+2] = unitIDs.get(i);
        }
        if (rc.canSubmitTransaction(transmission, 4)) {
            rc.submitTransaction(transmission, 4);
            System.out.println("Shared " + unitIDs.size() + " " + resourceType[unitType] + " creation.");
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
        for (int i = 1; i < rc.getRoundNum(); i++) {
            // If resource is soup, just check the most recent block
            // Else check all blocks for resource
            if (resource == 1 || resource == 2) {
                i = rc.getRoundNum() - 1;
            }
            Transaction[] retVal = rc.getBlock(i);
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

    /**
     * Updates unit ID list
     * i.e IDs of drones, landscapers
     *
     * @param unitType number correlates to resourceType
     * @throws GameActionException
     */
    public void updateUnitIDs(ArrayList<Integer> drones, int unitType) throws GameActionException{
        Transaction[] retVal = rc.getBlock(rc.getRoundNum() - 1);
        if (retVal != null) {
            for (Transaction tx : retVal) {
                int[] transmission = tx.getMessage();
                if (transmission[0] == signet && transmission[1] == unitType) {
                    for (int i = 2; i < 7; i++) {
                        if (transmission[i] != 0){
                            int unitID = transmission[i];
                            // Don't add duplicates
                            if (!drones.contains(unitID)) {
                                System.out.println("Added new " + resourceType[unitType] + " location to list");
                                drones.add(unitID);
                            }
                        }
                    }
                }
            }
        }
    }
}
