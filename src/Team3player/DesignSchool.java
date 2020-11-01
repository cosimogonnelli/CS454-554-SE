package Team3player;

import battlecode.common.*;

public class DesignSchool extends Robot {
    int LsCount = 0;
    public DesignSchool(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        if(LsCount < 2) {
            for (Direction dir : directions)
                if (tryBuild(RobotType.LANDSCAPER, dir))
                    System.out.println("Create landscaper");
        }
    }
}
