package StockTickerIOT;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.SampleException;
import commons.api.GatewayCloudHttp;
import commons.api.GatewayCloudMqtt;
import commons.init.ClusterProperties;
import commons.init.CoreServiceX;
import commons.init.CreateArtifacts;
import commons.model.Authentication;
import commons.model.Capability;
import commons.model.Gateway;
import commons.model.GatewayProtocol;
import commons.model.Sensor;
import commons.model.gateway.Measure;
import commons.run.DeviceTwin;
import commons.utils.Console;
import commons.utils.EntityFactory;
import commons.utils.SecurityUtil;

public class SendMeasures {

	public SendMeasures() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		ClusterProperties clusterProperties = new ClusterProperties();
		String iotCoreUrlHostname = clusterProperties.getIotHost();
		/*
		String instance = 	clusterProperties.getInstanceId();
		String tenant = 	clusterProperties.getTenantId();
		String user = 		clusterProperties.getIotUser();
		String password = 	clusterProperties.getIotPassword();
		
		CoreServiceX coreService = new CoreServiceX(host, instance, tenant, user, password);
		String GwyProtId = clusterProperties.getGatewayProtocolId();
		GatewayProtocol gatewayProtocol = GatewayProtocol.fromValue( GwyProtId );
		Console.printSeparator();
		Console.printText( String.format( "Searching for gateway of Protocol Type: %1$s ...", gatewayProtocol ));
		//TODO Consider pushing the 'Gateway' into a CoreServiceX field
		// At the moment, I suspect it's better to keep it at this level, in order to keep the CoreServiceX flexible
		Gateway gateway;
*/
		try {
			//gateway = coreService.getOnlineCloudGateway(gatewayProtocol);
			int deviceCount = clusterProperties.getDeviceCount();
			if ( deviceCount < 1 )
				deviceCount = 1;
			
			DeviceTwin[] deviceTwin = new DeviceTwin[ deviceCount+1 ];  //ignore zero, start from one...
			for (int i = 1; i <= deviceCount; i++) {  //starting at 1 (not zero)
				deviceTwin[ i ] = new DeviceTwin( i, iotCoreUrlHostname );
				deviceTwin[ i ].sendDemoMeasures();
			} //end: for
					
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (SampleException e) {
			Console.printError(String.format("Unable to run the create the IoT Core artifacts - %1$s", e.getMessage()));
			System.exit(1);
		}
		
		//TODO Disabled (Cluster Properties don't change)
		//clusterProperties.writeProperties();
		Console.printSeparator();
		Console.printText( "Done." );
		
	}//end: main()
	
} //end: class
