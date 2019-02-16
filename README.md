# jStockTickerIoT
StockMarket Ticker for IoT (without CSV integration yet)

An extension Java Project that creates the StockMarket Ticker Device (with a Measure and Command Capability).  Not using Maven (currently).

Builds upon IoTStarterkit_java-samples (uses its 'commons.jar' - it's comes included under /lib in the project)

Comprises of two packages:
* 'commons.init' which are effectively rewrites of the classes I couldn't coerce to my will from the original 'commons.jar'
* 'StockTickerIOT' which extracts all the StockMarket-relevant properties into a single class (for easy future conversion into different demo schemas).  It's also where you'll find the 'main' class to initialize the artifacts in IoT Core

## Properties files
* input:  C:\Users\i817399\eclipse-workspace\jStockTickerIoT\sample.properties
* output: C:\Users\i817399\eclipse-workspace\jStockTickerIoT\file.properties

(Eventually I need to overwrite sample.properties instead of creating a new one)
