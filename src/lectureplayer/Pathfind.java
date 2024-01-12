package lectureplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Pathfind {

    private static Direction dir;

    public static void moveTowards(RobotController rc, MapLocation loc, boolean fill) throws GameActionException {
        
        // move forward if possible, if not, try to go right or left but still towards target
        Direction dir = rc.getLocation().directionTo(loc);
        if(rc.canMove(dir)) rc.move(dir);
        else if(rc.canMove(dir.rotateLeft())) rc.move(dir.rotateLeft());
        else if(rc.canMove(dir.rotateRight())) rc.move(dir.rotateRight());
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
