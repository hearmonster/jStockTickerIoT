package StockTickerIOT;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import commons.model.Capability;
import commons.model.CapabilityType;
import commons.model.Command;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.Property;
import commons.model.PropertyType;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.model.SensorTypeCapability;
import commons.model.gateway.Measure;
import commons.utils.Console;

// Based upon commons.utils.'EntityFactory' class
public abstract class StockTickerArtifactFactory {

	private static final String SAMPLE_DEVICE_NAME = "StockMarketTickerDevice";
	private static final String SAMPLE_SENSOR_NAME = "StockMarketTickerSensor";

	private static final String STOCK_MARKET_SENSOR_TYPE_NAME = "StockMarketTickerSensortype";


	private static final String TICKER_MEASURE_CAPABILITY_NAME = "StockMarketTicker";  //"Ticker" => Measure
	private static final String TICKER_MEASURE_CAPABILITY_ALTERNATE_ID = "stockMarketTicker_ALTID";

	private static final String INSTRUCTION_COMMAND_CAPABILITY_NAME = "StockMarketInstruction";  //"Instruction" => Command
	private static final String INSTRUCTION_COMMAND_CAPABILITY_ALTERNATE_ID = "stockMarketInstruction_ALTID";

	private static final String SYMBOL_PROPERTY_NAME = "ST_Symbol";  //type: string
	private static final String PRICE_PROPERTY_NAME = "ST_Price";	//type:  double
	private static final String QUANTITY_PROPERTY_NAME = "ST_Quantity";  //: integer
	private static final String BUYSELL_PROPERTY_NAME = "ST_BuySell"; //type: string (Instruction: BUY or SELL)

	private static final String SYMBOL_PROPERTY_UOM = "CompanySymbol";
	private static final String PRICE_PROPERTY_UOM = "Dollars";
	private static final String QUANTITY_PROPERTY_UOM = "Shares";
	private static final String BUYSELL_PROPERTY_UOM = "BUY or SELL";

	//Order of construction
	//1a. Create 3 x Properties [Symbol, Price, Quantity] (for the Measure Capability) 
	//1b. Create 1 x Property [BuySell] (for the Command Capability)  
	
	//2. Create two Capabilities [StockMarketTicker_CAPABILITY, StockInstructionTicker_CAPABILITY]
	// (Measure and Capability respectfully)
	
	//3. Create the Sensor Type
	
	//4. Create the Sensor
	
	//5. Create the Device

//--------------------------------------------------------------------------------------------
// Generic Helpers
//The names are private to this class, and need to remain so

	public static String getStockMarketMeasureCapabilityName( ) {
		return TICKER_MEASURE_CAPABILITY_NAME;
	}

	public static String getStockMarketCommandCapabilityName( ) {
		return INSTRUCTION_COMMAND_CAPABILITY_NAME;
	}

		public static String getStockMarketSensorTypeName( ) {
		return STOCK_MARKET_SENSOR_TYPE_NAME;
	}

	public static String getStockMarketDeviceName( ) {
		return SAMPLE_DEVICE_NAME;
	}

	public static String getStockMarketSensorName( ) {
		return SAMPLE_SENSOR_NAME;
	}

//--------------------------------------------------------------------------------------------
// PROPERTIES 
// 1. For the upcoming Measure CAPABILITY,  ??? => Symbol (string), Quantity (int), Price (float)
// Create three Properties; a (Company) Symbol, a Price and a Quantity
	
	private static Property buildSymbolProperty() {
		Property property = new Property();

		property.setName(SYMBOL_PROPERTY_NAME);
		property.setDataType(PropertyType.STRING);
		property.setUnitOfMeasure(SYMBOL_PROPERTY_UOM);

		return property;
	}

	
	private static Property buildPriceProperty() {
		Property property = new Property();

		property.setName(PRICE_PROPERTY_NAME);
		property.setDataType(PropertyType.FLOAT);
		property.setUnitOfMeasure(PRICE_PROPERTY_UOM);

		return property;
	}

	private static Property buildQuantityProperty() {
		Property property = new Property();

		property.setName(QUANTITY_PROPERTY_NAME);
		property.setDataType(PropertyType.INTEGER);
		property.setUnitOfMeasure(QUANTITY_PROPERTY_UOM);

		return property;
	}

	// 2. For the upcoming Command CAPABLITY,  BUYSELL => Symbol (string), Quantity (int), BuyOrSell (string)
	// Reuse two Properties; the (Company) Symbol, and the Quantity
	// Plus, create a new one;   BuyOrSell (string)

	private static Property buildBuySellProperty() {
		Property property = new Property();

		property.setName(BUYSELL_PROPERTY_NAME);
		property.setDataType(PropertyType.STRING);
		property.setUnitOfMeasure(BUYSELL_PROPERTY_UOM);

		return property;
	}


	//--------------------------------------------------------------------------------------------
	// CAPABLITY - Measure
	// Wrap the three (Measure) Properties above into a single Capability, to form a Measure
	
	public static Capability buildStockMarketMeasureCapability() {
	//Deliberately keeping the method name generic (no mention of Stock Market/Ticker etc), to minimize impact on calling class
		Capability capability = new Capability();

		capability.setName(TICKER_MEASURE_CAPABILITY_NAME);
		capability.setAlternateId(TICKER_MEASURE_CAPABILITY_ALTERNATE_ID);
		capability.setProperties(
			new Property[] { buildSymbolProperty(), buildPriceProperty(), buildQuantityProperty() });

		return capability;
	}


	// CAPABLITY - command
	// Wrap the three (Instruction) Properties above into a single Capability, to form a Command
	public static Capability buildStockMarketCommandCapability() {
	//Deliberately keeping the method name generic (no mention of Stock Market/Ticker etc), to minimize impact on calling class
		Capability capability = new Capability();

		capability.setName(INSTRUCTION_COMMAND_CAPABILITY_NAME);
		capability.setAlternateId(INSTRUCTION_COMMAND_CAPABILITY_ALTERNATE_ID);
		capability.setProperties(new Property[] { buildSymbolProperty(), buildQuantityProperty(), buildBuySellProperty(),  });

		return capability;
	}

	
	//--------------------------------------------------------------------------------------------
	// SENSOR-TYPE
	
	public static SensorType buildStockMarketSensorType(Capability measureCapability, Capability commandCapability) {
		SensorType sensorType = new SensorType();

		sensorType.setName(STOCK_MARKET_SENSOR_TYPE_NAME);

		SensorTypeCapability measure = new SensorTypeCapability();
		measure.setId(measureCapability.getId());
		measure.setType(CapabilityType.MEASURE);

		SensorTypeCapability command = new SensorTypeCapability();
		command.setId(commandCapability.getId());
		command.setType(CapabilityType.COMMAND);

		sensorType.setCapabilities(new SensorTypeCapability[] { measure, command });

		return sensorType;
	}

	
	//--------------------------------------------------------------------------------------------
	// SENSOR (instance)
	public static Sensor buildStockMarketSensor(Device device, SensorType sensorType) {
		Sensor sensor = new Sensor();

		sensor.setDeviceId(device.getId());
		sensor.setSensorTypeId(sensorType.getId());
		sensor.setName(SAMPLE_SENSOR_NAME);

		return sensor;
	}

	//--------------------------------------------------------------------------------------------
	// DEVICE (instance)

	

	
	public static Device buildStockMarketDevice(Gateway gateway) {
		Device device = new Device();

		device.setGatewayId(gateway.getId());
		device.setName(SAMPLE_DEVICE_NAME);

		return device;
	}


	//SENDING EVENTS
	//============================================================================================
	
	//Create random Symbol (random 4-char string)
	//TODO change random string to one of a list of valid/existing Symbols
	private static String buildSymbol() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4);
	}
	

	//Create random Price (float)
	private static float buildPrice() {
		float min = -100.0f;
		float max = 100.0f;

		float randomFloat = new Random().nextFloat() * (max - min) + min;

		return BigDecimal.valueOf(randomFloat).setScale(1, BigDecimal.ROUND_HALF_EVEN).floatValue();
	}


	//Create random Quantity (integer)
	private static int buildQuantity() {
	int min = 0;
	int max = 1000;

	return new Random().nextInt(max - min + 1) + min;
	}

	
	//Create random Instruction (string: BUY, SELL)
	private static String buildInstruction() {
		boolean b = new Random().nextBoolean();
		String instruction = "";
		
		if (b)
			instruction = "BUY";
		else
			instruction = "SELL";
	return instruction;
	}

	//Keep example of (boolean)
	/*
	private static boolean buildLEDValue() {
		return new Random().nextBoolean();
	}
	*/


	
	//--------------------------------------------------------------------------------------------
	// this is called while sending events - the "buildXXX" methods create random values
	//This creates MEASURES
	public static Measure buildTickerMeasure(Sensor sensor, Capability capability) {
		Measure measure = new Measure();

		measure.setCapabilityAlternateId(capability.getAlternateId());
		measure.setSensorAlternateId(sensor.getAlternateId());
		measure.setMeasures(
			new Object[][] { { buildSymbol(), buildPrice(), buildQuantity() } });

		return measure;
	}

	//This creates COMMANDS
	public static Command buildInstructionCommand(Sensor sensor, Capability capability) {
		Command command = new Command();

		command.setCapabilityId(capability.getId());
		command.setSensorId(sensor.getId());

		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put(SYMBOL_PROPERTY_NAME, buildSymbol());
		properties.put(QUANTITY_PROPERTY_NAME, buildQuantity());
		properties.put(BUYSELL_PROPERTY_NAME, buildInstruction());

		command.setProperties(properties);

		return command;
	}

}


