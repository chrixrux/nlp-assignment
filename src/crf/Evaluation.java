package crf;

public class Evaluation {
	public double precision;
	public double recall;
	public double f1;
	
	public Evaluation(double precision, double recall, double f1) {
		super();
		this.precision = precision;
		this.recall = recall;
		this.f1 = f1;
	}
}
