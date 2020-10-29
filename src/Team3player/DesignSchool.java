package Team3player;
import battlecode.common.*;

public class DesignSchool extends Robot {

    public DesignSchool(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
            for (Direction dir : directions)
            if (tryBuild(RobotType.LANDSCAPER, dir))
            System.out.println("Create landscaper");
    }
}
