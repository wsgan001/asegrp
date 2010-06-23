package imminer.core;

public enum PatternType { 
	AND_PATTERN, 
	OR_PATTERN, 
	XOR_PATTERN,
	COMBO_PATTERN,
	PRE_COMBO_PATTERN,	/*A type used to pre-compute AND patterns before actually starting computing OR and XOR patterns*/ 
	SINGLE_PATTERN
}