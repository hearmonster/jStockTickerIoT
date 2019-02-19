package commons.init;

import java.io.IOException;
import commons.api.CoreService;
import commons.connectivity.HttpClient;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.utils.Console;

//added in order to replicate AbstractCoreServiceSample.getOrAddXXXX() methods

public class CoreServiceX extends CoreService {

	protected HttpClient httpClient;

	protected String baseUri;

	public CoreServiceX(String host, String instance, String tenant, String user, String password) {
		//all the superclass's Constructor is, is the same as below (initialize 'baseUri' and 'httpClientX'):
		super(host, instance, tenant, user, password);

		baseUri = String.format("https://%1$s/iot/core/api/v1", host);
		httpClient = new HttpClient(user, password);
	}


	public Capability getCapabilityByName( String capName )
	throws IOException {
	//Takes the Name(String) of a Capability
	//Returns either 'null' or the first capability that matches (in the event there are more than one)
		Capability result = null;
		
		//?filter=name%20eq%20'Notify%20Device'&skip=0&top=100
		//String filter =  URLEncoder.encode( "?filter=name eq '" + capName + "'&skip=0&top=100", "UTF-8");
		
		String destination = String.format("%1$s/capabilities?filter=name eq '%2$s'&skip=0&top=100", baseUri, capName);

		try {
			httpClient.connect(destination);
			
			Capability[] filteredCapabilities = httpClient.doGet(Capability[].class);  //potentially returns zero or more Capabilities

			if (filteredCapabilities.length == 0) {
				Console.printWarning(String.format("No '%1$s' Capability found", capName ));
			} else if (filteredCapabilities.length > 1) {
				Console.printWarning(String.format("Multiple '%1$s' Capabilities found - returning only the first", capName ));
				result = filteredCapabilities[0];
			} else {
				Console.printText(String.format("'%1$s' Capability found", capName ));
				result = filteredCapabilities[0];
			}
		} finally {
			httpClient.disconnect();
		}
		return result;
	}

	
	public SensorType getSensorTypeByName( String sensortypeName )
	throws IOException {
	//Takes the Name(String) of a SensorType
	//Returns either 'null' or the first SensorType that matches (in the event there are more than one)
		SensorType result = null;
		
		String destination = String.format("%1$s/sensorTypes?filter=name eq '%2$s'&skip=0&top=100", baseUri, sensortypeName);

		try {
			httpClient.connect(destination);
			
			SensorType[] filteredSensorTypes = httpClient.doGet(SensorType[].class);  //potentially returns zero or more Capabilities

			if (filteredSensorTypes.length == 0) {
				Console.printWarning(String.format("No '%1$s' SensorType found", sensortypeName ));
			} else if (filteredSensorTypes.length > 1) {
				Console.printWarning(String.format("Multiple '%1$s' SensorTypes found - returning only the first", sensortypeName ));
				result = filteredSensorTypes[0];
			} else {
				Console.printText(String.format("'%1$s' SensorType found", sensortypeName ));
				result = filteredSensorTypes[0];
			}
		} finally {
			httpClient.disconnect();
		}
		return result;
	}

	
	public Device getDeviceByName( String deviceName, String gatewayId )
	throws IOException {
	//Takes the Name(String) of a Device >>>and the GatewayId it "hangs" under<<<
	//Returns either 'null' or the first Device that matches (in the event there are more than one)
		Device result = null;
		
		String destination = String.format("%1$s/devices?filter=name eq '%2$s' and gatewayId eq '%3$s'&skip=0&top=100", baseUri, deviceName, gatewayId );

		try {
			httpClient.connect(destination);
			
			Device[] filteredDevices = httpClient.doGet(Device[].class);  //potentially returns zero or more Devices

			if (filteredDevices.length == 0) {
				Console.printWarning(String.format("No '%1$s' Device found", deviceName ));
			} else if (filteredDevices.length > 1) {
				Console.printWarning(String.format("Multiple '%1$s' Devices found - returning only the first", deviceName ));
				result = filteredDevices[0];
			} else {
				Console.printText(String.format("'%1$s' Device found", deviceName ));
				result = filteredDevices[0];
			}
		} finally {
			httpClient.disconnect();
		}
		return result;
	}

	public Sensor getSensorByName( String sensorName )
	throws IOException {
	//Takes the Name(String) of a Sensor
	//Returns either 'null' or the first Sensor that matches (in the event there are more than one)
		Sensor result = null;
		
		String destination = String.format("%1$s/sensors?filter=name eq '%2$s'&skip=0&top=100", baseUri, sensorName);

		try {
			httpClient.connect(destination);
			
			Sensor[] filteredSensors = httpClient.doGet(Sensor[].class);  //potentially returns zero or more Sensors

			if (filteredSensors.length == 0) {
				Console.printWarning(String.format("No '%1$s' Sensor found", sensorName ));
			} else if (filteredSensors.length > 1) {
				Console.printWarning(String.format("Multiple '%1$s' Sensors found - returning only the first", sensorName ));
				result = filteredSensors[0];
			} else {
				Console.printText(String.format("'%1$s' Sensor found", sensorName ));
				result = filteredSensors[0];
			}
		} finally {
			httpClient.disconnect();
		}
		return result;
	}

	
	//******************************************************************************************************
	// getOrAdd_XXX
	
	public Capability getOrAddMeasureCapability() throws IOException {
		//Ensure Measure Capability (and Properties) exist...
		Capability capability = null;  //return value
		
		//Look up the name from the '<Device-specific>ArtifactFactory'
		String capabilityName = ArtifactFactory.getMeasureCapabilityName();

		//Now perform a search of IoT Core to see whether it's already been created up there
		capability = getCapabilityByName( capabilityName );
		
		//If the Capability doesn't exist, create it in IoT Core...
		if ( capability == null) {
			Console.printSeparator();
			Console.printText( String.format("Attempting to create Measure Capability..." ) );
			//Obtain a template from the '<Device-specific>ArtifactFactory'
			Capability templateCapability = ArtifactFactory.buildMeasureCapability();
			//Now create it in IoT Core
			//Note that the Capability returned is the actual instance created within IoT Core, not the template I passed
			//TODO catch{} for errors
			capability = addCapability( templateCapability );
		}

		Console.printNewLine();
		Console.printText( String.format("IoT Core Measure <Capability ID>: %1$s", capability.getId() ) );
		return capability;
	}


	public Capability getOrAddCommandCapability() throws IOException {
		//Ensure Command Capability (and Properties) exist...
		Capability capability = null;  //return value
		
		//Look up the name from the '<Device-specific>ArtifactFactory'
		String capabilityName = ArtifactFactory.getCommandCapabilityName();

		//Now perform a search of IoT Core to see whether it's already been created up there
		capability = getCapabilityByName( capabilityName );
		
		//If the Capability doesn't exist, create it in IoT Core...
		if ( capability == null) {
			Console.printSeparator();
			Console.printText( String.format("Attempting to create Command Capability..." ) );
			//Obtain a template from the '<Device-specific>ArtifactFactory'
			Capability templateCapability = ArtifactFactory.buildCommandCapability();
			//Now create it in IoT Core
			//Note that the Capability returned is the actual instance created within IoT Core, not the template I passed
			//TODO catch{} for errors
			capability = addCapability( templateCapability );
		}

		Console.printNewLine();
		Console.printText( String.format("IoT Core Command <Capability ID>: %1$s", capability.getId() ) );
		return capability;
	}

	

	public SensorType getOrAddSensorType( Capability measureCapability, Capability commandCapability ) throws IOException {
		//Ensure SensorType exists...
		SensorType sensortype = null;	//return value
		
		//Look up the name from the '<Device-specific>ArtifactFactory'
		String capabilityName = ArtifactFactory.getSensorTypeName();

		//Now perform a search of IoT Core to see whether it's already been created up there
		sensortype = getSensorTypeByName( capabilityName );
		
		//If the SensorType doesn't exist, create it in IoT Core...
		if ( sensortype == null) {
			Console.printSeparator();
			Console.printText( String.format("Attempting to create SensorType..." ) );
			//Obtain a template from the '<Device-specific>ArtifactFactory'
			SensorType templateSensorType = ArtifactFactory.buildSensorType( measureCapability, commandCapability );
			//Now create it in IoT Core
			//Note that the SensorType returned is the actual instance created within IoT Core, not the template I passed
			//TODO catch{} for errors
			sensortype = addSensorType( templateSensorType );
		}

		Console.printNewLine();
		Console.printText( String.format("IoT Core Command <SensorType ID>: %1$s", sensortype.getId() ) );
		return sensortype;
	}



	public Device getOrAddDevice( Gateway gateway, int deviceInstanceNo ) throws IOException {
		//Ensure Device exists...
		Device device = null;	//return value
		
		//Look up the name from the '<Device-specific>ArtifactFactory'
		String deviceName = ArtifactFactory.getDeviceName( deviceInstanceNo );

		//Now perform a search of IoT Core to see whether it's already been created up there
		device = getDeviceByName( deviceName, gateway.getId() );
		
		//If the Device doesn't exist, create it in IoT Core...
		if ( device == null) {
			Console.printSeparator();
			Console.printText( String.format("Attempting to create Device..." ) );
			//Obtain a template from the '<Device-specific>ArtifactFactory'
			Device templateDevice = ArtifactFactory.buildDevice( gateway );
			//Now create it in IoT Core
			//Note that the Device returned is the actual instance created within IoT Core, not the template I passed
			//TODO catch{} for errors
			device = addDevice( templateDevice );
		}

		Console.printNewLine();
		Console.printText( String.format("IoT Core Command <Device ID>: %1$s", device.getId() ) );
		return device;
	}


	public Sensor getOrAddSensor( Device device, SensorType sensortype, int deviceInstanceNo ) throws IOException {
		//Ensure the Sensor exists...
		Sensor sensor = null;	//return value
		
		//Look up the name from the '<Device-specific>ArtifactFactory'
		String sensorName = ArtifactFactory.getSensorName( deviceInstanceNo );

		//Now perform a search of IoT Core to see whether it's already been created up there
		sensor = getSensorByName( sensorName );
		
		//If the Sensor doesn't exist, create it in IoT Core & attach it to my Device...
		if ( sensor == null) {
			Console.printSeparator();
			Console.printText( String.format("Attempting to create Sensor..." ) );
			//Obtain a template from the '<Device-specific>ArtifactFactory'
			Sensor templateSensor = ArtifactFactory.buildSensor( device, sensortype  );
			//Now create it in IoT Core
			//Note that the Device returned is the actual instance created within IoT Core, not the template I passed
			//TODO catch{} for errors
			sensor = addSensor( templateSensor );
		}

		Console.printNewLine();
		Console.printText( String.format("IoT Core <Sensor ID>: %1$s", sensor.getId() ) );
		return sensor;
	}


	//Overrides 'SecurityUtil.getAuthentication' with my extended 'AuthenticationX' class (extended with a 'p12' field)
	public AuthenticationX getAuthenticationX( Device device )
	throws IOException {
		String destination = String.format("%1$s/devices/%2$s/authentications/clientCertificate/p12", baseUri,
			device.getId());

		try {
			httpClient.connect(destination);
			return httpClient.doGet(AuthenticationX.class);
		} finally {
			httpClient.disconnect();
		}
	}


	//WRAPPERS
	
	// 'renaming' the parent's 'getDevice()' to be consistent with my naming approach (i.e. 'getDeviceByName()', 'getDeviceById()' etc.)
	public Device getDeviceById(String deviceId, Gateway gateway) throws IOException {
		return getDevice( deviceId, gateway);
	}
	
	//Not so much a wrapper, but an addition ('CoreService' doesn't have any 'getSensor()' at all)
	public Sensor getSensorById( String sensorId )
	throws IOException {
		String destination = String.format("%1$s/sensors/%2$s", baseUri, sensorId );

		try {
			httpClient.connect(destination);
			return httpClient.doGet(Sensor.class);
		} finally {
			httpClient.disconnect();
		}
	}

	


	
	
}
