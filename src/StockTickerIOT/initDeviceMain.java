package StockTickerIOT;

import java.io.IOException;

import commons.SampleException;
import commons.init.ClusterProperties;
import commons.init.CoreServiceX;
import commons.init.CreateArtifacts;
import commons.utils.Console;

import commons.model.Gateway;
import commons.model.GatewayProtocol;

public class initDeviceMain {
	
	//These are variables I *don't* think I want as fields..yet (TBD)
	//ClusterProperties clusterProperties;
	//CoreServiceX coreService

	public static void main(String[] args) {
		ClusterProperties clusterProperties = new ClusterProperties();
		String host = 		clusterProperties.getIotHost();
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
		try {
			gateway = coreService.getOnlineCloudGateway(gatewayProtocol);
			int deviceCount = clusterProperties.getDeviceCount();
			if ( deviceCount < 1 )
				deviceCount = 1;
			
			CreateArtifacts artifacts = new CreateArtifacts( coreService, deviceCount, gateway );

		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		catch (SampleException e) {
			Console.printError(String.format("Unable to run the create the IoT Core artifacts - %1$s", e.getMessage()));
			System.exit(1);
		}

		//TODO Disabled (Cluster Properties don't change)
		//clusterProperties.writeProperties();
		Console.printSeparator();
		Console.printText( "Done." );

	}

}
