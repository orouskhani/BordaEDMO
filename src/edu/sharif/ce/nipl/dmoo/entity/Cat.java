package edu.sharif.ce.nipl.dmoo.entity;


public class Cat {
	private double[] position;
	private double[] vel;
	private double cost;
	private boolean stFlag;
    private int dominatedCounter;
    private double crowdingDistance;
    private int rank;
    private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public Cat(int id , double[] position, double[] vel, double cost, boolean stFlag , int dominatedCounter , double crowdingDistance) {
		super();
		this.id = id;
		this.position = new double[position.length];
		for(int i = 0 ; i < position.length ; i++)
			this.position[i] = position[i];
		
		this.vel = new double[vel.length];
		for(int i = 0 ; i < vel.length ; i++)
			this.vel[i] = vel[i];
		
		this.cost = cost;
		this.stFlag = stFlag;
		
		this.crowdingDistance = crowdingDistance;
		this.dominatedCounter = dominatedCounter;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = new double[position.length];
		for(int i = 0 ; i < this.position.length ; i++)
			this.position[i] = position[i];
	}
	
	public double getCrowdingDistance() {
		return crowdingDistance;
	}

	public void setCrowdingDistance(double cd) {
		this.crowdingDistance = cd;
	}

	public double[] getVel() {
		return vel;
	}

	public void setVel(double[] vel) {
		this.vel = new double[vel.length];
		for(int i = 0 ; i < this.vel.length ; i++)
			this.vel[i] = vel[i];
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public boolean isStFlag() {
		return stFlag;
	}

	public void setStFlag(boolean stFlag) {
		this.stFlag = stFlag;
	}

    @Override
    public Object clone() {
        Cat cat = new Cat(this.id , this.getPosition(), this.getVel(), this.getCost(), this.isStFlag() , this.getDominatedCounter() , this.getCrowdingDistance());
        return cat;

    }

    public int getDominatedCounter() {
        return dominatedCounter;
    }

    public void setDominatedCounter(int dominatedCounter) {
        this.dominatedCounter = dominatedCounter;
    }
}
