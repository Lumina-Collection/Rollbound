package net.luminacollection.rollbound.common.utils;

import net.luminacollection.rollbound.common.roll.Die;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserStringDice
{
	private static final String dieRegex = "\\d{1,2}[wd]\\d{1,3}";
	private static final String separatorRegex = "\\.";
	private static final String modifierRegex = "[+-]\\d{1,3}";
	private static final String regex = dieRegex + "(" + separatorRegex + dieRegex + ")*(" + modifierRegex + ")?";
	
	public static Die[] dice(int[][] diceGroups)
	{
		var dice = new Die[0];
		for (int[] diceGroup : diceGroups) {
			var newDice = new Die[dice.length + diceGroup[0]];
			System.arraycopy(dice, 0, newDice, 0, dice.length);
			for (int i = 0; i < diceGroup[0]; i++) {
				newDice[dice.length + i] = new Die(diceGroup[1]);
			}
			dice = newDice;
		}
		return dice;
	}
	
	public static int modifier(String stringToParse)
	{
		stringToParse = stringToParse.toLowerCase();
		
		if (!stringToParse.matches(regex)) throw new IllegalArgumentException("Given string has invalid format.");
		
		Matcher modifierMatcher = Pattern.compile(modifierRegex).matcher(stringToParse);
		var modifier = 0;
		if (modifierMatcher.find()) {
			modifier = Integer.parseInt(modifierMatcher.group());
		}
		
		return modifier;
	}
	
	public static boolean matches(String rollString)
	{
		return rollString.matches(regex);
	}
	
	public static int[][] diceGroups(String stringToParse)
	{
		stringToParse = stringToParse.toLowerCase();
		
		if (!stringToParse.matches(regex)) throw new IllegalArgumentException("Given string has invalid format.");
		
		Matcher dieMatcher = Pattern.compile(dieRegex).matcher(stringToParse);
		var matches = (int) dieMatcher.results().count();
		var i = matches;
		var diceGroups = new int[matches][2];
		dieMatcher.reset();
		while (dieMatcher.find()) {
			var count = Integer.parseInt(dieMatcher.group().split("[wd]")[0]);
			var faces = Integer.parseInt(dieMatcher.group().split("[wd]")[1]);
			diceGroups[matches - i][0] = count;
			diceGroups[matches - i][1] = faces;
			i--;
		}
		
		return diceGroups;
	}
}
