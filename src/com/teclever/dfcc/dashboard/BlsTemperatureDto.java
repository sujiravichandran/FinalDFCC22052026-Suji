package com.teclever.dfcc.dashboard;

import java.util.List;

public class BlsTemperatureDto {

	private List<String> ch1Temperature;
	private List<String> ch2Temperature;
	private List<String> ch3Temperature;
	private List<String> ch4Temperature;

	public List<String> getCh1Temperature() {
		return ch1Temperature;
	}

	public void setCh1Temperature(List<String> ch1Temperature) {
		this.ch1Temperature = ch1Temperature;
	}

	public List<String> getCh2Temperature() {
		return ch2Temperature;
	}

	public void setCh2Temperature(List<String> ch2Temperature) {
		this.ch2Temperature = ch2Temperature;
	}

	public List<String> getCh3Temperature() {
		return ch3Temperature;
	}

	public void setCh3Temperature(List<String> ch3Temperature) {
		this.ch3Temperature = ch3Temperature;
	}

	public List<String> getCh4Temperature() {
		return ch4Temperature;
	}

	public void setCh4Temperature(List<String> ch4Temperature) {
		this.ch4Temperature = ch4Temperature;
	}

}
