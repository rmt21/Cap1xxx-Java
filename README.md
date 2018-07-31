# Cap1xxx-Java
Cap1xxx library for Java, derived from python libraries to enable use on core java devices such as raspberry pi.

This library was written primarily for the Cap1166 chip found in Pimoronis Touch Phat which im currently using with the raspberry pi.

Uses pi4j for i2c communication and BitwiseUtil class from googles own library for this family of touch controllers.

Very simple to use and is mostly identical which the functionality this version offers with Pimoronis own code, I've not converted everything over however.

Create a basic handler class and it can be called from CapListener in order to register button presses. I run the following, please note it does require a little delay otherwise it will lock itself.

		Runnable checkInputs = new Runnable() {
			public void run() {
				cl.checkinputStatus(tp.readInputStatusSimple());
			}
		};
		executor.scheduleAtFixedRate(checkInputs, 5, 1, TimeUnit.SECONDS);
