package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.concurrent.TimeUnit;

public class ImuSubClass {
    BNO055IMU imu;
    Orientation angles;

    double heading = 0;

    public void init(HardwareMap hardwareMap) {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public void loop() {
        angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        heading = AngleUnit.DEGREES.normalize(AngleUnit.DEGREES.fromUnit(angles.angleUnit,angles.firstAngle));
    }

    public double getHeading() {
        return heading;
    }

    void turn(double degrees, double speed, double accuracy, double settleTime, double pGain, HardwareDesignosaurs robot, ElapsedTime time, LinearOpMode opmode) {
        loop();
        double target = getHeading() + degrees;
        double lastBad = time.now(TimeUnit.MILLISECONDS);
        double lastTime = time.now(TimeUnit.MILLISECONDS);
        while (opmode.opModeIsActive()) {
            double currTime = time.now(TimeUnit.MILLISECONDS);
            double deltTime = (currTime - lastTime) / 1000;
            lastTime = currTime;

            loop();
            double error = target - getHeading();
            //double output = robot.limit(error/pGain,speed,-speed);
            double output = error/pGain;
            robot.moveDirection(0,0,output);

            System.out.println("asdfghjkl target: "+target);
            System.out.println("asdfghjkl current: "+getHeading());
            System.out.println("asdfghjkl error: "+error);
            System.out.println("asdfghjkl time good"+(currTime - lastBad));
            System.out.println("asdfghjkl output"+(output));


            opmode.telemetry.addData("target",target);
            opmode.telemetry.addData("current",getHeading());
            opmode.telemetry.addData("error",error);
            opmode.telemetry.addData("time good",currTime - lastBad);
            opmode.telemetry.update();
            if (error < .2) {
                if (currTime - lastBad > settleTime) {
                    robot.setPowers(0);
                    break;
                }
            } else {
                lastBad = currTime;
            }
        }

    }

    void turn(double degrees, double speed, HardwareDesignosaurs robot, ElapsedTime time, LinearOpMode opMode) {
        turn(degrees, speed, .2, 250, 1/40, robot, time, opMode);
    }
    void turn(double degrees, HardwareDesignosaurs robot, ElapsedTime time, LinearOpMode opMode) {
        turn(degrees, .2, robot, time, opMode);
    }

    void turnSimp(double degrees, double speed, HardwareDesignosaurs robot, LinearOpMode opMode) {
        loop();
        double startHeading = getHeading();
        if (startHeading > degrees) {
            robot.moveDirection(0,0,speed);
        } else {
            robot.moveDirection(0,0,-speed);
        }
        while (opMode.opModeIsActive()) {

            System.out.println("asdfghjkl target: "+degrees);
            System.out.println("asdfghjkl current: "+getHeading());

            opMode.telemetry.addData("target",degrees);
            opMode.telemetry.addData("current",getHeading());
            opMode.telemetry.update();

            loop();
            if (startHeading > degrees) {
                if (getHeading() < degrees) {
                    break;
                }
            } else {
                if (getHeading() > degrees) {
                    break;
                }
            }
        }
    }

    void turnSimp(double degrees, HardwareDesignosaurs robot, LinearOpMode opMode) {
        turnSimp(degrees,.6, robot, opMode);
        turnSimp(degrees,.3, robot, opMode);
        turnSimp(degrees,.15, robot, opMode);
    }

}
