package net.luminacollection.rollbound.common.color;

import net.luminacollection.rollbound.common.roll.SuccessState.Triggered;

import java.awt.Color;

public class SuccessColor
{
	public static String byPercentage(int percentage)
	{
		if (percentage < 0 || percentage > 100) throw new IllegalArgumentException("Percentage must be between 0 and 100.");
		float factorGreen = percentage / 100.0F;
		var red = 255;
		var green = 255;
		var modifier = Math.round(factorGreen * 510);
		if (modifier > 255) red = 510 - modifier;
		else green = modifier;
		return String.format("#%02x%02x%02x", red, green, 0);
	}
	
	public static String byFacesAndResult(int faces, int result, Triggered triggered)
	{
		if (faces < 1) throw new IllegalArgumentException("Faces must be greater than 0.");
		if (result < 1 || result > faces) throw new IllegalArgumentException("Result must be between 1 and " + faces + ".");
		float percentage = (float) result / (float) faces * 100F;
		if (triggered == Triggered.BELOW) percentage = 100 - percentage;
		return byPercentage((int) percentage);
	}
}
