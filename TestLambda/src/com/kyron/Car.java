package com.kyron;

import java.util.concurrent.TimeUnit;

/*
 * - More complex than String/Integer class
 * - Add its method to callable list
 * - InvokeAll and do more operation (map, filter null, calculate max value, etc)
 */
public class Car {
	
	private String color;
	
	// constructor
	public Car() {
		super();
	}
	public Car(String color) {
		this.color = color;
	}
	public int getBBValue() {
		// calculate blue book value
		return 5;
	}
	
	public Car findCar(String color) throws InterruptedException {	
		//TimeUnit.SECONDS.sleep(1);
		long x = 2;
		for (int i=0; i<10; i++) {
			 x = x^i;
		}
		Car retval = (color.equalsIgnoreCase(this.color) ? this : null);
		return retval;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

}
