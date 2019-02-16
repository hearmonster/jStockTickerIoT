package commons.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import commons.connectivity.ProxySelector;
import commons.model.GatewayProtocol;
import commons.utils.Console;
import commons.utils.Constants;
import commons.utils.FileUtil;

/**
 * An abstraction over all sample applications.
 */
public class DeviceProperties {

	private final String CONFIGURATIONS_FILE_NAME = "sample.properties";

	public final String IOT_HOST = "iot.host";
	public final String INSTANCE_ID = "instance.id";
	public final String TENANT_ID = "tenant.id";
	public final String IOT_USER = "iot.user";
	public final String IOT_PASSWORD = "iot.password";
	public final String DEVICE_ID = "device.id";
	public final String SENSOR_ID = "sensor.id";
	public final String SENSOR_TYPE_ID = "sensor.type.id";
	public final String CAPABILITY_ID = "capability.id";
	public final String GATEWAY_PROTOCOL_ID = "gateway.protocol.id";
	public final String PROXY_PORT = "proxy.port";
	public final String PROXY_HOST = "proxy.host";

	protected Properties properties;

	public DeviceProperties() {
		init();
	}




	/**
	 * Reads the configuration properties from the file located in the same directory to JAR archive. Sticks to the
	 * empty properties collection if the configuration file does not exist.
	 */
	private void init() {
		File jar = new File(DeviceProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String path = jar.getParentFile().getAbsolutePath().concat(System.getProperty("file.separator"))
			.concat(CONFIGURATIONS_FILE_NAME);
		Console.printText("Looking for properties in: " + path);
		try {
			path = URLDecoder.decode(path, Constants.DEFAULT_ENCODING.name());
		} catch (UnsupportedEncodingException e) {
			Console.printWarning("Unable to decode config file path.");
		}
		File config = new File(path);

		properties = new Properties();

		try {
			if (config.exists()) {
				properties = FileUtil.readProperties(new FileInputStream(config));
			}
		} catch (IOException e) {
			// do nothing
		} finally {
			promptProperties();
			printProperties();
		}

		setProxy();
	}

	/**
	 * Prompts the user for missing configuration properties.
	 */
	protected void promptProperties() {
		Console console = Console.getInstance();

		String host = properties.getProperty(IOT_HOST);
		host = console.awaitNextLine(host, "Hostname (e.g. 'trial.eu10.cp.iot.sap'): ");
		properties.setProperty(IOT_HOST, host);

		String instance = properties.getProperty(INSTANCE_ID);
		instance = console.awaitNextLine(instance, "Instance ID (e.g. 'demo'): ");
		properties.setProperty(INSTANCE_ID, instance);

		String tenant = properties.getProperty(TENANT_ID);
		tenant = console.awaitNextLine(tenant, "Tenant ID (e.g. '0123456789'): ");
		properties.setProperty(TENANT_ID, tenant);

		String user = properties.getProperty(IOT_USER);
		user = console.awaitNextLine(user, "Username (e.g. 'root'): ");
		properties.setProperty(IOT_USER, user);

		String gatewayType = properties.getProperty(GATEWAY_PROTOCOL_ID);
		gatewayType = console.awaitNextLine(gatewayType, "Gateway Protocol ID ('rest' or 'mqtt'): ");
		properties.setProperty(GATEWAY_PROTOCOL_ID, GatewayProtocol.fromValue(gatewayType).getValue());

		//Now OPTIONAL (indicates intention to create new Device)
		String deviceId = properties.getProperty(DEVICE_ID);
		if ((deviceId == null) || (deviceId.equals("")))
		{
			deviceId = console.nextLine("Device ID (e.g. '32' or leave empty): ");
			properties.setProperty(DEVICE_ID, deviceId);
		}

		//Now OPTIONAL (indicates intention to create new Sensor/Sensor Type)
		String sensorId = properties.getProperty(SENSOR_ID);
		if (sensorId == null || (sensorId.equals("")))
		{
			sensorId = console.nextLine("Sensor ID (e.g. '32' or leave empty): ");
			properties.setProperty(SENSOR_ID, sensorId);
		}

		String proxyHost = properties.getProperty(PROXY_HOST);
		if (proxyHost == null) {
			proxyHost = console.nextLine("Proxy Host (e.g. 'proxy' or leave empty): ");
			properties.setProperty(PROXY_HOST, proxyHost);
		}

		String proxyPort = properties.getProperty(PROXY_PORT);
		if (proxyPort == null) {
			proxyPort = console.nextLine("Proxy Port (e.g. '8080' or leave empty): ");
			properties.setProperty(PROXY_PORT, proxyPort);
		}

		String password = properties.getProperty(IOT_PASSWORD);
		if (password == null) {
			password = console.nextPassword("Password for your user: ");
			properties.setProperty(IOT_PASSWORD, password);
		}

		console.close();
	};

	
	/**
	 * Prints out the resulting configuration properties to the console. Skips user password and properties having empty
	 * values.
	 */
	private void printProperties() {
		Console.printNewLine();
		Console.printText("Properties:");
		for (Object key : properties.keySet()) {
			//if (IOT_PASSWORD.equals(key) || properties.get(key).toString().trim().isEmpty()) {
			//	continue;
			//}
			Console.printProperty(key, properties.get(key));
		}
		Console.printNewLine();
	}

	
	private void setProxy() {
		String proxyHost = properties.getProperty(PROXY_HOST);
		String proxyPort = properties.getProperty(PROXY_PORT);

		ProxySelector.setProxy(proxyHost, proxyPort);
	}

	
	public void setProperty( String key, String value ) {
		properties.put( key, value );
	}
	

	public String getStringProperty( String key ) {
		return properties.getProperty( key );
	}

	//Persist Properties to (NEW) file
	//TODO overwrite existing props file when I'm ready
	public void writeProperties() {
		String propertiesFile = System.getProperty("user.dir") + "\\file.properties";
		try(OutputStream propertiesFileWriter = new FileOutputStream(propertiesFile)){
			properties.store(propertiesFileWriter, "save to properties file");
		}catch(IOException ioe){
			ioe.printStackTrace();
		}

	}
	
}