package lectureplayer;

import battlecode.common.*;

public enum Role {
    ATTACKER,
    HEALER,
    BUILDER,
    GUARDIAN;

    public static Role getRobotRole(int id) {
        // Deterministically select role based on robot id

        Role role = Role.ATTACKER;
        if (id % 7 == 0) {
            role = Role.BUILDER;
        } else if (id % 3 == 0) {
            role = Role.HEALER;
        }

        return role;
    }
}