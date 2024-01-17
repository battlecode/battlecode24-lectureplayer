package lectureplayer;

import battlecode.common.*;

public class Communication {

    static final int BEGINNING_ENEMY_IDX = 6;
    // same as vision radius for convenience
    static final int CLUSTER_RADIUS = 20;

    public static void updateEnemyInfo(RobotController rc, MapLocation loc, int numberEnemies) {
        // check existing clusters to see if in range of any

        // update last cluster or create new one if there are none

    }

    public static void updateFlagInfo(RobotController rc, MapLocation loc, boolean carried, int flagID) {

    }

    public static MapLocation readLocation(RobotController rc, int idx) {
        return new MapLocation(0, 0);
    }

    public static int readEnemySize(RobotController rc, int idx) {
        return 0;
    }

    public static boolean checkIfCarried(RobotController rc, int idx) throws GameActionException {
        return (rc.readSharedArray(idx) & 0b10) == 1;
    }

    public static boolean checkIfUpdated(RobotController rc, int idx) throws GameActionException {
        return rc.readSharedArray(idx) % 2 == 1;
    }

    public static int locationToNum(RobotController rc, MapLocation loc) {
        return loc.x + rc.getMapWidth() * loc.y + 1;

    }

    public static MapLocation numToLocation(RobotController rc, int num) {
        return new MapLocation((num - 1) % rc.getMapWidth(), (num - 1) / rc.getMapWidth());

    }
}
