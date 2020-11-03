package Team3player;
import battlecode.common.*;
import java.util.ArrayList;

public class Radio {

    RobotController rc;

    // unique signet to discern our messages from others
    static final int signet = 333333333;

    public Radio(RobotController r) {
        rc = r;
    }

    public void shareSoupLocation(MapLocation loc ) throws GameActionException {
        int[] transmission = new int[7];
        transmission[0] = signet;
        transmission[1] = 0;
        transmission[2] = loc.x;
        transmission[3] = loc.y;
        if (rc.canSubmitTransaction(transmission, 3)) {
            rc.submitTransaction(transmission, 3);
            System.out.println("Shared soup location at: " + loc);
        }
    }

    public void updateSoupMap(ArrayList<MapLocation> soupMap) throws GameActionException {
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] transmission = tx.getMessage();
            if(transmission[0] == signet && transmission[1] == 0){
                System.out.println("Added new soup location to nav");
                soupMap.add(new MapLocation(transmission[2], transmission[3]));
            }
        }
    }
}
