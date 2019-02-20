package commons.run;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.init.ArtifactFactory; //for the 'addSuffix()' method
import commons.SampleException;
import commons.api.GatewayCloudMqtt;
import commons.connectivity.MqttClient;
import commons.init.DeviceProperties;
import commons.model.Authentication;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Sensor;
import commons.model.gateway.Measure;
import commons.utils.Console;
import commons.utils.EntityFactory;
import commons.init.SecurityUtilX;

public class DeviceTwin extends Device implements Runnable {

	protected DeviceProperties deviceProperties;
	protected GatewayCloudMqtt gatewayCloud;
	protected String iotCoreUrlHostname;
	
	//Make dummy artifacts (because the MQTTClient and GatewayCloudMqtt classes expect them)
	//Note: Only the 'xxxAlternateId' fields in each one need to be populated!
	protected Device device;
	protected Sensor sensor;
	protected Capability measureCapability;

	public DeviceTwin( int deviceInstanceNo, String iotCoreUrlHostname ) throws SampleException {
		Console.printSeparator();
		Console.printText( String.format( ">>>>>>>>> Configuring Device # %1$s... <<<<<<<<<", deviceInstanceNo ) );
		
		this.iotCoreUrlHostname = iotCoreUrlHostname;
		
		//read the RESPECTIVE 'device_XX.properties' Properties file
		String suffix = addSuffix( deviceInstanceNo );
		String devicePropertiesFilename = String.format( "device%1$s.properties", suffix );
		deviceProperties  = new DeviceProperties( devicePropertiesFilename );

		// Grab the essential Properties
		String deviceAlternateId     = deviceProperties.getDeviceAltId();
		String sensorAlternateId     = deviceProperties.getSensorAltId();
		String capabilityAlternateId = deviceProperties.getMeasureCapabilityAltId();  //Measure (not Command)
		String keystoreSecret        = deviceProperties.getKeystoreSecret();

		//Make a dummy Device, Sensor and Capability object (because the MQTTClient and GatewayCloudMqtt classes expect one)
		//Only the 'AlternateId' fields need to be populated!
		device = new Device();
		device.setAlternateId( deviceAlternateId );
		sensor = new Sensor();
		sensor.setAlternateId( sensorAlternateId );
		measureCapability = new Capability();
		measureCapability.setAlternateId( capabilityAlternateId );
		
		try {
			SSLSocketFactory sslSocketFactory = SecurityUtilX.getSSLSocketFactory( suffix, keystoreSecret );  //may throw 'GeneralSecurityException'
			
			//switch (gatewayProtocol) {
			//case MQTT:
				this.gatewayCloud = new GatewayCloudMqtt(device, sslSocketFactory);
			//	break;
			//case REST:
			//default:
			//	gatewayCloud = new GatewayCloudHttp(device, sslSocketFactory);
			//	break;
			//}

			Console.printSeparator();

			//sendAmbientMeasures(sensor, measureCapability);

			///receiveAmbientMeasures(measureCapability, device);
	
		} catch (IOException | GeneralSecurityException /* | IllegalStateException */ e ) {
			throw new SampleException(e.getMessage());
		}


	}

	protected static String addSuffix( int deviceInstanceNo ) {
		String suffix = new DecimalFormat("00").format( deviceInstanceNo ); 
		return "_" + suffix;
	}

	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		// YOU NEED
		// a) a 'device'
		// b) an 'SSLSocketFactory'
				
	}

	public void sendDemoMeasures()
	throws IOException {
	
		try {
			gatewayCloud.connect( this.iotCoreUrlHostname );
		} catch (IOException e) {
			throw new IOException( String.format( "Unable to connect to the Gateway Cloud at $1$s", this.iotCoreUrlHostname), e);
		}
	
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {
	
			@Override
			public void run() {
				Measure measure = ArtifactFactory.buildDemoMeasure( sensor, measureCapability );
	
				try {
					gatewayCloud.sendMeasure(measure);
				} catch (IOException e) {
					Console.printError(e.getMessage());
				} finally {
					Console.printSeparator();
				}
			}
	
		}, 0, 1000, TimeUnit.MILLISECONDS);
	
		try {
			executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new IOException("Interrupted exception", e);
		} finally {
			executor.shutdown();
			gatewayCloud.disconnect();
		} //end: try/catch/finally
		
	} //end: sendAmbientMeasures()

}
