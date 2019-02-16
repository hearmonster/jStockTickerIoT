package commons.init;

import java.io.IOException;

import commons.SampleException;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayProtocol;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.utils.Console;

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
			
	//Finishing with catch block from 'SampleApp'...
		//import java.security.GeneralSecurityException;

		} catch (IOException /*| GeneralSecurityException */ | IllegalStateException e) {
			throw new SampleException(e.getMessage());
		}

		
		properties.setProperty( "Krisha", "work" );
		properties.writeProperties();
	}

	
}
