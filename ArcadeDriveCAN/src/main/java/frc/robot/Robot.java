package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;

/**
 * This is a demo implementation of a four-NEO drivetrain set up with
 * a single NEO for the left side and a single NEO for the right. It is
 * set up for use with single-joystick driving.
 * <br>
 * This class will automatically output diagnostic statistics to Shuffleboard
 * as well as print it to the console for the robot logs.
 *
 * @see CANSparkMax
 */
@SuppressWarnings("FieldCanBeLocal")
public class Robot extends TimedRobot {

    //Define CAN Object variables
    private CANSparkMax m_leftFront;
    private CANSparkMax m_leftAft;
    private CANSparkMax m_rightFront;
    private CANSparkMax m_rightAft;

    //Define SpeedControllerGroups and DifferentialDrives
    private SpeedControllerGroup m_left;
    private SpeedControllerGroup m_right;
    private DifferentialDrive m_drive;

    //Define Joysticks
    private Joystick m_joy;

    //Define variables for printing statistics to DS
    private StringBuilder _sb = new StringBuilder();
    private int looperCounter = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

    /**
     * Initiate motor controllers on robot startup
     */
    @Override
    public void robotInit() {
        //Instantiate a StringBuilder to throw initial printouts to
        StringBuilder _initSb = new StringBuilder();

        //Instantiate CANSparkMaxes
        m_leftFront = new CANSparkMax(0, MotorType.kBrushless);
        m_rightFront = new CANSparkMax(1, MotorType.kBrushless);
        m_leftAft = new CANSparkMax(2, MotorType.kBrushless);
        m_rightAft = new CANSparkMax(3, MotorType.kBrushless);

        //Reset Spark Maxes to factory default
        m_leftFront.restoreFactoryDefaults();
        m_rightFront.restoreFactoryDefaults();
        m_leftAft.restoreFactoryDefaults();
        m_rightAft.restoreFactoryDefaults();

        //Set aft motors to follow forward motors
        m_leftAft.follow(m_leftFront,false);
        m_rightAft.follow(m_rightFront,false);

        //Instantiate SpeedControllerGroups and DifferentialDrives
        m_left = new SpeedControllerGroup(m_leftAft,m_leftFront);
        m_right = new SpeedControllerGroup(m_rightAft,m_rightFront);
        m_drive = new DifferentialDrive(m_left,m_right);

        //Instantiate Joystick
        m_joy = new Joystick(0);

        //Append date of last revision to StringBuilder
        File file = new File(Robot.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        _initSb.append("ROBOT.JAVA LAST REVISED: ").append(sdf.format(file.lastModified()));

        //Append firmware versions and serial numbers to StringBuilder
        _initSb.append("\n-----\nLEFT FRONT DRIVETRAIN FIRM: ").append(m_leftFront.getFirmwareString());
        _initSb.append("\nLEFT AFT DRIVETRAIN FIRM: ").append(m_leftAft.getFirmwareString());
        _initSb.append("\nRIGHT FRONT DRIVETRAIN FIRM: ").append(m_rightFront.getFirmwareString());
        _initSb.append("\nRIGHT AFT DRIVETRAIN FIRM: ").append(m_rightAft.getFirmwareString());
        _initSb.append("\n-----\nLEFT FRONT DRIVETRAIN SERIAL: ").append(Arrays.toString(m_leftFront.getSerialNumber()));
        _initSb.append("\nLEFT AFT DRIVETRAIN SERIAL: ").append(Arrays.toString(m_leftAft.getSerialNumber()));
        _initSb.append("\nRIGHT FRONT DRIVETRAIN SERIAL: ").append(Arrays.toString(m_rightFront.getSerialNumber()));
        _initSb.append("\nRIGHT AFT DRIVETRAIN SERIAL: ").append(Arrays.toString(m_rightAft.getSerialNumber())).append("\n-----");


        //Print out initial diagnostic info from StringBuilder
        System.out.println(_initSb);

    }

    /**
     * Just runs teleopPeriodic(). Provides normal robot functions
     * during sandstorm.
     */
    @Override
    public void autonomousPeriodic() {
        teleopPeriodic();
    }

    /**
     * Drives the robot using DifferentialDrive's arcadeDrive() method
     *
     * @see DifferentialDrive
     */
    @Override
    public void teleopPeriodic() {
        //Put percent output to SmartDashboard
        Shuffleboard.getTab("DRIVETRAIN").add(m_drive);

        //Arcade drive the robot
        m_drive.arcadeDrive(-m_joy.getY(),m_joy.getX());

        //Print diagnostic statistics every 10 iterations
        looperCounter++;
        if (looperCounter >= 10) { printStats(); looperCounter = 0; }
    }

    private void printStats() {
        _sb.append("**********");
        _sb.append("\ntimestamp ").append(sdf.format(Instant.now().getEpochSecond())); //Gets time from Unix epoch
        _sb.append("\tleft-front ").append(m_leftFront.get());
        _sb.append("\tright-front ").append(m_rightFront.get());
        _sb.append("\nleft-aft ").append(m_leftAft.get());
        _sb.append("\tright-aft ").append(m_rightAft.get());
        _sb.append("\n**********");

        //Print StringBuilder
        System.out.println(_sb.toString());

        //Clear StringBuilder for next use
        _sb.setLength(0);
    }
}
