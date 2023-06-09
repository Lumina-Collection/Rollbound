package net.luminacollection.rollbound.common.roll;

import java.security.SecureRandom;

public record Die(int numberOfFaces)
{
	public static Die d6 = new Die(6);
	public static Die d20 = new Die(20);
	public static Die d100 = new Die(100);
	
	
	public Die {
		if (numberOfFaces < 2 || numberOfFaces > 999) numberOfFaces = 6;
	}
	
	public int roll() {
		return new SecureRandom().nextInt(numberOfFaces) + 1;
	}
	
	public String toString() {
		return "1d" + numberOfFaces;
	}
}
