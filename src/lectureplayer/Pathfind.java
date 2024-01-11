package lectureplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Pathfind {

    private static Direction dir;

    public static void moveTowards(RobotController rc, MapLocation loc, boolean fill) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc);
        if(rc.canMove(dir)) rc.move(dir);
        else if(fill & rc.canFill(rc.getLocation().add(dir))) rc.fill(rc.getLocation().add(dir));
        else {
            Direction randDir = RobotPlayer.directions[RobotPlayer.random.nextInt(8)];
            if(rc.canMove(randDir)) rc.move(randDir);
        }
    }

    public static void explore(RobotController rc) throws GameActionException {
        if(rc.isMovementReady()) {
            MapLocation[] crumbLocs = rc.senseNearbyCrumbs(-1);
            if(crumbLocs.length > 0) {
                moveTowards(rc, crumbLocs[0], false);
            }

            if(dir == null || !rc.canMove(dir)) {
                dir = RobotPlayer.directions[RobotPlayer.random.nextInt(8)];
            }
            if(rc.canMove(dir)) rc.move(dir);
        }
    }
}
