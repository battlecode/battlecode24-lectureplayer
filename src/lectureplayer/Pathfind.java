package lectureplayer;

import java.util.HashSet;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.HashSet;

public class Pathfind {

    private static Direction dir;

    private static MapLocation prevDest = null;
    private static HashSet<MapLocation> line = null;
    private static int obstacleStartDist = 0;

    public static void moveTowards(RobotController rc, MapLocation loc, boolean fill) throws GameActionException {
        
        // move forward if possible, if not, try to go right or left but still towards target
        Direction dir = rc.getLocation().directionTo(loc);

        if(fill & rc.canFill(rc.getLocation().add(dir))) rc.fill(rc.getLocation().add(dir));

        if(rc.canMove(dir)) rc.move(dir);
        else if(rc.canMove(dir.rotateLeft())) rc.move(dir.rotateLeft());
        else if(rc.canMove(dir.rotateRight())) rc.move(dir.rotateRight());
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

    private static int bugState = 0; // 0 head to target, 1 circle obstacle
    private static MapLocation closestObstacle = null;
    private static int closestObstacleDist = 10000;
    private static Direction bugDir = null;

    public static void resetBug(){
        bugState = 0; // 0 head to target, 1 circle obstacle
        closestObstacle = null;
        closestObstacleDist = 10000;
        bugDir = null;
    }

    public static void bugNavOne(RobotController rc, MapLocation destination) throws GameActionException{
        if(bugState == 0) {
            bugDir = rc.getLocation().directionTo(destination);
            if(rc.canMove(bugDir)){
                rc.move(bugDir);
            } else {
                bugState = 1;
                closestObstacle = null;
                closestObstacleDist = 10000;
            }
        } else {
            if(rc.getLocation().equals(closestObstacle)){
                bugState = 0;
            }

            if(rc.getLocation().distanceSquaredTo(destination) < closestObstacleDist){
                closestObstacleDist = rc.getLocation().distanceSquaredTo(destination);
                closestObstacle = rc.getLocation();
            }

            for(int i = 0; i < 9; i++){
                if(rc.canMove(bugDir)){
                    rc.move(bugDir);
                    bugDir = bugDir.rotateRight();
                    bugDir = bugDir.rotateRight();
                    break;
                } else {
                    bugDir = bugDir.rotateLeft();
                }
            }
        }
    }

    public static void bugNav2(RobotController rc, MapLocation destination) throws GameActionException{
        
        if(!destination.equals(prevDest)) {
            prevDest = destination;
            line = createLine(rc.getLocation(), destination);
        }

        for(MapLocation loc : line) {
            rc.setIndicatorDot(loc, 255, 0, 0);
        }

        if(bugState == 0) {
            bugDir = rc.getLocation().directionTo(destination);
            if(rc.canMove(bugDir)){
                rc.move(bugDir);
            } else {
                bugState = 1;
                obstacleStartDist = rc.getLocation().distanceSquaredTo(destination);
                bugDir = rc.getLocation().directionTo(destination);
            }
        } else {
            if(line.contains(rc.getLocation()) && rc.getLocation().distanceSquaredTo(destination) < obstacleStartDist) {
                bugState = 0;
            }

            for(int i = 0; i < 9; i++){
                if(rc.canMove(bugDir)){
                    rc.move(bugDir);
                    bugDir = bugDir.rotateRight();
                    bugDir = bugDir.rotateRight();
                    break;
                } else {
                    bugDir = bugDir.rotateLeft();
                }
            }
        }
    }

    public static void bugNavZero(RobotController rc, MapLocation destination) throws GameActionException{
        Direction bugDir = rc.getLocation().directionTo(destination);

        if(rc.canMove(bugDir)){
            rc.move(bugDir);
        } else {
            for(int i = 0; i < 8; i++){
                if(rc.canMove(bugDir)){
                    rc.move(bugDir);
                    break;
                } else {
                    bugDir = bugDir.rotateLeft();
                }
            }
        }
    }

    private static HashSet<MapLocation> createLine(MapLocation a, MapLocation b) {
        HashSet<MapLocation> locs = new HashSet<>();
        int x = a.x, y = a.y;
        int dx = b.x - a.x;
        int dy = b.y - a.y;
        int sx = (int) Math.signum(dx);
        int sy = (int) Math.signum(dy);
        dx = Math.abs(dx);
        dy = Math.abs(dy);
        int d = Math.max(dx,dy);
        int r = d/2;
        if (dx > dy) {
            for (int i = 0; i < d; i++) {
                locs.add(new MapLocation(x, y));
                x += sx;
                r += dy;
                if (r >= dx) {
                    locs.add(new MapLocation(x, y));
                    y += sy;
                    r -= dx;
                }
            }
        }
        else {
            for (int i = 0; i < d; i++) {
                locs.add(new MapLocation(x, y));
                y += sy;
                r += dx;
                if (r >= dy) {
                    locs.add(new MapLocation(x, y));
                    x += sx;
                    r -= dy;
                }
            }
        }
        locs.add(new MapLocation(x, y));
        return locs;
    }
}
