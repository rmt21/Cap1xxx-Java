/* Cap 1166 Java Library converted for Pimoroni TOUCH PHAT
 * V0.01
 * 
 * Reece Smith-Tyler 2018
 */


import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class Cap1xxxjava {
	
	
	// DEVICE MAP
	private int DEFAULT_ADDR = 0x28;

	// Supported devices
	private byte PID_CAP1208 = 0b01101011;
	private byte PID_CAP1188 = 0b01010000;
	private byte PID_CAP1166 = 0x2C;

	// REGISTER MAP

	private int R_MAIN_CONTROL      = 0x00;
	private int R_GENERAL_STATUS    = 0x02;
	private int R_INPUT_STATUS      = 0x03;
	private int R_LED_STATUS        = 0x04;
	private int R_NOISE_FLAG_STATUS = 0x0A;

	// Read-only delta counts for all inputs
	private int R_INPUT_1_DELTA   = 0x10;
	private int R_INPUT_2_DELTA   = 0x11;
	private int R_INPUT_3_DELTA   = 0x12;
	private int R_INPUT_4_DELTA   = 0x13;
	private int R_INPUT_5_DELTA   = 0x14;
	private int R_INPUT_6_DELTA   = 0x15;
	private int R_INPUT_7_DELTA   = 0x16;
	private int R_INPUT_8_DELTA   = 0x17;

	private int R_SENSITIVITY     = 0x1F;
	// B7     = N/A
	// B6..B4 = Sensitivity
	// B3..B0 = Base Shift
	//SENSITIVITY = {128: 0b000, 64:0b001, 32:0b010, 16:0b011, 8:0b100, 4:0b100, 2:0b110, 1:0b111} WHAT?

	private int R_GENERAL_CONFIG  = 0x20;
	// B7 = Timeout
	// B6 = Wake Config ( 1 = Wake pin asserted )
	// B5 = Disable Digital Noise ( 1 = Noise threshold disabled )
	// B4 = Disable Analog Noise ( 1 = Low frequency analog noise blocking disabled )
	// B3 = Max Duration Recalibration ( 1 =  Enable recalibration if touch is held longer than max duration )
	// B2..B0 = N/A

	private int R_INPUT_ENABLE    = 0x21;


	private int R_INPUT_CONFIG    = 0x22;

	private int R_INPUT_CONFIG2   = 0x23; // Default 0x00000111

	// Values for bits 3 to 0 of R_INPUT_CONFIG2
	// Determines minimum amount of time before
	// a "press and hold" event is detected.

	// Also - Values for bits 3 to 0 of R_INPUT_CONFIG
	// Determines rate at which interrupt will repeat
	//
	// Resolution of 35ms, max = 35 + (35 * 0b1111) = 560ms

	private int R_SAMPLING_CONFIG = 0x24; // Default 0x00111001
	private int R_CALIBRATION     = 0x26; // Default 0b00000000
	private int R_INTERRUPT_EN    = 0x27; // Default 0b11111111
	private int R_REPEAT_EN       = 0x28; // Default 0b11111111
	private int R_MTOUCH_CONFIG   = 0x2A; // Default 0b11111111
	private int R_MTOUCH_PAT_CONF = 0x2B;
	private int R_MTOUCH_PATTERN  = 0x2D;
	private int R_COUNT_O_LIMIT   = 0x2E;
	private int R_RECALIBRATION   = 0x2F;

	// R/W Touch detection thresholds for inputs
	private int R_INPUT_1_THRESH  = 0x30;
	private int R_INPUT_2_THRESH  = 0x31;
	private int R_INPUT_3_THRESH  = 0x32;
	private int R_INPUT_4_THRESH  = 0x33;
	private int R_INPUT_5_THRESH  = 0x34;
	private int R_INPUT_6_THRESH  = 0x35;
	private int R_INPUT_7_THRESH  = 0x36;
	private int R_INPUT_8_THRESH  = 0x37;

	// R/W Noise threshold for all inputs
	private int R_NOISE_THRESH    = 0x38;

	// R/W Standby and Config Registers
	private int R_STANDBY_CHANNEL = 0x40;
	private int R_STANDBY_CONFIG  = 0x41;
	private int R_STANDBY_SENS    = 0x42;
	private int R_STANDBY_THRESH  = 0x43;

	private int R_CONFIGURATION2  = 0x44;
	// B7 = Linked LED Transition Controls ( 1 = LED trigger is !touch )
	// B6 = Alert Polarity ( 1 = Active Low Open Drain, 0 = Active High Push Pull )
	// B5 = Reduce Power ( 1 = Do not power down between poll )
	// B4 = Link Polarity/Mirror bits ( 0 = Linked, 1 = Unlinked )
	// B3 = Show RF Noise ( 1 = Noise status registers only show RF, 0 = Both RF and EMI shown )
	// B2 = Disable RF Noise ( 1 = Disable RF noise filter )
	// B1..B0 = N/A

	// Read-only reference counts for sensor inputs
	private int R_INPUT_1_BCOUNT  = 0x50;
	private int R_INPUT_2_BCOUNT  = 0x51;
	private int R_INPUT_3_BCOUNT  = 0x52;
	private int R_INPUT_4_BCOUNT  = 0x53;
	private int R_INPUT_5_BCOUNT  = 0x54;
	private int R_INPUT_6_BCOUNT  = 0x55;
	private int R_INPUT_7_BCOUNT  = 0x56;
	private int R_INPUT_8_BCOUNT  = 0x57;

	// LED Controls - For CAP1188 and similar
	private int R_LED_OUTPUT_TYPE = 0x71;
	private int R_LED_LINKING     = 0x72;
	private int R_LED_POLARITY    = 0x73;
	private int R_LED_OUTPUT_CON  = 0x74;
	private int R_LED_LTRANS_CON  = 0x77;
	private int R_LED_MIRROR_CON  = 0x79;

	// LED Behaviour
	private int R_LED_BEHAVIOUR_1 = 0x81; // For LEDs 1-4
	private int R_LED_BEHAVIOUR_2 = 0x82;// For LEDs 5-8
	private int R_LED_PULSE_1_PER = 0x84;
	private int R_LED_PULSE_2_PER = 0x85;
	private int R_LED_BREATHE_PER = 0x86;
	private int R_LED_CONFIG      = 0x88;
	private int R_LED_PULSE_1_OUT = 0x90;
	private int R_LED_PULSE_2_OUT = 0x91;
	private int R_LED_BREATHE_OUT = 0x92;
	private int R_LED_DIRECT_OUT  = 0x93;
	private int R_LED_DIRECT_RAMP = 0x94;
	private int R_LED_OFF_DELAY   = 0x95;

	// R/W Power buttonc ontrol
	private int R_POWER_BUTTON    = 0x60;
	private int R_POW_BUTTON_CONF = 0x61;

	// Read-only upper 8-bit calibration values for sensors
	private int R_INPUT_1_CALIB   = 0xB1;
	private int R_INPUT_2_CALIB   = 0xB2;
	private int R_INPUT_3_CALIB   = 0xB3;
	private int R_INPUT_4_CALIB   = 0xB4;
	private int R_INPUT_5_CALIB   = 0xB5;
	private int R_INPUT_6_CALIB   = 0xB6;
	private int R_INPUT_7_CALIB   = 0xB7;
	private int R_INPUT_8_CALIB   = 0xB8;

	// Read-only 2 LSBs for each sensor input
	private int R_INPUT_CAL_LSB1  = 0xB9;
	private int R_INPUT_CAL_LSB2  = 0xBA;

	// Product ID Registers
	private int R_PRODUCT_ID      = 0xFD;
	private int R_MANUFACTURER_ID = 0xFE;
	private int R_REVISION        = 0xFF;

	// LED Behaviour settings
	private int LED_BEHAVIOUR_DIRECT  = 0b00;
	private int LED_BEHAVIOUR_PULSE1  = 0b01;
	private int LED_BEHAVIOUR_PULSE2  = 0b10;
	private int LED_BEHAVIOUR_BREATHE = 0b11;

	private int LED_OPEN_DRAIN = 0; // Default, LED is open-drain output with ext pullup
	private int LED_PUSH_PULL  = 1; // LED is driven HIGH/LOW with logic 1/0

	private int LED_RAMP_RATE_2000MS = 7;
	private int LED_RAMP_RATE_1500MS = 6;
	private int LED_RAMP_RATE_1250MS = 5;
	private int LED_RAMP_RATE_1000MS = 4;
	private int LED_RAMP_RATE_750MS  = 3;
	private int LED_RAMP_RATE_500MS  = 2;
	private int LED_RAMP_RATE_250MS  = 1;
	private int LED_RAMP_RATE_0MS    = 0;
	
	//Global variables
	byte repeat_enabled = 0b00000000;
	public static final int SENSITIVITY_HIGH = 0b00000000;   // 128x
	public static final int SENSITIVITY_NORMAL = 0b00110000; // 16x
    public static final int SENSITIVITY_LOW = 0b01100000;    // 2x
    // Max LED brightness.
    private static final int MAX_LED_BRIGHTNESS = 0b1111;
    private int INPUT_COUNT = 6;
    private int LED_COUNT = 6;
    private int[] LED_MAP = {5, 4, 3, 2, 1, 0};
    
    final GpioController gpio = GpioFactory.getInstance();
    private static GpioPinDigitalInput alert;
    
    CapListener cl;
	
	private I2CBus i2c;
	private I2CDevice device;
	
	
	public Cap1xxxjava(CapListener cl)
	{
		this.cl = cl;
		try {
			i2c = I2CFactory.getInstance(I2CBus.BUS_1);
		} catch (UnsupportedBusNumberException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialiseDevice();
	}
	
	public void initialiseDevice()
	{
		try {
			device = i2c.getDevice(PID_CAP1166);
			

			setInputsEnabled(true);
			setInterruptsEnabled(true);
			//setMultitouchInputMax(maxTouch);
			setRepeatRate(1);
			setSensitivity(SENSITIVITY_NORMAL);
			setLedFade(LED_RAMP_RATE_2000MS);
			setLedBrightness(1);
			setLedInputLinkEnabled(false);
			writeByte(R_LED_OUTPUT_CON, (byte) 0x00);
			
//			readRegBuffer(R_INPUT_1_DELTA, INPUT_COUNT, INPUT_COUNT+1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public byte readReg(int register)
	{
		try {
			return (byte) device.read(register);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Byte) null;
	}
	public byte[] readRegBuffer(int register, int count, int length)
	{
		try {
			byte[] temp = new byte[50];
			device.read(register, temp, count, length);
			return temp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void writeByte(int register, byte value)
	{
		try {
			device.write(register, value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setInputsEnabled(boolean enable)
	{
		if (enable == true)
		{
			writeByte(R_INPUT_ENABLE, (byte) 0b11111111);
		}
		else
		{
			writeByte(R_INPUT_ENABLE, (byte) 0x00);
		}
	}
	
	public void setInterruptsEnabled(boolean enable)
	{
		if (enable == true)
		{
			writeByte(R_INTERRUPT_EN, (byte) 0b11111111);
		}
		else
		{
			writeByte(R_INTERRUPT_EN, (byte) 0x00);
		}
	}
	
	public void setRepeatRate(int rate)
	{
		if (rate == -1)
		{
			setRepeatEnabled(false);
		}
		else
		{
			setRepeatEnabled(true);
			byte value = readReg(R_INPUT_CONFIG);
			value = BitwiseUtil.applyBitRange(value, rate, 0x0f);
			writeByte(R_INPUT_CONFIG, value);
			value = readReg(R_INPUT_CONFIG2);
			value = BitwiseUtil.applyBitRange(value, rate, 0x0f);
			writeByte(R_INPUT_CONFIG2, value);
		}
	}
	
	public void setRepeatEnabled(boolean enable)
	{
		if (enable == true)
		{
			writeByte(R_REPEAT_EN, (byte) 0b11111111);
		}
		else
		{
			writeByte(R_REPEAT_EN, (byte) 0x00);
		}
	}
	
	public void setLedInputLinkEnabled(boolean enable)
	{
		if (enable == true)
		{
			writeByte(R_LED_LINKING, (byte) 0b11111111);
		}
		else
		{
			writeByte(R_LED_LINKING, (byte) 0x00);
		}
	}
	
	public void setSensitivity(int sensitivity)
	{
		byte value = readReg(R_SENSITIVITY);
		value = BitwiseUtil.applyBitRange(value, sensitivity, 0x70);
		writeByte(R_SENSITIVITY, value);
	}
	
	public void setLedFade(int ledFade)
	{
		//assertLedSupport();
		writeByte(R_LED_BREATHE_OUT, (byte) ledFade);
		writeByte(R_LED_OFF_DELAY, (byte) (ledFade*0.75));
	}
	
	public void setLedBrightness(float ledBrightness)
	{
		if (ledBrightness < 0 || ledBrightness > 1)
		{
			System.out.println("ERROR BRIGHTNESS VALUE (0 OR 1)");
		}
		int val = Math.round(ledBrightness * MAX_LED_BRIGHTNESS);
		byte brightness = (byte)(val << 4);
		writeByte(R_LED_DIRECT_OUT, brightness);
	}
	
	public void setLedState(int index, boolean state)
	{
		if (index < 0 || index >= LED_COUNT)
		{
			System.out.println("LED INDEX NOT FOUND");
		}
		else
		{
			byte value = readLedStatus();
			if (state == true)
			{
				value = BitwiseUtil.setBit(value, LED_MAP[index]);
			}
			else
			{
				value = BitwiseUtil.clearBit(value, LED_MAP[index]);
			}
			writeByte(R_LED_OUTPUT_CON, value);
		}
	}
	
	public byte readLedStatus()
	{
		return readReg(R_LED_OUTPUT_CON);
	}
	
	public void clearLeds()
	{
		for (int i=0; i < LED_COUNT; i++)
		{
		setLedState(i, false);
		}
	}
	
	public boolean readInputChannel(int channel)
	{
		byte status = readInputStatus();
		return BitwiseUtil.isBitSet(status, channel);
	}
	
	public byte readInputStatus()
	{
		byte statusFlags = readReg(R_INPUT_STATUS);
		byte[] inputDeltas = readRegBuffer(R_INPUT_8_THRESH, INPUT_COUNT, INPUT_COUNT+1);
		byte[] inputThresholds = readRegBuffer(R_INPUT_8_DELTA, INPUT_COUNT, INPUT_COUNT+1);
		
		for (int i=0; i < INPUT_COUNT; i++)
		{
			if (BitwiseUtil.isBitSet(statusFlags, i))
			{
				if (inputDeltas[i] >= inputThresholds[i])
				{
					statusFlags = BitwiseUtil.setBit(statusFlags, i);
				}
				else
				{
					statusFlags = BitwiseUtil.clearBit(statusFlags, i);
				}
			}
		}
		return statusFlags;
		
	}
	public int[] readInputStatusSimple()
	{
		int[] statusFlagsSimple = new int[INPUT_COUNT];
		byte statusFlags = readReg(R_INPUT_STATUS);
		byte[] inputDeltas = readRegBuffer(R_INPUT_1_DELTA, INPUT_COUNT, INPUT_COUNT+1);
		byte[] inputThresholds = readRegBuffer(R_INPUT_1_THRESH, INPUT_COUNT, INPUT_COUNT+1);
		
		for (int i=0; i < INPUT_COUNT; i++)
		{
			if (BitwiseUtil.isBitSet(statusFlags, i))
			{
				if (inputDeltas[i] >= inputThresholds[i])
				{
					statusFlags = BitwiseUtil.setBit(statusFlags, i);
					statusFlagsSimple[i] = 1;
					setLedState(i, true);
				}
				else
				{
					statusFlags = BitwiseUtil.clearBit(statusFlags, i);
					statusFlagsSimple[i] = 0;
					setLedState(i, false);
				}
			}
		}
		
		readInterruptFlag(true);
		return statusFlagsSimple;
	}
	
	public boolean readInterruptFlag(boolean clear)
	{
		byte value = readReg(R_MAIN_CONTROL);
		boolean flag = BitwiseUtil.isBitSet(value, 0);
		
		if (flag && clear)
		{
			value = BitwiseUtil.clearBit(value, 0);
			writeByte(R_MAIN_CONTROL, value);
		}
		
		clearLeds();
		
		return flag;
	}

}
