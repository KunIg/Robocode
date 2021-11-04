package IK;

import robocode.AdvancedRobot;
import robocode.Bullet;
import robocode.Condition;

public class BulletTracker extends Condition {

    private long                expectedTimeOfImpact;
    private String              targetName;
    private Bullet              bullet;
    private AdvancedRobot       myRobot;
    private int                 aimMethod;
    private boolean             hitTarget;


    public BulletTracker(
            AdvancedRobot ar,
            Bullet b,
            String targetName,
            long timeToImpact,
            int aimMethod) {

        if (b != null) {

            this.myRobot                = ar;
            this.bullet                 = b;
            this.targetName             = targetName;
            this.expectedTimeOfImpact   = ar.getTime() + timeToImpact;
            this.aimMethod              = aimMethod;
            this.hitTarget              = false;
            myRobot.addCustomEvent(this);
        }

    }


    public long     getExpectedTimeOfImpact() { return expectedTimeOfImpact; }
    public String   getTargetName()           { return targetName; }
    public Bullet   getBullet()               { return bullet; }
    public int      getAimMethod()            { return aimMethod; }
    public boolean  hitTarget()               { return hitTarget; }


    public boolean test() {

        if (targetName.equals(bullet.getVictim())) {
            hitTarget = true;
            myRobot.removeCustomEvent(this);
            return true;
        }

        if (bullet.getVictim() != null) {
            myRobot.removeCustomEvent(this);
        }

        if (expectedTimeOfImpact <= myRobot.getTime()) {
            hitTarget = false;
            myRobot.removeCustomEvent(this);
            return true;
        }

        return false;
    }
}