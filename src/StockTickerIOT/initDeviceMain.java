package StockTickerIOT;

import commons.SampleException;
import commons.utils.Console;

public class initDeviceMain {
	
	public static void main(String[] args) {
		try {
			CreateArtifacts artifacts = new CreateArtifacts();
		}
		catch (SampleException e) {
			Console.printError(String.format("Unable to run the sample - %1$s", e.getMessage()));
			System.exit(1);
		}
		
		
	}

}
