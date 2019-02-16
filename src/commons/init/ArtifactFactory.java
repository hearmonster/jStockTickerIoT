package commons.init;

import StockTickerIOT.StockTickerArtifactFactory;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.Sensor;
import commons.model.SensorType;

public class ArtifactFactory extends StockTickerArtifactFactory {
//Provides generically-named 'BuildXXX' methods for the calling class (CreateArtifacts) to call
//It then translates the calls into the Device-specific method calls to the extended class 'below'

	public ArtifactFactory() {
		super();
	}

	//**********************************************************
	//CAPABILITIES
		//MEASURE
		public static String getMeasureCapabilityName() {
			return StockTickerArtifactFactory.getStockMarketMeasureCapabilityName();
		}
	
		public static Capability buildMeasureCapability() {
			return StockTickerArtifactFactory.buildStockMarketMeasureCapability();
		}

		//COMMAND
		public static String getCommandCapabilityName() {
			return StockTickerArtifactFactory.getStockMarketCommandCapabilityName();
		}

		public static Capability buildCommandCapability() {
			return StockTickerArtifactFactory.buildStockMarketCommandCapability();
		}

	//SENSOR TYPE
	public static String getSensorTypeName() {
		return StockTickerArtifactFactory.getStockMarketSensorTypeName();
	}

	public static SensorType buildSensorType( Capability measureCapability, Capability commandCapability ) {
		return StockTickerArtifactFactory.buildStockMarketSensorType( measureCapability, commandCapability );
	}

	//DEVICE
	public static String getDeviceName() {
		return StockTickerArtifactFactory.getStockMarketDeviceName();
	}

	public static Device buildDevice( Gateway gateway ) {
		return StockTickerArtifactFactory.buildStockMarketDevice( gateway );
	}

	//SENSOR
	public static String getSensorName() {
		return StockTickerArtifactFactory.getStockMarketSensorName();
	}

	public static Sensor buildSensor( Device device, SensorType sensorType ) {
		return StockTickerArtifactFactory.buildStockMarketSensor( device, sensorType );
	}

}
