package lectureplayer;

import battlecode.common.*;

public class Communication {

    static final int LAST_FLAG_IDX = 5;
    static final int START_ENEMY_IDX = 6;
    static final int LAST_ENEMY_IDX = 61;

    static final int ENEMY_CLUSTER_RADIUS_SQ = 20;

    static final int FLAG_WARNING_IDX = 62;

    static int numClusters = 0;

    public static void updateEnemyInfo(RobotController rc, MapLocation loc, int numEnemies) throws GameActionException {
        for (int idx = START_ENEMY_IDX; idx < START_ENEMY_IDX + numClusters; idx++) {
            MapLocation enemyLoc = getLocation(rc, idx);

            if (enemyLoc.distanceSquaredTo(loc) <= ENEMY_CLUSTER_RADIUS_SQ) {
                int enemyGroupSize = numEnemies <= 1 ? numEnemies : numEnemies <= 4 ? 2 : 3;
                int value = locationToInt(rc, enemyLoc) * 16 + enemyGroupSize * 4 + 1;

                if (rc.canWriteSharedArray(idx, value) && idx <= LAST_ENEMY_IDX) {
                    rc.writeSharedArray(idx, value);
                }
                return;
            } 
        }

        int newIdx = START_ENEMY_IDX + numClusters;
        if (newIdx <= LAST_ENEMY_IDX) {
            numClusters++;
            int enemyGroupSize = numEnemies <= 1 ? numEnemies : numEnemies <= 4 ? 2 : 3;
            int value = locationToInt(rc, loc) * 16 + enemyGroupSize * 4 + 1;

            if (rc.canWriteSharedArray(newIdx, value)) {
                rc.writeSharedArray(newIdx, value);
            }
        }
    }

    // SHARED ARRAY: 64 indices
    // first 6 indices: flag infos
    // last index: used for giving robots a personal ID
    public static void updateFlagInfo(RobotController rc, MapLocation loc, boolean isCarried, Team team, int idx) throws GameActionException {
        int value = locationToInt(rc, loc) * 16 + teamToInt(team) * 4 + (isCarried ? 1 : 0) * 2 + 1;

        if (rc.canWriteSharedArray(idx, value)) {
            rc.writeSharedArray(idx, value);
        }
    }

    public static void updateWarningInfo(RobotController rc, MapLocation loc, int idx) throws GameActionException {
        int value = locationToInt(rc, loc) * 16 + 1;

        if (rc.canWriteSharedArray(idx, value)) {
            rc.writeSharedArray(idx, value);
        }
    }

    public static MapLocation getLocation(RobotController rc, int idx) throws GameActionException {
        //return new MapLocation(0, 0);

        int value = rc.readSharedArray(idx);
        int locNum = value >> 4;
        MapLocation loc = intToLocation(rc, locNum);
        return loc;
    }

    public static Team getTeam(RobotController rc, int idx) throws GameActionException {
        //return Team.NEUTRAL;

        int value = rc.readSharedArray(idx);
        int teamNum = (value >> 2) & 1;
        Team team = teamNum == 0 ? Team.A : Team.B;
        return team;
    }

    public static boolean getCarried(RobotController rc, int idx) throws GameActionException{
        int value = rc.readSharedArray(idx);
        int carriedNum = (value >> 1) & 1;
        boolean carried = carriedNum == 1;
        return carried;
    }

    public static int teamToInt(Team team) {
        return team == Team.A ? 0 : 1;
    }

    public static void setUnupdated(RobotController rc, int idx) throws GameActionException {
        int value = rc.readSharedArray(idx);
        int newValue = value & ~1;

        if (rc.canWriteSharedArray(idx, newValue)) {
            rc.writeSharedArray(idx, newValue);
        }
    }

    public static boolean getIfUpdated(RobotController rc, int idx) throws GameActionException {
        int value = rc.readSharedArray(idx);
        boolean updated = value % 2 == 1;
        return updated;
    }

    public static int locationToInt(RobotController rc, MapLocation loc) {
        if (loc == null)
            return 0;
        return 1 + loc.x + loc.y * rc.getMapWidth();
    }

    public static MapLocation intToLocation(RobotController rc, int m) {
        if (m == 0)
            return null;
        return new MapLocation((m - 1) % rc.getMapWidth(), (m - 1) / rc.getMapWidth());
    }
}
