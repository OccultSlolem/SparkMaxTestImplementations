package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * <p>This is a demo of the Spark MAXes as they may be used for the Cargo
 * Manipulator. Note that for this to work <b>there must be a quadrature
 * encoder plugged into the pivot two Spark MAX.</b></p>
 *
 * <p>You can either use the tuning already in this class (which was
 * ripped from the original robot code) or you can tune it from
 * Shuffleboard for easy tuning. Change the setpoint by adjusting the
 * "Set Rotations" box.</p>
 *
 * <p>This class will automatically output PID stats to Shuffleboard as
 * well as printing it out for the robot logs.</p>
 *
 * @see CANSparkMax
 */
@SuppressWarnings("FieldCanBeLocal")
public class Robot extends TimedRobot {

    //Define CAN object variables
    private CANSparkMax m_cargoOne;
    private CANSparkMax m_cargoTwo;
    private CANPIDController m_pidController;
    private CANEncoder m_encoder;

    //Define PID tuning variables
    private double kP, kI, kD, kIz, kFF, kMaxOutput, kMinInput;

    //Define variables for printing statistics to DS
    private StringBuilder _sb = new StringBuilder();
    private int looperCounter = 0;

    /**
     * On robot power up, configure the Spark MAXes for the PID loop
     */
    @Override
    public void robotInit() {
        //Instantiate a StringBuilder to throw initial printouts to
        StringBuilder _initSb = new StringBuilder();

        //Instantiate CANSparkMaxes
        m_cargoOne = new CANSparkMax(0,MotorType.kBrushless);
        m_cargoTwo = new CANSparkMax(1,MotorType.kBrushless);

        //Append date of last revision to StringBuilder
        File file = new File(Robot.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        _initSb.append("ROBOT.JAVA LAST REVISED: ").append(sdf.format(file.lastModified()));

        //Append firmware version and serial numbers to StringBuilder
        _initSb.append("\nCARGO ONE FIRM: ").append(m_cargoOne.getFirmwareString());
        _initSb.append("\nCARGO TWO FIRM: ").append(m_cargoTwo.getFirmwareString());
        _initSb.append("\n-----\nCARGO ONE SERIAL: ").append(Arrays.toString(m_cargoOne.getSerialNumber()));
        _initSb.append("\nCARGO TWO SERIAL: ").append(Arrays.toString(m_cargoTwo.getSerialNumber()));

        //Print out initial diagnostic info from StringBuilder
        System.out.println(_initSb.toString());

        //Reset Spark MAXes to factory default
        m_cargoOne.restoreFactoryDefaults();
        m_cargoTwo.restoreFactoryDefaults();

        //Assign encoder to encoder plugged into Spark MAX
        m_encoder = m_cargoTwo.getEncoder();

        //Set cargoOne to follow cargoTwo
        m_cargoOne.follow(m_cargoTwo,false);

        //Get PID Controller from cargoTwo
        m_pidController = m_cargoTwo.getPIDController();

        //Assign PID coefficients
        kP = 0.22;
        kI = 0.114;
        kD = 0.7;
        kFF = 0;
        kIz = 1;
        kMinInput = -0.4;
        kMaxOutput = 0.2;

        //Assign PID constants to PID controller
        m_pidController.setP(kP);
        m_pidController.setI(kI);
        m_pidController.setD(kD);
        m_pidController.setFF(kFF);
        m_pidController.setIZone(kIz);
        m_pidController.setOutputRange(kMinInput, kMaxOutput);

        //Display PID coefficients on Shuffleboard
        SmartDashboard.putNumber("P Gain", kP);
        SmartDashboard.putNumber("I Gain", kI);
        SmartDashboard.putNumber("D Gain", kD);
        SmartDashboard.putNumber("Feed Forward", kFF);
        SmartDashboard.putNumber("Integrator Zone", kIz);
        SmartDashboard.putNumber("Min Output", kMinInput);
        SmartDashboard.putNumber("Max Output", kMaxOutput);
        SmartDashboard.putNumber("Set rotations", 0);
    }

    /**
     * On enable, print the state of the PID loop
     */
    @Override
    public void teleopInit() {
        _sb.append("\tENABLED");
        printPIDF();
    }

    /**
     * Prints the current state of the PID loop. Note that it puts it all in one
     * StringBuilder, and prints out the one string, as opposed to having to make
     * several calls to NetComm to print them all separately.
     */
    private void printPIDF() {
        _sb.append("**********");

        _sb.append("\ntimestamp ").append(Timer.getFPGATimestamp());

        _sb.append("\np ").append(kP);
        _sb.append("\ti ").append(kI);
        _sb.append("\td ").append(kD);

        _sb.append("\nff ").append(kFF);
        _sb.append("\tiz ").append(kIz);

        _sb.append("\nin ").append(m_encoder.getPosition());
        _sb.append("\tout ").append(m_cargoTwo.getAppliedOutput());

        _sb.append("\n**********");
        System.out.println(_sb.toString());
        _sb.setLength(0); //Clear the StringBuilder for the next usage
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
     * Collects tuning from Shuffleboard and adjusts the PID loop to match.
     * Sets a setpoint for the loop based off of the "Set rotations" number
     * from Shuffleboard.
     */
    @Override
    public void teleopPeriodic() {
        //Read PID coefficients from Shuffleboard
        double p = SmartDashboard.getNumber("P Gain", 0);
        double i = SmartDashboard.getNumber("I Gain", 0);
        double d = SmartDashboard.getNumber("D Gain", 0);
        double ff = SmartDashboard.getNumber("Feed Forward", 0);
        double iz = SmartDashboard.getNumber("Integrator Zone", 0);
        double max = SmartDashboard.getNumber("Max Output", 0);
        double min = SmartDashboard.getNumber("Min Output", 0);
        double rotations = SmartDashboard.getNumber("Set rotations",0);

        //If PID coefficients differ from current settings, assign them to the motor controller
        if(p != kP) { m_pidController.setP(p); kP = p; }
        if(i != kI) { m_pidController.setI(i); kI = i; }
        if(d != kD) { m_pidController.setD(d); kD = d; }
        if(ff != kFF) { m_pidController.setFF(ff); kFF = ff; }
        if(iz != kIz) { m_pidController.setIZone(iz); kIz = iz; }
        if(max != kMaxOutput || min != kMinInput) {
            m_pidController.setOutputRange(min,max);
            kMaxOutput = max; kMinInput = min;
        }

        /*
         * This next line does most of the heavy lifting of the PID loop
         * It assigns a reference (read: setpoint) for the PID controller
         * as the set rotations we get from Shuffleboard, then tells the
         * Spark Max to use closed-loop position mode (as opposed to, say,
         * closed-loop current mode)
         */
        m_pidController.setReference(rotations,ControlType.kPosition);

        SmartDashboard.putNumber("SetPoint",rotations);
        SmartDashboard.putNumber("ProcessVariable",m_encoder.getPosition());

        //Print PID loop stats every 10 iterations
        looperCounter++;
        if(looperCounter >= 10) { printPIDF(); looperCounter = 0; }
    }

    /**
     * On disable, print the state of the PID loop
     */
    @Override
    public void disabledInit() {
        System.out.print("\tDISABLED");
        printPIDF();
    }
}