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
            "refinery"
    };

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

    public void updateMap(ArrayList<MapLocation> map, int resource) throws GameActionException {
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] transmission = tx.getMessage();
            if(transmission[0] == signet && transmission[1] == resource){
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
