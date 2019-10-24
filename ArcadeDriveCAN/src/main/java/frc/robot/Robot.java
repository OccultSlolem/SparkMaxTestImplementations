package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * This is a demo implementation of a two-NEO drivetrain set up with
 * a single NEO for the left side and a single NEO for the right. It is
 * set up for use with single-joystick driving.
 * <br>
 * This class will automatically output diagnostic statistics to Shuffleboard
 * as well as print it to the console for the robot logs.
 *
 * @see CANSparkMax
 */
public class Robot extends TimedRobot {

    //Define CAN Object variables
    private CANSparkMax m_left;
    private CANSparkMax m_right;
    private DifferentialDrive m_drive;

    //Define Joysticks
    private Joystick m_joy;

    //Define variables for printing statistics to DS
    private StringBuilder _sb = new StringBuilder();
    private int looperCounter = 0;

    /**
     * Initiate motor controllers on robot startup
     */
    @Override
    public void robotInit() {
        //Instantiate a StringBuilder to throw initial printouts to
        StringBuilder _initSb = new StringBuilder();

        //Instantiate CANSparkMaxes
        m_left = new CANSparkMax(0, CANSparkMaxLowLevel.MotorType.kBrushless);
        m_right = new CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless);

        //Reset Spark Maxes to factory default
        m_left.restoreFactoryDefaults();
        m_right.restoreFactoryDefaults();

        //Instantiate DifferentialDrive
        m_drive = new DifferentialDrive(m_left,m_right);

        //Instantiate Joystick
        m_joy = new Joystick(0);

        //Append date of last revision to StringBuilder
        File file = new File(Robot.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        _initSb.append("ROBOT.JAVA LAST REVISED: ").append(sdf.format(file.lastModified()));

        //Append firmware versions and serial numbers to StringBuilder
        _initSb.append("\n-----\nLEFT DRIVETRAIN FIRM: ").append(m_left.getFirmwareString());
        _initSb.append("\nRIGHT DRIVETRAIN FIRM: ").append(m_right.getFirmwareString());
        _initSb.append("\n-----\nLEFT DRIVETRAIN SERIAL: ").append(Arrays.toString(m_left.getSerialNumber()));
        _initSb.append("\nRIGHT DRIVETRAIN SERIAL: ").append(Arrays.toString(m_right.getSerialNumber())).append("\n-----");

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
        SmartDashboard.putNumber("Left Drivetrain Percent Output", m_left.get());
        SmartDashboard.putNumber("Right Drivetrain Percent Output", m_right.get());

        //Arcade drive the robot
        m_drive.arcadeDrive(-m_joy.getY(),m_joy.getX());

        //Print diagnostic statistics every 10 iterations
        looperCounter++;
        if (looperCounter >= 10) { printStats(); looperCounter = 0; }
    }

    private void printStats() {
        _sb.append("**********");
        _sb.append("\ntimestamp ").append(Timer.getFPGATimestamp());
        _sb.append("\tleft ").append(m_left.get());
        _sb.append("\tright ").append(m_right.get());
        _sb.append("\n**********");

        //Print StringBuilder
        System.out.println(_sb.toString());

        //Clear StringBuilder for next use
        _sb.setLength(0);
    }
}
