package StockTickerIOT;

import java.io.IOException;

import commons.SampleException;
import commons.init.CoreServiceX;
import commons.init.DeviceProperties;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayProtocol;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.utils.Console;
import commons.init.ArtifactFactory;

public class CreateArtifacts extends ArtifactFactory {
	//'ArtifactFactory' in turn extends the '<Device-specific>ArtifactFactory' class
	protected DeviceProperties properties;
	protected CoreServiceX coreService;

	public CreateArtifacts() throws SampleException {

		//read the device.properties Properties file
		properties = new DeviceProperties();

	//Starting 'AbstractCoreServiceSample' class...
		String host = 		properties.getStringProperty( properties.IOT_HOST);
		String instance = 	properties.getStringProperty( properties.INSTANCE_ID);
		String tenant = 	properties.getStringProperty( properties.TENANT_ID);
		String user = 		properties.getStringProperty( properties.IOT_USER);
		String password = 	properties.getStringProperty( properties.IOT_PASSWORD);

		coreService = new CoreServiceX(host, instance, tenant, user, password);

		//TODO Do I need this?!
		//(import)>>  		import java.util.Comparator;
		//(Class Field)>>  	private Comparator<SensorTypeCapability> sensorTypeCapabilityComparator;
		//sensorTypeCapabilityComparator = Comparator.comparing(SensorTypeCapability::getId);

	//Now starting 'SampleApp'...
		String deviceId = properties.getStringProperty( properties.DEVICE_ID);
		String sensorId = properties.getStringProperty( properties.SENSOR_ID);
		String GwyProtId = properties.getStringProperty( properties.GATEWAY_PROTOCOL_ID);
		GatewayProtocol gatewayProtocol = GatewayProtocol.fromValue( GwyProtId );

		try {
			Console.printSeparator();
			Console.printText( String.format( "Searching for gateway of Protocol Type: %1$s ...", gatewayProtocol ));

			Gateway gateway = coreService.getOnlineCloudGateway(gatewayProtocol);

			Console.printSeparator();
			Console.printText( "Searching for Measure Capability...");

			Capability measureCapability = coreService.getOrAddMeasureCapability();

			Console.printSeparator();
			Console.printText( "Searching for Command Capability...");
			
			Capability commandCapability = coreService.getOrAddCommandCapability();
			
			Console.printSeparator();
			Console.printText( "Searching for SensorType...");
			
			SensorType sensortype = coreService.getOrAddSensorType( measureCapability, commandCapability);

			Console.printSeparator();
			Console.printText( "Searching for Device...");
			
			Device device = coreService.getOrAddDevice( gateway );
			if ( device != null )
				properties.setProperty( properties.DEVICE_ID, device.getId() );

			Console.printSeparator();
			Console.printText( "Searching for Sensor (to add to my Device)...");
			
			Sensor sensor = coreService.getOrAddSensor( device, sensortype );
			if ( sensor != null )
				properties.setProperty( properties.SENSOR_ID, sensor.getId() );

			//Creating the Device...
			//I can't use the way that 'SampleApp' creates the Device i.e.
			//		Device device = getOrAddDevice(deviceId, gateway);
			// because the AbstractCoreServiceSample.getOrAddDevice() is hard-coded to build the Device from the "inflexible" '(Greenhouse)EntityFactory' Class - I can't override that
			// Instead, (if it can't find the Device) I need to create one of *my* Devices from *my* '(StockTicker)ArtifactFactory' Class 
			
			// So instead, I need to create a templateDevice from my Device class first, something like:
			//i.e.		Device templateDevice = buildTickerDevice(Gateway gateway)
			//and then pass my 'templateDevice' into a reworked 'getOrAddDevice()' method that
			//   a) searches for the Device in IoT Core
			// TODO  Improve the search ...by adding a filter that looks for the name?
			//  From IoT SDK: "It is possible to filter by 'id’, 'alternateId’, 'gatewayId’, 'name’, 'description’, and 'status’."
			//   b) (and if it can't find it) creates one *using my template*

			//I've created one in TickerArtifactFactory.getOrAddDevice()
			//TODO but I need to pass in the 'coreservice' (and improve the filter)
	//...Stop (for now).
			
			
	//Finishing with catch block from 'SampleApp'...
		//import java.security.GeneralSecurityException;

		} catch (IOException /*| GeneralSecurityException */ | IllegalStateException e) {
			throw new SampleException(e.getMessage());
		}

		
		properties.setProperty( "Krisha", "work" );
		properties.writeProperties();
	}

	
}
