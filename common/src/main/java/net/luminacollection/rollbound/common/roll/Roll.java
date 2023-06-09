package net.luminacollection.rollbound.common.roll;

import net.luminacollection.rollbound.common.utils.ParserStringDice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Roll
{
	private final int[][] diceGroups;
	private final Die[] dice;
	private final int[] diceResults;
	private final int modifier;
	private final int totalResult;
	private int threshold = 0;
	
	
	public Roll(String stringToParse) {
		this.diceGroups = ParserStringDice.diceGroups(stringToParse);
		this.dice = ParserStringDice.dice(diceGroups);
		this.diceResults = new int[dice.length];
		int totalResultWithoutModifier = 0;
		for (int i = 0; i < dice.length; i++) {
			diceResults[i] = dice[i].roll();
			totalResultWithoutModifier += diceResults[i];
		}
		this.modifier = ParserStringDice.modifier(stringToParse);
		this.totalResult = totalResultWithoutModifier + modifier;
	}
	
	public Map<Integer, Integer> diceWithResults() {
		var map = new HashMap<Integer, Integer>();
		for (int i = 0; i < dice.length; i++) {
			if (map.containsKey(dice[i].numberOfFaces()))
				map.put(dice[i].numberOfFaces(), map.get(dice[i].numberOfFaces()) + diceResults[i]);
			else
				map.put(dice[i].numberOfFaces(), diceResults[i]);
		}
		return map;
	}
	
	public int[] diceResults() {
		return diceResults;
	}
	
	public Die[] dice() {
		return dice;
	}
	
	public int modifier() {
		return modifier;
	}
	
	public int totalResult() {
		return totalResult;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dice.length; i++) {
			sb.append(dice[i].numberOfFaces());
			sb.append(" -> ");
			sb.append(diceResults[i]);
			sb.append("\n");
		}
		sb.append("Modifier: ");
		sb.append(modifier);
		sb.append("\n");
		sb.append("Total: ");
		sb.append(totalResult);
		return sb.toString();
	}
	
	public int[][] diceGroups()
	{
		return diceGroups;
	}
	
	public void setThreshold(int threshold)
	{
		this.threshold = threshold;
	}
	
	public int threshold()
	{
		return threshold;
	}
}
