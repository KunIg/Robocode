package IK;

import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class IK_Bot extends AdvancedRobot {
      final double quarterTurn = 90.0;
      final double halfTurn = 180.0;
      final double threeQuarterTurn = 270.0;
      final double fullTurn = 360.0;
	  double previousEnergy = 100;
	  int movementDirection = 1;
	  int gunDirection = 1;
	  double oldEnemyHeading;
	  double buffer;
	  String EnemyRobot;
	  public void run() {
	  double width = this.getBattleFieldWidth();
	  double height = this.getBattleFieldHeight();
	  buffer = 0.2 * Math.max(height, width);
	  setAdjustGunForRobotTurn(true);
	    do {
	    	if (getRadarTurnRemaining()== 0.0) {
	    		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	    		setTurnGunRightRadians(Double.POSITIVE_INFINITY);
	    	}
	    	/*
	    	double xPos = this.getX();
	    	double yPos = this.getY();
	    	if (yPos < buffer) { // bottom
	    		if (this.getHeading()<180) {
	    			this.setTurnLeft(90);
	    		}
	    		else {
	    			this.setTurnRight(90);
	    		}
	    	}
	    	else if (yPos> height-buffer) {
	    		if((this.getHeading()<90)&&(this.getHeading()>0)) {
	    			this.setTurnRight(90);
	    		}
	    		else if ((this.getHeading()<360)&&(this.getHeading()>270)) {
	    			this.setTurnLeft(90);
	    		}
	    	}
	    	if (xPos < buffer) { // left
	    		if (this.getHeading()<270) {
	    			this.setTurnLeft(90);
	    		}
	    		else {
	    			this.setTurnRight(90);
	    		}
	    	}
	    	else if (xPos> width-buffer) { // right
	    		if((this.getHeading()<90)&&(this.getHeading()>0)) {
	    			this.setTurnLeft(90);
	    		}
	    		else if ((this.getHeading()<180)&&(this.getHeading()>90)) {
	    			this.setTurnRight(90);
	    		}
	    	}
	    	else {
	    		this.setTurnLeft(0);
	    		this.setTurnRight(0);
	    	}
*/
	    	execute();
	    } while (true);
	  }
	  public void onScannedRobot(ScannedRobotEvent e) {
		  EnemyRobot = e.getName();
	      double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
		  double radarTurn = Utils.normalRelativeAngle(angleToEnemy - getRadarHeadingRadians());
		  //double gunTurn = Utils.normalRelativeAngle(angleToEnemy - getGunHeadingRadians());
		  double extraTurn = Math.min(Math.atan(36.0/e.getDistance()), Rules.RADAR_TURN_RATE_RADIANS);
		  radarTurn += (radarTurn < 0 ? -extraTurn : extraTurn);


		  setTurnRadarRightRadians(radarTurn);
		  //setTurnGunRightRadians(gunTurn);
	      // Stay perpendicular to opponent
		  setTurnRight(e.getBearing()+90-30*movementDirection);
	      
	     // If opponent has small energy drop, it most likely fired
	    double changeInEnergy = previousEnergy-e.getEnergy();
	    if (changeInEnergy>0 && changeInEnergy<=3) {
	         movementDirection =-movementDirection;
	         setAhead((e.getDistance()/4+25*2)*movementDirection);
	     }
	    

	    // Fire directly at target
	    firelineary (e,3);
	    //smartFire(e);
	    
	    // Track the energy level
	    previousEnergy = e.getEnergy();
	  }
/*
		public void onHitRobot(HitRobotEvent e) {
		    double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
			double gunTurn = Utils.normalRelativeAngle(angleToEnemy - getGunHeadingRadians());
			setTurnGunRightRadians(gunTurn);
			turnRight(e.getBearing());

			// Determine a shot that won't kill the robot...
			// We want to ram him instead for bonus points
			if (e.getEnergy() > 16) {
				fire(3);
			} else if (e.getEnergy() > 10) {
				fire(2);
			} else if (e.getEnergy() > 4) {
				fire(1);
			} else if (e.getEnergy() > 2) {
				fire(.5);
			} else if (e.getEnergy() > .4) {
				fire(.1);
			}
			if (e.getEnergy() <= .6)
			ahead(40); // Ram him again!
		}
*/
	  /*
	   public void onHitByBullet(HitByBulletEvent event) {
	       if (!event.getName().equals(EnemyRobot)) {
	           double tpRnd = Math.random() * 10;
	           int rndInt = (int) Math.ceil(tpRnd);
	           tpRnd = tpRnd % 3;
	           switch (rndInt) {
	             case 0:  back(100);
	                      break;
	             case 1:  back(10);
	                      turnRight(90);
	                      ahead(50);
	                      break;
	             case 2: back(10);
	                     turnLeft(90);
	                     ahead(50);
	           }
	       }
	   }
	   */
	   
		public void firelineary(ScannedRobotEvent e, double Power) {
			double bulletPower = Power;
			double myX = this.getX();
			double myY = this.getY();
			double absoluteBearing = e.getBearingRadians() + this.getHeadingRadians();
			double enemyX = myX + e.getDistance() * Math.sin(absoluteBearing);
			double enemyY = myY + e.getDistance() * Math.cos(absoluteBearing);
			double enemyHeading = e.getHeadingRadians();
			double enemyHeadingChange = enemyHeading - oldEnemyHeading;
			oldEnemyHeading = enemyHeading;
			double enemyVelocity = e.getVelocity();
			
			double deltaTime = 0;
			double predictedX = enemyX;
			double predictedY = enemyY;
			
			while (((++deltaTime)*(20-3.0*bulletPower))<Point2D.Double.distance(myX,myY,predictedX,predictedY)) {
				predictedX += Math.sin(enemyHeading)*enemyVelocity;
				predictedY += Math.cos(enemyHeading)*enemyVelocity;
				enemyHeading += enemyHeadingChange;
			}
			
			double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX-myX, predictedY-myY));
			this.setTurnGunRightRadians(Utils.normalRelativeAngle(theta-this.getGunHeadingRadians()));
			fire(bulletPower);
			
		}
		
        public void onHitWall(HitWallEvent ev) {
            centerRobot();

        }
        private void centerRobot() {
            double width, height, x, y;
            height= this.getBattleFieldHeight();
            width = this.getBattleFieldWidth();
            y = this.getY();
             x = this.getX();
             if (x > width/2)
                turnRight(threeQuarterTurn - getHeading());
             else
                 turnRight(quarterTurn - getHeading());
             ahead(getRandomNumber((int)Math.abs(width/2 - x)/2,(int)Math.abs(width/2 - x)));
         	   if (y < height/2)
                turnLeft(getHeading());
             else
                turnRight(halfTurn - getHeading());
         	  ahead(getRandomNumber((int)Math.abs(height/2 - y)/2,(int)Math.abs(height/2 - y)));
           	

          }
        
        public int getRandomNumber(int min, int max) {
            return (int) ((Math.random() * (max - min)) + min);
        }
}


