package Team3player;
import battlecode.common.*;

import java.util.ArrayList;

public class HQ extends Robot {

    int minerCount = 0;
    MapLocation HQLoc = rc.getLocation();

    public HQ(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (HQLocation.size() == 0) {
            HQLocation.add(HQLoc);
            //Share HQ location on blockchain
            radio.shareLocation(HQLoc, 0);
        }

        //Build Miners for 250 to begin, after that prioritize refineries unless there are fewer than 5 miners
        if(minerCount < 6) {
            for (Direction dir : directions)
                if (tryBuild(RobotType.MINER, randomDirection()))
                    ++minerCount;
        }

        //Compute and check possible locations of enemy HQ and share to blockchain
//        if (HQLocation.size() == 1) {
//            int x = HQLoc.x;
//            int y = HQLoc.y;
//            ArrayList<MapLocation> possibleEnemyHQLocations = new ArrayList<>();
//            possibleEnemyHQLocations.add(new MapLocation(-x,y));
//            possibleEnemyHQLocations.add(new MapLocation(x,-y));
//            possibleEnemyHQLocations.add(new MapLocation(-x,-y));
//            for (MapLocation loc : possibleEnemyHQLocations) {
//                if (rc.senseRobotAtLocation(loc).type == RobotType.HQ) {
//                    HQLocation.add(loc);
//                    radio.shareLocation(loc, 7);
//                    break;
//                }
//            }
//        }
    }
}
