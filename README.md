# SparkMaxTestImplementations
This repo represents various experiments I have been doing on programming with the [Spark MAX Motor Controllers](http://www.revrobotics.com/rev-11-2158/). Each folder underneath the root directory of the repo represents their own implementation, with their own unique codebases. If you want to see the actual meat and potatoes of the implementations, go down the src/main/java/ folder and find the Robot class in each implementation.

**Warning: This code hasn't been maintained for a while. I don't gurantee its compatatibility with the latest SDK.** I wrote this a few months after the Spark MAX was released to get an idea of how it would be to program with it. It has not and will not be updated to use the latest version of the Spark MAX SDK. If in doubt, defer to Rev Robotics'/WPI's documentation.


## Setup  ##

1. Run `git clone https://github.com/Uberlyuber/SparkMaxTestImplementations.git` inside the folder you want to download the code to
2. Navigate to the implementation you wish to use
3. Run `gradlew build` to build the robot code
4. While connected to your robot, run `gradlew deploy` to deploy it to your roboRIO.
5. Ensure the CAN Devices are set up in the same way as they are in the code
6. Enjoy :)

## Code Summaries ##

- CargoManipulator

This implementation is based off a manipulator our team used for the cargo pieces (orange kickballs) in the 2019 FRC game. The manipulator was on a wristed joint that used two motors to pivot and one motor to suck the ball in with wheels.

This implementation is important because it shows how to use the position-based closed-loop control mode of the Spark Max. Note that for it to work, a quadrature encoder would need to be plugged into the pivot two motor controller. In addition, the PID loop would likely need to be adjusted for the increased torque the NEOs would provide, compared to the 775pros we currently use.

- ArcadeDriveCAN

This implementation demonstrates a four-NEO drivetrain setup. It uses two SpeedControllerGroups - one for each side - that it uses as constructors for the DifferentialDrive, which we can then execute ArcadeDrive() on. A good note is that the aft motors will follow the speed of the front motors (ie, the left aft motor will try to match the speed of the left forward motor).
