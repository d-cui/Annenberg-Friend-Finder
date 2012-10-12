package com.harvard.annenberg;

public class Person {
	String name;
	String img;
	String status;
	String table;
	String time;

	public Person(String name, String img, String status, String table,
			String time) {
		super();
		this.name = name;
		this.img = img;
		this.status = status;
		this.table = table;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public String getImg() {
		return img;
	}

	public String getStatus() {
		return status;
	}

	public String getTable() {
		return table;
	}

	public String getTime() {
		return time;
	}

}
