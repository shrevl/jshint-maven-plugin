package com.shrevl.jshint.maven.plugin;


public class Error {
	private Integer line;
	private Integer character;
	private String reason;
	private String evidence;
	
	/**
	 * @return the line
	 */
	public Integer getLine() {
		return line;
	}

	/**
	 * @param line the line to set
	 */
	public void setLine(Integer line) {
		this.line = line;
	}

	/**
	 * @return the character
	 */
	public Integer getCharacter() {
		return character;
	}

	/**
	 * @param character the character to set
	 */
	public void setCharacter(Integer character) {
		this.character = character;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the evidence
	 */
	public String getEvidence() {
		return evidence;
	}

	/**
	 * @param evidence the evidence to set
	 */
	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}
}
